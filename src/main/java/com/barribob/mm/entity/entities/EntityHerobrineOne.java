package com.barribob.mm.entity.entities;

import javax.annotation.Nullable;

import com.barribob.mm.entity.action.ActionFireball;
import com.barribob.mm.entity.action.ActionGroundSlash;
import com.barribob.mm.entity.action.ActionSpinSlash;
import com.barribob.mm.entity.action.ActionTeleport;
import com.barribob.mm.entity.action.IAction;
import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.animation.AnimationFireballThrow;
import com.barribob.mm.entity.animation.AnimationHerobrineGroundSlash;
import com.barribob.mm.entity.animation.AnimationSpinSlash;
import com.barribob.mm.entity.projectile.ProjectileFireball;
import com.barribob.mm.entity.projectile.ProjectileHerobrineQuake;
import com.barribob.mm.entity.util.ComboAttack;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityHerobrineOne extends EntityLeveledMob implements RangedAttackMob {
    // Swinging arms is the animation for the attack
    private static final EntityDataAccessor<Boolean> SWINGING_ARMS = SynchedEntityData.<Boolean>defineId(EntityLeveledMob.class, EntityDataSerializers.BOOLEAN);
    private ComboAttack attackHandler = new ComboAttack();
    private byte passiveParticleByte = 7;
    private int maxHits = 3;
    private int hits = 5;
    private byte deathParticleByte = 8;
    public static final byte slashParticleByte = 9;
    private byte fireballParticleByte = 10;
    private boolean markedToDespawn = false;
    private byte spinSlash = 4;
    private byte groundSlash = 5;
    private byte fireball = 6;

    public EntityHerobrineOne(Level worldIn) {
        super(worldIn);
        this.healthScaledAttackFactor = 0.2;
        this.setSize(0.8f, 2.0f);
        if (!level.isClientSide) {
            attackHandler.setAttack(spinSlash, new ActionSpinSlash());
            attackHandler.setAttack(groundSlash, new ActionGroundSlash(() -> new ProjectileHerobrineQuake(level, this, this.getAttack())));
            attackHandler.setAttack(fireball, new IAction() {
                @Override
                public void performAction(EntityLeveledMob actor, LivingEntity target) {
                    actor.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.4F / (actor.level.random.nextFloat() * 0.4F + 0.8F));

                    ProjectileFireball projectile = new ProjectileFireball(actor.level, actor, actor.getAttack(), null);
                    ModUtils.throwProjectile(actor, target, projectile, 2.0f, 0.5f, ModUtils.yVec(0.5f));
                }
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
        attackHandler.setAttack(spinSlash, new ActionSpinSlash(), () -> new AnimationSpinSlash());
        attackHandler.setAttack(groundSlash, new ActionGroundSlash(() -> new ProjectileHerobrineQuake(level, this, this.getAttack())),
                () -> new AnimationHerobrineGroundSlash());
        attackHandler.setAttack(fireball, new ActionFireball(), () -> new AnimationFireballThrow());
        this.currentAnimation = new AnimationSpinSlash();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAIRangedAttack<EntityHerobrineOne>(this, 1.0f, 40, 10.0f, 0.2f));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30.0D);
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (hits == 0) {
            hits = maxHits;
            return super.hurt(source, amount);
        } else if (source.getEntity() instanceof LivingEntity) {
            new ActionTeleport().performAction(this, (LivingEntity) source.getEntity());
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F + ModRandom.getFloat(0.2f));
            this.setRevengeTarget((LivingEntity) source.getEntity());

            hits--;
        }

        return false;
    }

    @Override
    public void die(DamageSource cause) {
        level.broadcastEntityEvent(this, this.deathParticleByte);
        this.setPos(0, 0, 0);
        this.discard();
        super.die(cause);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.attackHandler.getCurrentAttackAction().performAction(this, target);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SWORD_OF_SHADES));
        this.setItemStackToSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.SWORD_OF_SHADES));
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner,
     * natural spawning etc, but not called when entity is reloaded from nbt. Mainly
     * used for initializing attributes and inventory
     */
    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        IEntityLivingData ientitylivingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        return ientitylivingdata;
    }

    @Override
    public void setAggressive(boolean swingingArms) {
        this.entityData.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
        if (swingingArms) {
            float distance = (float) this.getDistanceSq(this.getTarget().posX, getTarget().getBoundingBox().minY, getTarget().posZ);
            float melee_distance = 4;

            if (distance > Math.pow(melee_distance, 2)) {
                attackHandler.setCurrentAttack(rand.nextInt(2) == 0 ? fireball : groundSlash);
            } else {
                attackHandler.setCurrentAttack(spinSlash);

                if (this.getTarget() != null) {
                    Vec3 dir = getTarget().position().subtract(position()).normalize();
                    Vec3 leap = new Vec3(dir.x, 0, dir.z).normalize().scale(0.4f).add(ModUtils.yVec(0.3f));
                    this.motionX += leap.x;
                    if (this.motionY < 0.1) {
                        this.motionY += leap.y;
                    }
                    this.motionZ += leap.z;
                }
            }

            this.level.broadcastEntityEvent(this, attackHandler.getCurrentAttack());

            if (attackHandler.getCurrentAttack() == fireball) {
                this.motionY = 0.7f;
                this.fallDistance = -4;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.markedToDespawn) {
            this.discard();
        }

        if (!this.level.isClientSide && this.isSwingingArms() && attackHandler.getCurrentAttack() == fireball) {
            this.level.broadcastEntityEvent(this, this.fireballParticleByte);
        } else {
            this.level.broadcastEntityEvent(this, this.passiveParticleByte);
        }
    }

    /**
     * Handler for {@link Level#setEntityState}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id >= 4 && id <= 6) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        } else if (id == this.passiveParticleByte) {
            if (random.nextInt(2) == 0) {
                ParticleManager.spawnEffect(level, ModUtils.entityPos(this).add(ModRandom.randVec().scale(1.5f)).add(new Vec3(0, 1, 0)), ModColors.AZURE);
            }
        } else if (id == this.deathParticleByte) {
            int particleAmount = 100;
            for (int i = 0; i < particleAmount; i++) {
                ParticleManager.spawnEffect(this.level, ModUtils.entityPos(this).add(ModRandom.randVec().scale(2f)).add(new Vec3(0, 1, 0)), ModColors.AZURE);
            }
        } else if (id == this.fireballParticleByte) {
            int fireballParticles = 5;
            for (int i = 0; i < fireballParticles; i++) {
                Vec3 pos = new Vec3(ModRandom.getFloat(0.5f), this.getEyeHeight() + 1.0f, ModRandom.getFloat(0.5f)).add(ModUtils.entityPos(this));
                ParticleManager.spawnCustomSmoke(level, pos, ProjectileFireball.FIREBALL_COLOR, Vec3.ZERO);
            }
        } else if (id == EntityHerobrineOne.slashParticleByte) {
            Vec3 color = new Vec3(0.5, 0.2, 0.3);
            float particleHeight = 1.2f;
            for (float r = 0.5f; r <= 2; r += 0.5f) {
                for (float sector = 0; sector < 360; sector += 10) {
                    Vec3 pos = new Vec3(Math.cos(sector) * r, particleHeight, Math.sin(sector) * r).add(ModUtils.entityPos(this));
                    ParticleManager.spawnEffect(level, pos, ModColors.AZURE);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.markedToDespawn = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWINGING_ARMS, Boolean.valueOf(false));
    }

    public boolean isSwingingArms() {
        return this.entityData.get(SWINGING_ARMS).booleanValue();
    }
}
