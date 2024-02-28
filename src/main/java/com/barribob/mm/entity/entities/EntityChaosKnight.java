package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.barribob.mm.Main;
import com.barribob.mm.entity.EntityCrimsonPortalSpawn;
import com.barribob.mm.entity.ai.EntityAITimedAttack;
import com.barribob.mm.entity.projectile.ProjectileChaosFireball;
import com.barribob.mm.entity.util.DirectionalRender;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.packets.MessageDirectionForRender;
import com.barribob.mm.renderer.ITarget;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;
import com.barribob.mm.world.gen.nexus.WorldGenNexusTeleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityChaosKnight extends EntityMaelstromMob implements IAttack, DirectionalRender, ITarget {
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.NOTCHED_6));
    private Vec3 chargeDir;
    private static final float dashRadius = 2;
    private Consumer<LivingEntity> prevAttack;

    private final Consumer<LivingEntity> sideSwipe = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.single_slash", false);
        addEvent(() -> {
            float distance = distanceTo(target);
            if (distance > 2) {
                ModUtils.leapTowards(this, target.position(), (float) (0.45 * Math.sqrt(distance)), 0.5f);
            }
        }, 5);

        addEvent(() -> {
            Vec3 offset = position().add(ModUtils.getRelativeOffset(this, new Vec3(0.5, 1, -1)));
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MOB)
                    .directEntity(this)
                    .element(getElement())
                    .disablesShields().build();
            float damage = getAttack() * getConfigFloat("single_swipe_damage");

            ModUtils.handleAreaImpact(2, (e) -> damage, this, offset, source, 0.5f, 0, false);
            swipeBlocks();

            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
            Vec3 away = this.position().subtract(target.position()).normalize();
            ModUtils.leapTowards(this, away, 0.4f, 0.4f);
        }, 18);

        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 35);
    };

    private void swipeBlocks() {
        double swipeWidth = getMobConfig().getDouble("swipe_width");
        AABB box = new AABB(blockPosition()).move(-0.5, 1.5, -0.5).inflate(swipeWidth, 1, swipeWidth);
        ModUtils.destroyBlocksInAABB(box, level, this);
    }

    private final Consumer<LivingEntity> leapSlam = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.leap_slam", false);
        addEvent(() -> {
            ModUtils.leapTowards(this, target.position(), (float) (0.45f * Math.sqrt(distanceTo(target))), 0.9f);
            setLeaping(true);
        }, 20);
        addEvent(() -> {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.EXPLOSION)
                    .directEntity(this)
                    .element(getElement())
                    .stoppedByArmorNotShields().build();

            Vec3 pos = this.position().add(ModUtils.getRelativeOffset(this, new Vec3(1, 0, 0)));
            float damage = this.getAttack() * getConfigFloat("leap_slam_damage");
            ModUtils.handleAreaImpact(3, (e) -> damage, this, pos, source);
            Explosion.BlockInteraction interaction = ModUtils.mobGriefing(this.level, this) ? BlockInteraction.DESTROY : BlockInteraction.NONE;
            this.level.explode(this, pos.x, pos.y + 1, pos.z, (float) getMobConfig().getDouble("slam_explosion_strength"), false, interaction);
            this.level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }, 42);
        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 60);
    };

    private final Consumer<LivingEntity> dash = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.dash", false);
        Vec3 targetPos = getTarget().position().add(ModUtils.yVec(1));
        Vec3 startPos = position().add(ModUtils.yVec(getEyeHeight()));
        Vec3 dir = targetPos.subtract(startPos).normalize();

        AtomicReference<Vec3> teleportPos = new AtomicReference<>(targetPos);
        int maxDistance = 10;
        ModUtils.lineCallback(
                targetPos.add(dir),
                targetPos.add(dir.scale(maxDistance)),
                maxDistance * 2,
                (pos, i) -> {
            boolean safeLanding = ModUtils.cubePoints(0, -2, 0, 1, 0, 1).stream()
                    .anyMatch(off -> level.getBlockState(new BlockPos(pos.add(off)))
                            .isFaceSturdy(level, new BlockPos(pos.add(off)).below(), Direction.UP));
            boolean notOpen = ModUtils.cubePoints(0, 1, 0, 1, 3, 1).stream()
                    .anyMatch(off -> level.getBlockState(new BlockPos(pos.add(off)))
                    		.isSuffocating(this.level, new BlockPos(pos.add(off))));

            if (safeLanding && !notOpen) {
                teleportPos.set(pos);
            }
        });

        this.chargeDir = teleportPos.get();

        // Send the aimed position to the client side
        Main.NETWORK.sendToAllTracking(new MessageDirectionForRender(this, this.chargeDir), this);

        addEvent(() -> {
            level.explode(this, getX(), getY(), getZ(), 2, BlockInteraction.NONE);
            ModUtils.lineCallback(startPos, chargeDir, (int) Math.sqrt(chargeDir.subtract(startPos).lengthSqr()), (vec, i) -> {
                DamageSource source = ModDamageSource.builder()
                        .type(ModDamageSource.MOB)
                        .directEntity(this)
                        .element(getElement())
                        .stoppedByArmorNotShields().build();
                float damage = getAttack() * getConfigFloat("dash_damage");
                ModUtils.handleAreaImpact(dashRadius, (e) -> damage, this, vec, source, 0.3f, 5);
                ModUtils.destroyBlocksInAABB(this.getBoundingBox().move(position().scale(-1)).move(vec), level, this);
                level.playLocalSound(vec.x, vec.y, vec.z, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 1.0f, 1.0f + ModRandom.getFloat(0.1f), false);
            });
            this.moveTo(chargeDir.x, chargeDir.y, chargeDir.z);
            level.broadcastEntityEvent(this, ModUtils.SECOND_PARTICLE_BYTE);
            playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f + ModRandom.getFloat(0.1f));
        }, 20);

        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 40);
    };

    private final Consumer<LivingEntity> spinSlash = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.triple_slash", false);
        Runnable leap = () -> {
            ModUtils.leapTowards(this, target.position(), (float) (0.35f * Math.sqrt(distanceTo(target))), 0.5f);
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0);
        };
        Runnable meleeAttack = () -> {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MOB)
                    .directEntity(this)
                    .element(getElement())
                    .disablesShields().build();

            float damage = getAttack() * getConfigFloat("spin_slash_damage");
            ModUtils.handleAreaImpact(2.7f, (e) -> damage, this, position().add(ModUtils.yVec(1)), source, 0.5f, 0, false);
            swipeBlocks();

            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        };

        addEvent(leap, 10);
        addEvent(meleeAttack, 15);
        addEvent(leap, 23);
        addEvent(meleeAttack, 29);
        addEvent(leap, 34);
        addEvent(meleeAttack, 41);
        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 60);
    };

    private final Consumer<LivingEntity> summonMeteors = (target) -> {
        addEvent(() -> this.setDeltaMovement(this.getDeltaMovement().add(0, 0.5, 0)), 3);
        ModBBAnimations.animation(this, "chaos_knight.summon", false);
        for (int tick = 20; tick < 140; tick += 5) {
            addEvent(() -> {
                float damage = this.getAttack() * getConfigFloat("meteor_damage");
                ProjectileChaosFireball meteor = new ProjectileChaosFireball(level, this, damage, null);
                Vec3 pos = new Vec3(ModRandom.getFloat(10), ModRandom.getFloat(1), ModRandom.getFloat(10)).add(this.position()).add(ModUtils.yVec(13));
                Vec3 targetPos = new Vec3(ModRandom.getFloat(5), 0, ModRandom.getFloat(5)).add(target.position());
                Vec3 vel = targetPos.subtract(pos).normalize().scale(0.4);
                meteor.setPos(pos.x, pos.y, pos.z);
                meteor.shoot(this, 90, 0, 0.0F, 0.0f, 0);
                ModUtils.setEntityVelocity(meteor, vel);
                meteor.setTravelRange(20f);
                level.addFreshEntity(meteor);
            }, tick);
        }
        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 30);
        addEvent(() -> {
            level.broadcastEntityEvent(this, ModUtils.THIRD_PARTICLE_BYTE);
            this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0f, 1.0f * ModRandom.getFloat(0.2f));
        }, 12);
    };

    public EntityChaosKnight(Level worldIn) {
        super(worldIn);
        // Using this attribute to teleport the knight back if it falls off the tower it spawns on
        this.setImmovable(true);
        this.setSize(1.5f, 3.0f);
        this.healthScaledAttackFactor = 0.2;
    }

    @Override
    public void tick() {
        super.tick();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        if(!level.isClientSide && this.isLeaping()) {
            AABB box = getBoundingBox().inflate(0.25, 0.12, 0.25).move(0, 0.12, 0);
            ModUtils.destroyBlocksInAABB(box, level, this);
        }

        if (level.isClientSide || this.tickCount % 5 != 0) {
            return;
        }

        boolean hasGround = false;
        for (int i = 0; i > -10; i--) {
            if (!level.isEmptyBlock(blockPosition().offset(new BlockPos(0, i, 0)))) {
                hasGround = true;
            }
        }

        if (!hasGround && this.getDeltaMovement().y < -1) {
            this.setImmovable(true);
        } else if (this.isImmovable()) {
            this.setImmovable(false);
        }
    }

    @Override
    public int startAttack(LivingEntity target, float distanceFactor, boolean strafingBackwards) {
        float healthRatio = this.getHealth() / this.getMaxHealth();
        setSwingingArms(true);
        double distance = Math.sqrt(distanceFactor);
        List<Consumer<LivingEntity>> attacks = new ArrayList<>(Arrays.asList(sideSwipe, leapSlam, dash, spinSlash, summonMeteors));
        double[] weights = {
                (1 - (distance / 10)) * (prevAttack != sideSwipe ? 1.5 : 1.0), // Swipe
                0.2 + 0.04 * distance, // Leap
                healthRatio < 0.7 ? 0.2 + 0.04 * distance : 0, // Dash
                0.5 - (prevAttack == spinSlash ? 0.3 : 0.0), // Spin slash
                prevAttack == summonMeteors || healthRatio > 0.5 ? 0.0 : (1 - healthRatio) // Meteors
        };

        prevAttack = ModRandom.choice(attacks, random, weights).next();
        prevAttack.accept(target);
        return prevAttack == sideSwipe || prevAttack == summonMeteors ? 50 : 90 - (int) (10 * (1 - healthRatio));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (amount > 0.0F && this.canBlockDamageSource(source)) {
            this.hurtCurrentlyUsedShield(amount);

            if (!source.isProjectile()) {
                Entity entity = source.getDirectEntity();

                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            this.playSound(SoundsHandler.ENTITY_CHAOS_KNIGHT_BLOCK, 1.0f, 0.9f + ModRandom.getFloat(0.2f));

            return false;
        }
        return super.hurt(source, amount);
    }

    private boolean canBlockDamageSource(DamageSource damageSourceIn) {
        if (!damageSourceIn.isBypassArmor() && !this.isSwingingArms()) {
            Vec3 vec3d = damageSourceIn.getSourcePosition();

            if (vec3d != null) {
                Vec3 vec3d1 = this.getViewVector(1.0F);
                Vec3 vec3d2 = vec3d.subtract(this.position()).normalize();
                vec3d2 = new Vec3(vec3d2.x, 0.0D, vec3d2.z);

                return vec3d2.dot(vec3d1) < 0.0D;
            }
        }

        return false;
    }

    @Override
    public float getEyeHeight() {
        return this.getBbHeight() * 0.8f;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAITimedAttack<>(this, 1.0f, 60, 15, 0.5f));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            for (int r = 1; r < 3; r++) {
                ModUtils.circleCallback(r, r * 20, (pos) -> {
                    pos = new Vec3(pos.x, 0, pos.y);
                    ParticleManager.spawnSplit(level, pos.add(this.position().add(ModUtils.getRelativeOffset(this, new Vec3(1, 0, 0))).add(ModUtils.yVec(-1.5f))), ModColors.RED, pos.scale(0.1f).add(ModUtils.yVec(0.05f)));
                });
            }
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            if (chargeDir != null) {
                Vec3 particleVel = chargeDir.subtract(position()).normalize().scale(0.5);
                ModUtils.lineCallback(position(), chargeDir, 20, (vec, i) -> ModUtils.performNTimes(10, (j) -> {
                    ParticleManager.spawnSplit(level, vec.add(ModRandom.randVec().scale(dashRadius * 2)), ModColors.RED, particleVel.add(ModRandom.randVec().scale(0.2f)));
                    ParticleManager.spawnCustomSmoke(level, vec.add(ModRandom.randVec().scale(dashRadius * 2)), ModColors.GREY, particleVel.add(ModRandom.randVec().scale(0.2f)));
                    Vec3 flamePos = vec.add(ModRandom.randVec().scale(dashRadius * 2));
                    Vec3 flameVel = particleVel.add(ModRandom.randVec().scale(0.2f));
                    level.addParticle(ParticleTypes.FLAME, flamePos.x, flamePos.y, flamePos.z, flameVel.x, flameVel.y, flameVel.z);
                }));
                this.chargeDir = null; // So that the lazer doesn't render anymore
            }
        } else if (id == ModUtils.THIRD_PARTICLE_BYTE) {
            ModUtils.circleCallback(2, 50, (pos) -> {
                pos = new Vec3(pos.x, 0, pos.y);
                ParticleManager.spawnDust(level, pos.add(this.position()).add(ModUtils.yVec(5)), ModColors.RED, pos.normalize().scale(0.3).add(ModUtils.yVec(0.1)), ModRandom.range(20, 30));
            });
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void setRenderDirection(Vec3 lazerDir) {
        this.chargeDir = lazerDir;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!level.isClientSide && this.level.dimension() == ModDimensions.NEXUS.getId() && this.getMobLevel() > 0) {
            // Spawn portal entity
            Vec3 origin = this.getInitialPosition();
            EntityCrimsonPortalSpawn spawner = new EntityCrimsonPortalSpawn(level, origin.x, origin.y, origin.z);
            level.addFreshEntity(spawner);

            // Spawn nexus teleporters
            BlockPos upperTeleporterPos = new BlockPos(origin).east(14).below().north();
            BlockPos lowerTeleporterPos = upperTeleporterPos.offset(0, -81, -2);
            new WorldGenNexusTeleporter(new Vec3(-1, -83, 2)).generate(level, random, upperTeleporterPos, Rotation.NONE);
            new WorldGenNexusTeleporter(new Vec3(-8, 81, 3)).generate(level, random, lowerTeleporterPos, Rotation.CLOCKWISE_90);
        }
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
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_CHAOS_KNIGHT_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_CHAOS_KNIGHT_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_CHAOS_KNIGHT_HURT;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void initAnimation() {
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }

    @Override
    public int getBrightnessForRender() {
        return Math.min(super.getBrightnessForRender() + 60, 200);
    }

    @Override
    public Optional<Vec3> getLazerTarget() {
        return Optional.ofNullable(this.chargeDir);
    }
}
