package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.barribob.mm.Main;
import com.barribob.mm.entity.action.IAction;
import com.barribob.mm.entity.ai.EntityAITimedAttack;
import com.barribob.mm.entity.animation.AnimationClip;
import com.barribob.mm.entity.animation.AnimationNone;
import com.barribob.mm.entity.animation.StreamAnimation;
import com.barribob.mm.entity.model.ModelMonolith;
import com.barribob.mm.entity.projectile.ProjectileMaelstromMeteor;
import com.barribob.mm.entity.projectile.ProjectileMonolithFireball;
import com.barribob.mm.entity.util.ComboAttack;
import com.barribob.mm.entity.util.DirectionalRender;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.packets.MessageDirectionForRender;
import com.barribob.mm.renderer.ITarget;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EntityMonolith extends EntityMaelstromMob implements IAttack, DirectionalRender, ITarget {
    private ComboAttack attackHandler = new ComboAttack();
    public static final byte noAttack = 0;
    public static final byte blueAttack = 4;
    public static final byte redAttack = 5;
    public static final byte yellowAttack = 6;
    private byte stageTransform = 7;
    private static final EntityDataAccessor<Boolean> TRANSFORMED = SynchedEntityData.<Boolean>createKey(EntityMonolith.class, EntityDataSerializers.BOOLEAN);
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.NOTCHED_6));

    // Field to store the lazer's aimed direction
    private Vec3 lazerDir;
    private float lazerRadius = 2.5f;

    // Datamanager to keep track of which attack the mob is doing
    private static final EntityDataAccessor<Byte> ATTACK = SynchedEntityData.<Byte>createKey(EntityMonolith.class, EntityDataSerializers.BYTE);

    public EntityMonolith(Level worldIn) {
        super(worldIn);
        this.setImmovable(true);
        this.setNoGravity(true);
        this.setSize(2.2f, 4.5f);
        this.healthScaledAttackFactor = 0.2;
        this.isImmuneToFire = true;

        BiConsumer<EntityLeveledMob, LivingEntity> fireballs = (EntityLeveledMob actor, LivingEntity target) -> {
            ModUtils.performNTimes(3, (i) -> spawnFireball(actor, target, this::getRandomFireballPosition));
            spawnFireball(actor, target, this::getPositionAboveTarget);
        };

        BiConsumer<EntityLeveledMob, LivingEntity> lazer = (EntityLeveledMob actor, LivingEntity target) -> {
            actor.playSound(SoundEvents.BLAZE_SHOOT, 1.5F, 0.4F / (actor.level.random.nextFloat() * 0.4F + 0.8F));

            float numParticles = 10;
            Vec3 dir = lazerDir.subtract(actor.position().add(ModUtils.yVec(actor.getEyeHeight()))).scale(1 / numParticles);
            Vec3 currentPos = actor.position().add(ModUtils.yVec(actor.getEyeHeight()));
            for (int i = 0; i < numParticles; i++) {

                DamageSource source = ModDamageSource.builder()
                        .type(ModDamageSource.EXPLOSION)
                        .directEntity(actor)
                        .stoppedByArmorNotShields()
                        .element(getElement()).build();

                ModUtils.handleAreaImpact(lazerRadius, (e) -> actor.getAttack() * actor.getConfigFloat("laser_damage"), actor, currentPos, source, 0.5f,
                        5, false);
                currentPos = currentPos.add(dir);
                for (int j = 0; j < 20; j++) {
                    Vec3 pos = currentPos.add(ModRandom.randVec().scale(lazerRadius));
                    if (level.isBlockFullCube(new BlockPos(pos).below()) && level.isEmptyBlock(new BlockPos(pos))) {
                        level.setBlockAndUpdate(new BlockPos(pos), Blocks.FIRE.defaultBlockState());
                    }
                }
            }
            level.broadcastEntityEvent(actor, ModUtils.FOURTH_PARTICLE_BYTE);
        };

        if (!level.isClientSide) {
            attackHandler.setAttack(blueAttack, new IAction() {
                @Override
                public void performAction(EntityLeveledMob actor, LivingEntity target) {
                    int numMobs = getMobConfig().getInt("summoning_algorithm.mobs_per_spawn");
                    for (int i = 0; i < numMobs; i++) {
                        ModUtils.spawnMob(level, blockPosition(), getMobLevel(), getMobConfig().getConfig("summoning_algorithm"));
                    }
                }
            });
            attackHandler.setAttack(redAttack, fireballs);
            attackHandler.setAttack(yellowAttack, (IAction) (actor, target) -> {
                DamageSource source = ModDamageSource.builder()
                        .type(ModDamageSource.MAGIC)
                        .directEntity(actor)
                        .stoppedByArmorNotShields()
                        .element(getElement()).build();

                ModUtils.handleAreaImpact(7, (e) -> getAttack() * getConfigFloat("defensive_burst_damage"), actor, position(), source, 2.0f, 0, true);
                actor.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0f, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
                actor.level.broadcastEntityEvent(actor, ModUtils.SECOND_PARTICLE_BYTE);
            });
            attackHandler.setAttack(stageTransform, new IAction() {
                // Change the yellow and blue attacks to new attacks
                @Override
                public void performAction(EntityLeveledMob actor, LivingEntity target) {
                    actor.getEntityData().set(TRANSFORMED, Boolean.valueOf(true));
                    attackHandler.setAttack(yellowAttack, (IAction) (actor1, target1) -> {
                        actor1.motionY = 0;
                        actor1.setImmovable(false);
                        actor1.setNoGravity(false);
                        Vec3 pos = target1.position().add(target1.getLookAngle()).add(ModUtils.yVec(24))
                                .add(new Vec3(ModRandom.getFloat(1), 0, ModRandom.getFloat(1)));
                        actor1.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        actor1.setPos(pos.x, pos.y, pos.z);
                        EntityMonolith.this.setLeaping(true);
                    });
                    attackHandler.setAttack(redAttack, lazer);
                }
            });
        }
    }

    public void spawnFireball(EntityLeveledMob actor, LivingEntity target, Function<LivingEntity, Vec3> getPosition) {
        ProjectileMonolithFireball meteor = new ProjectileMonolithFireball(level, actor, actor.getAttack() * actor.getConfigFloat("fireball_damage"), null);
        Vec3 pos = getPosition.apply(target);
        meteor.setPos(pos.x, pos.y, pos.z);
        meteor.shoot(actor, 90, 0, 0.0F, 0.5f, 0);
        meteor.motionX -= actor.motionX;
        meteor.motionZ -= actor.motionZ;
        meteor.setTravelRange(100f);
        level.addFreshEntity(meteor);
    }

    private Vec3 getRandomFireballPosition(LivingEntity target) {
        return ModRandom.randFlatVec(ModUtils.Y_AXIS)
                .scale(ModRandom.range(4, 5))
                .add(target.position())
                .add(ModUtils.yVec(ModRandom.range(15, 20)));
    }

    private Vec3 getPositionAboveTarget(LivingEntity target) {
        return target.position()
                .add(ModUtils.yVec(ModRandom.range(15, 20)));
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return this.getBbHeight() * 0.8f;
    }

    @Override
    protected void initAnimation() {
        List<List<AnimationClip<ModelMonolith>>> animationStage2 = new ArrayList<List<AnimationClip<ModelMonolith>>>();
        List<AnimationClip<ModelMonolith>> middle = new ArrayList<AnimationClip<ModelMonolith>>();

        BiConsumer<ModelMonolith, Float> resize = (model, f) -> {
            f *= 35f;

            model.shell3.rotateAngleX = 0.1f;
            model.body1.rotateAngleX = 0.1f;
            model.body2.rotateAngleX = 0.1f;

            model.shell3 = new ModelRenderer(model);
            model.shell3.setRotationPoint(0.0F, 24.0F, 0.0F);
            model.shell3.cubeList.add(new ModelBox(model.shell3, 116, 116, -4.0F, -71.0F + f.intValue(), -11.0F, 8, 61 - f.intValue(), 22, 0.0F, false));

            model.body1 = new ModelRenderer(model);
            model.body1.setRotationPoint(-6.0F, 24.0F, 0.0F);
            model.body1.cubeList.add(new ModelBox(model.body1, 94, 0, -5.0F, -69.0F + f.intValue(), -8.0F, 7, 62 - f.intValue(), 16, 0.0F, false));

            model.body2 = new ModelRenderer(model);
            model.body2.setRotationPoint(7.0F, 24.0F, 0.0F);
            model.body2.cubeList.add(new ModelBox(model.body2, 0, 95, -3.0F, -65.0F + f.intValue(), -8.0F, 6, 56 - f.intValue(), 16, 0.0F, false));
        };

        middle.add(new AnimationClip(40, 0, (float) Math.toDegrees(Math.PI / 3), resize));

        animationStage2.add(middle);
        attackHandler.setAttack(stageTransform, IAction.NONE, () -> new StreamAnimation(animationStage2));
        attackHandler.setAttack(blueAttack, IAction.NONE, () -> new AnimationNone());
        attackHandler.setAttack(redAttack, IAction.NONE, () -> new AnimationNone());
        attackHandler.setAttack(yellowAttack, IAction.NONE, () -> new AnimationNone());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAITimedAttack<EntityMonolith>(this, 0, 90, 30, 0, 30.0f));
    }

    public Optional<Vec3> getLazerTarget() {
        if (isTransformed() && getAttackColor() == redAttack && this.lazerDir != null) {
            return Optional.of(this.lazerDir);
        }
        return Optional.empty();
    }

    public boolean isTransformed() {
        return this.entityData.get(TRANSFORMED);
    }

    @Override
    public void tick() {
        super.tick();
        this.setRot(0, 0);
        this.setYHeadRot(0);

        if (!level.isClientSide && this.getTarget() == null) {
            this.entityData.set(ATTACK, noAttack);
        }

        // When is is "moving" make sure it still feels immovable
        // TODO: this doesn't always work on the client side
        if (!this.isImmovable()) {
            this.motionX = 0;
            this.motionZ = 0;
        }

        // Spawn a maelstrom splotch nearby
        int maelstromMeteorTime = 1200;
        int maxMeteors = 20;
        if (!level.isClientSide && this.getTarget() == null && this.tickCount % maelstromMeteorTime == 0 && this.tickCount < maelstromMeteorTime * maxMeteors) {
            ProjectileMaelstromMeteor meteor = new ProjectileMaelstromMeteor(level, this, this.getAttack());
            Vec3 pos = new Vec3(ModRandom.getFloat(1.0f), 0, ModRandom.getFloat(1.0f)).normalize().scale(ModRandom.range(20, 50)).add(this.position());
            meteor.setPos(pos.x, pos.y, pos.z);
            meteor.shoot(this, 90, 0, 0.0F, 0.7f, 0);
            meteor.motionX -= this.motionX;
            meteor.motionZ -= this.motionZ;
            meteor.setTravelRange(100f);
            level.addFreshEntity(meteor);
        }

        level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id >= 4 && id <= 7) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        } else if (id == ModUtils.PARTICLE_BYTE) {
            Vec3 particlePos = position().add(ModUtils.yVec(2)).add(ModRandom.randVec().scale(4));
            switch (this.getAttackColor()) {
                case noAttack:
                    ParticleManager.spawnMaelstromSmoke(level, random, particlePos, false);
                    break;
                case blueAttack:
                    ParticleManager.spawnEffect(level, particlePos, ModColors.BLUE);
                    break;
                case redAttack:
                    ParticleManager.spawnEffect(level, particlePos, ModColors.RED);
                    break;
                case yellowAttack:
                    ParticleManager.spawnEffect(level, particlePos, ModColors.YELLOW);
            }
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            ModUtils.performNTimes(4, (i) -> {
                ModUtils.circleCallback(2, 60, (pos) -> {
                    pos = new Vec3(pos.x, 0, pos.y);
                    ParticleManager.spawnFirework(level, pos.add(position()).add(ModUtils.yVec(i + 1)), ModColors.YELLOW, pos.scale(0.5f));
                });
            });
        } else if (id == ModUtils.THIRD_PARTICLE_BYTE) {
            ModUtils.performNTimes(100, (i) -> {
                this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + ModRandom.getFloat(5), this.getY() + ModRandom.getFloat(5),
                        this.getZ() + ModRandom.getFloat(5), 0, 0, 0);
            });
        } else if (id == ModUtils.FOURTH_PARTICLE_BYTE) {
            if (lazerDir != null) {
                float numParticles = 10;
                Vec3 dir = lazerDir.subtract(this.position().add(ModUtils.yVec(this.getEyeHeight()))).scale(1 / numParticles);
                Vec3 currentPos = this.position().add(ModUtils.yVec(this.getEyeHeight()));
                for (int i = 0; i < numParticles; i++) {
                    for (int j = 0; j < 50; j++) {
                        ParticleManager.spawnWisp(level, currentPos.add(ModRandom.randVec().scale(lazerRadius * 2)), ModColors.RED, ModUtils.yVec(0.1f));
                        Vec3 pos = currentPos.add(ModRandom.randVec().scale(lazerRadius * 2));
                        level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 0, 0, 0);
                        pos = currentPos.add(ModRandom.randVec().scale(lazerRadius * 2));
                        level.addParticle(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 0, 0, 0);
                    }
                    currentPos = currentPos.add(dir);
                }
            }
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void onStopLeaping() {
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.EXPLOSION)
                .directEntity(this)
                .stoppedByArmorNotShields()
                .element(getElement()).build();

        ModUtils.handleAreaImpact(5, (e) -> this.getAttack() * getConfigFloat("fall_damage"), this, this.position().add(ModUtils.yVec(1)), source);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0f, 1.0f + ModRandom.getFloat(0.1f));
        this.level.broadcastEntityEvent(this, ModUtils.THIRD_PARTICLE_BYTE);
        addEvent(() -> {
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            this.setImmovable(true);
            this.setNoGravity(false);
        }, 20);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.FALL) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void setRenderDirection(Vec3 lazerDir) {
        this.lazerDir = lazerDir;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK, noAttack);
        this.entityData.define(TRANSFORMED, Boolean.FALSE);
    }

    public byte getAttackColor() {
        return this.entityData.get(ATTACK);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.METAL_PLACE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_EXPLODE;
    }

    @Override
    public void writeEntityToNBT(CompoundTag compound) {
        // Make sure we save as immovable to avoid some weird states
        if (!this.isImmovable()) {
            this.setImmovable(true);
            this.setPos(0, 0, 0);// Setting any position teleports it back to the initial position
        }
        super.writeEntityToNBT(compound);
    }

    @Override
    public void die(DamageSource cause) {
        level.broadcastEntityEvent(this, ModUtils.THIRD_PARTICLE_BYTE); // Explode on death

        level.setBlockAndUpdate(blockPosition().below(2), ModBlocks.MAELSTROM_HEART.defaultBlockState());

        if(getMobConfig().getBoolean("spawn_nexus_portal_on_death")) {
            // Spawn the second half of the boss
            EntityWhiteMonolith boss = new EntityWhiteMonolith(level);
            boss.copyPosition(this);
            boss.setYHeadRot(this.yHeadRot);
            if (!level.isClientSide) {
                level.addFreshEntity(boss);
            }

            // Teleport away so that the player doens't see the death animation
            this.setImmovable(false);
            this.setPos(0, 0, 0);
        }

        ModUtils.getEntitiesInBox(this, getBoundingBox().inflate(15, 2, 15)).stream().filter(EntityMaelstromMob::isMaelstromMob).forEach((e) -> {
            e.invulnerableTime = 0;
            e.hurt(DamageSource.MAGIC, 50);
        });

        super.die(cause);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public int startAttack(LivingEntity target, float distanceSq, boolean strafingBackwards) {
        this.playSound(SoundsHandler.ENTITY_MONOLITH_AMBIENT.get(), 0.7f, 1.0f * ModRandom.getFloat(0.2f));

        chooseAttack();

        level.broadcastEntityEvent(this, attackHandler.getCurrentAttack());
        this.entityData.set(ATTACK, attackHandler.getCurrentAttack());

        addEvent(() -> {
            this.attackHandler.getCurrentAttackAction().performAction(this, target);
            this.entityData.set(ATTACK, noAttack);
        }, 40);

        int attackCooldown = this.attackHandler.getCurrentAttack() == yellowAttack && this.isTransformed() ? 120 : 90;

        return attackCooldown - (int) (30 * (1 - (this.getHealth() / this.getMaxHealth())));
    }

    public void chooseAttack() {
        int numMinions = (int) ModUtils.getEntitiesInBox(this, getBoundingBox().inflate(10, 2, 10)).stream().filter(EntityMaelstromMob::isMaelstromMob).count();

        double yellowWeight = 0.0;
        if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 6) {
            yellowWeight = 1.0; // Likely to use yellow attack if the player is near
        } else if (isTransformed()) {
            yellowWeight = 0.3;
        }

        Byte[] attack = {blueAttack, redAttack, yellowAttack};
        double[] weights = {numMinions == 0 ? 0.8 : 0.1, 0.5, yellowWeight};
        attackHandler.setCurrentAttack(ModRandom.choice(attack, random, weights).next());

        if (!isTransformed() && this.getHealth() < getMobConfig().getInt("second_boss_phase_hp")) {
            attackHandler.setCurrentAttack(stageTransform);
        }

        // Initialize the lazer
        if (isTransformed() && attackHandler.getCurrentAttack() == redAttack && this.getTarget() != null) {
            this.lazerDir = getTarget().position().add(ModUtils.yVec(this.getEyeHeight()))
                    .subtract(position().add(ModUtils.yVec(this.getEyeHeight()))).normalize().scale(20).add(position());

            // Send the aimed position to the client side
            Main.NETWORK.sendToAllTracking(new MessageDirectionForRender(this, this.lazerDir), this);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }
}
