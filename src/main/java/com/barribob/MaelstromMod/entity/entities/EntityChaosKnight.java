package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.entity.EntityCrimsonPortalSpawn;
import com.barribob.MaelstromMod.entity.ai.EntityAITimedAttack;
import com.barribob.MaelstromMod.entity.projectile.ProjectileChaosFireball;
import com.barribob.MaelstromMod.entity.util.DirectionalRender;
import com.barribob.MaelstromMod.entity.util.IAttack;
import com.barribob.MaelstromMod.init.ModBBAnimations;
import com.barribob.MaelstromMod.init.ModDimensions;
import com.barribob.MaelstromMod.packets.MessageDirectionForRender;
import com.barribob.MaelstromMod.renderer.ITarget;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import com.barribob.MaelstromMod.world.gen.nexus.WorldGenNexusTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class EntityChaosKnight extends EntityMaelstromMob implements IAttack, DirectionalRender, ITarget {
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossEvent.Color.RED, BossEvent.Overlay.NOTCHED_6));
    private Vec3 chargeDir;
    private static final float dashRadius = 2;
    private Consumer<LivingEntity> prevAttack;

    private final Consumer<LivingEntity> sideSwipe = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.single_slash", false);
        addEvent(() -> {
            float distance = getDistance(target);
            if (distance > 2) {
                ModUtils.leapTowards(this, target.getPositionVector(), (float) (0.45 * Math.sqrt(distance)), 0.5f);
            }
        }, 5);

        addEvent(() -> {
            Vec3 offset = getPositionVector().add(ModUtils.getRelativeOffset(this, new Vec3(0.5, 1, -1)));
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MOB)
                    .directEntity(this)
                    .element(getElement())
                    .disablesShields().build();
            float damage = getAttack() * getConfigFloat("single_swipe_damage");

            ModUtils.handleAreaImpact(2, (e) -> damage, this, offset, source, 0.5f, 0, false);
            swipeBlocks();

            playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
            Vec3 away = this.getPositionVector().subtract(target.getPositionVector()).normalize();
            ModUtils.leapTowards(this, away, 0.4f, 0.4f);
        }, 18);

        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 35);
    };

    private void swipeBlocks() {
        double swipeWidth = getMobConfig().getDouble("swipe_width");
        AABB box = new AABB(getPosition()).offset(-0.5, 1.5, -0.5).grow(swipeWidth, 1, swipeWidth);
        ModUtils.destroyBlocksInAABB(box, world, this);
    }

    private final Consumer<LivingEntity> leapSlam = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.leap_slam", false);
        addEvent(() -> {
            ModUtils.leapTowards(this, target.getPositionVector(), (float) (0.45f * Math.sqrt(getDistance(target))), 0.9f);
            setLeaping(true);
        }, 20);
        addEvent(() -> {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.EXPLOSION)
                    .directEntity(this)
                    .element(getElement())
                    .stoppedByArmorNotShields().build();

            Vec3 pos = this.getPositionVector().add(ModUtils.getRelativeOffset(this, new Vec3(1, 0, 0)));
            float damage = this.getAttack() * getConfigFloat("leap_slam_damage");
            ModUtils.handleAreaImpact(3, (e) -> damage, this, pos, source);
            this.world.newExplosion(this, pos.x, pos.y + 1, pos.z, (float) getMobConfig().getDouble("slam_explosion_strength"), false, true);
            this.world.setEntityState(this, ModUtils.PARTICLE_BYTE);
        }, 42);
        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 60);
    };

    private final Consumer<LivingEntity> dash = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.dash", false);
        Vec3 targetPos = getAttackTarget().getPositionVector().add(ModUtils.yVec(1));
        Vec3 startPos = getPositionVector().add(ModUtils.yVec(getEyeHeight()));
        Vec3 dir = targetPos.subtract(startPos).normalize();

        AtomicReference<Vec3> teleportPos = new AtomicReference<>(targetPos);
        int maxDistance = 10;
        ModUtils.lineCallback(
                targetPos.add(dir),
                targetPos.add(dir.scale(maxDistance)),
                maxDistance * 2,
                (pos, i) -> {
            boolean safeLanding = ModUtils.cubePoints(0, -2, 0, 1, 0, 1).stream()
                    .anyMatch(off -> world.getBlockState(new BlockPos(pos.add(off)))
                            .isSideSolid(world, new BlockPos(pos.add(off)).down(), Direction.UP));
            boolean notOpen = ModUtils.cubePoints(0, 1, 0, 1, 3, 1).stream()
                    .anyMatch(off -> world.getBlockState(new BlockPos(pos.add(off)))
                            .causesSuffocation());

            if (safeLanding && !notOpen) {
                teleportPos.set(pos);
            }
        });

        this.chargeDir = teleportPos.get();

        // Send the aimed position to the client side
        Main.network.sendToAllTracking(new MessageDirectionForRender(this, this.chargeDir), this);

        addEvent(() -> {
            world.createExplosion(this, posX, posY, posZ, 2, false);
            ModUtils.lineCallback(startPos, chargeDir, (int) Math.sqrt(chargeDir.subtract(startPos).lengthSquared()), (vec, i) -> {
                DamageSource source = ModDamageSource.builder()
                        .type(ModDamageSource.MOB)
                        .directEntity(this)
                        .element(getElement())
                        .stoppedByArmorNotShields().build();
                float damage = getAttack() * getConfigFloat("dash_damage");
                ModUtils.handleAreaImpact(dashRadius, (e) -> damage, this, vec, source, 0.3f, 5);
                ModUtils.destroyBlocksInAABB(this.getEntityBoundingBox().offset(getPositionVector().scale(-1)).offset(vec), world, this);
                world.playSound(vec.x, vec.y, vec.z, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.HOSTILE, 1.0f, 1.0f + ModRandom.getFloat(0.1f), false);
            });
            this.setPositionAndUpdate(chargeDir.x, chargeDir.y, chargeDir.z);
            world.setEntityState(this, ModUtils.SECOND_PARTICLE_BYTE);
            playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0f, 1.0f + ModRandom.getFloat(0.1f));
        }, 20);

        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 40);
    };

    private final Consumer<LivingEntity> spinSlash = (target) -> {
        ModBBAnimations.animation(this, "chaos_knight.triple_slash", false);
        Runnable leap = () -> {
            ModUtils.leapTowards(this, target.getPositionVector(), (float) (0.35f * Math.sqrt(getDistance(target))), 0.5f);
            this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0);
        };
        Runnable meleeAttack = () -> {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MOB)
                    .directEntity(this)
                    .element(getElement())
                    .disablesShields().build();

            float damage = getAttack() * getConfigFloat("spin_slash_damage");
            ModUtils.handleAreaImpact(2.7f, (e) -> damage, this, getPositionVector().add(ModUtils.yVec(1)), source, 0.5f, 0, false);
            swipeBlocks();

            playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
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
        addEvent(() -> this.motionY += 0.5, 3);
        ModBBAnimations.animation(this, "chaos_knight.summon", false);
        for (int tick = 20; tick < 140; tick += 5) {
            addEvent(() -> {
                float damage = this.getAttack() * getConfigFloat("meteor_damage");
                ProjectileChaosFireball meteor = new ProjectileChaosFireball(world, this, damage, null);
                Vec3 pos = new Vec3(ModRandom.getFloat(10), ModRandom.getFloat(1), ModRandom.getFloat(10)).add(this.getPositionVector()).add(ModUtils.yVec(13));
                Vec3 targetPos = new Vec3(ModRandom.getFloat(5), 0, ModRandom.getFloat(5)).add(target.getPositionVector());
                Vec3 vel = targetPos.subtract(pos).normalize().scale(0.4);
                meteor.setPosition(pos.x, pos.y, pos.z);
                meteor.shoot(this, 90, 0, 0.0F, 0.0f, 0);
                ModUtils.setEntityVelocity(meteor, vel);
                meteor.setTravelRange(20f);
                world.spawnEntity(meteor);
            }, tick);
        }
        addEvent(() -> EntityChaosKnight.super.setSwingingArms(false), 30);
        addEvent(() -> {
            world.setEntityState(this, ModUtils.THIRD_PARTICLE_BYTE);
            this.playSound(SoundEvents.ENTITY_ILLAGER_CAST_SPELL, 1.0f, 1.0f * ModRandom.getFloat(0.2f));
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
    public void onUpdate() {
        super.onUpdate();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

        if(!world.isRemote && this.isLeaping()) {
            AABB box = getEntityBoundingBox().grow(0.25, 0.12, 0.25).offset(0, 0.12, 0);
            ModUtils.destroyBlocksInAABB(box, world, this);
        }

        if (world.isRemote || this.ticksExisted % 5 != 0) {
            return;
        }

        boolean hasGround = false;
        for (int i = 0; i > -10; i--) {
            if (!world.isAirBlock(getPosition().add(new BlockPos(0, i, 0)))) {
                hasGround = true;
            }
        }

        if (!hasGround && this.motionY < -1) {
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

        prevAttack = ModRandom.choice(attacks, rand, weights).next();
        prevAttack.accept(target);
        return prevAttack == sideSwipe || prevAttack == summonMeteors ? 50 : 90 - (int) (10 * (1 - healthRatio));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (amount > 0.0F && this.canBlockDamageSource(source)) {
            this.damageShield(amount);

            if (!source.isProjectile()) {
                Entity entity = source.getImmediateSource();

                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            this.playSound(SoundsHandler.ENTITY_CHAOS_KNIGHT_BLOCK, 1.0f, 0.9f + ModRandom.getFloat(0.2f));

            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    private boolean canBlockDamageSource(DamageSource damageSourceIn) {
        if (!damageSourceIn.isUnblockable() && !this.isSwingingArms()) {
            Vec3 vec3d = damageSourceIn.getDamageLocation();

            if (vec3d != null) {
                Vec3 vec3d1 = this.getLook(1.0F);
                Vec3 vec3d2 = vec3d.subtractReverse(new Vec3(this.posX, this.posY, this.posZ)).normalize();
                vec3d2 = new Vec3(vec3d2.x, 0.0D, vec3d2.z);

                return vec3d2.dotProduct(vec3d1) < 0.0D;
            }
        }

        return false;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.8f;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAITimedAttack<>(this, 1.0f, 60, 15, 0.5f));
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            for (int r = 1; r < 3; r++) {
                ModUtils.circleCallback(r, r * 20, (pos) -> {
                    pos = new Vec3(pos.x, 0, pos.y);
                    ParticleManager.spawnSplit(world, pos.add(this.getPositionVector().add(ModUtils.getRelativeOffset(this, new Vec3(1, 0, 0))).add(ModUtils.yVec(-1.5f))), ModColors.RED, pos.scale(0.1f).add(ModUtils.yVec(0.05f)));
                });
            }
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            if (chargeDir != null) {
                Vec3 particleVel = chargeDir.subtract(getPositionVector()).normalize().scale(0.5);
                ModUtils.lineCallback(getPositionVector(), chargeDir, 20, (vec, i) -> ModUtils.performNTimes(10, (j) -> {
                    ParticleManager.spawnSplit(world, vec.add(ModRandom.randVec().scale(dashRadius * 2)), ModColors.RED, particleVel.add(ModRandom.randVec().scale(0.2f)));
                    ParticleManager.spawnCustomSmoke(world, vec.add(ModRandom.randVec().scale(dashRadius * 2)), ModColors.GREY, particleVel.add(ModRandom.randVec().scale(0.2f)));
                    Vec3 flamePos = vec.add(ModRandom.randVec().scale(dashRadius * 2));
                    Vec3 flameVel = particleVel.add(ModRandom.randVec().scale(0.2f));
                    world.spawnParticle(ParticleTypes.FLAME, flamePos.x, flamePos.y, flamePos.z, flameVel.x, flameVel.y, flameVel.z);
                }));
                this.chargeDir = null; // So that the lazer doesn't render anymore
            }
        } else if (id == ModUtils.THIRD_PARTICLE_BYTE) {
            ModUtils.circleCallback(2, 50, (pos) -> {
                pos = new Vec3(pos.x, 0, pos.y);
                ParticleManager.spawnDust(world, pos.add(this.getPositionVector()).add(ModUtils.yVec(5)), ModColors.RED, pos.normalize().scale(0.3).add(ModUtils.yVec(0.1)), ModRandom.range(20, 30));
            });
        }
        super.handleStatusUpdate(id);
    }

    @Override
    public void setRenderDirection(Vec3 lazerDir) {
        this.chargeDir = lazerDir;
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!world.isRemote && this.dimension == ModDimensions.NEXUS.getId() && this.getLevel() > 0) {
            // Spawn portal entity
            Vec3 origin = this.getInitialPosition();
            EntityCrimsonPortalSpawn spawner = new EntityCrimsonPortalSpawn(world, origin.x, origin.y, origin.z);
            world.spawnEntity(spawner);

            // Spawn nexus teleporters
            BlockPos upperTeleporterPos = new BlockPos(origin).east(14).down().north();
            BlockPos lowerTeleporterPos = upperTeleporterPos.add(0, -81, -2);
            new WorldGenNexusTeleporter(new Vec3(-1, -83, 2)).generate(world, rand, upperTeleporterPos, Rotation.NONE);
            new WorldGenNexusTeleporter(new Vec3(-8, 81, 3)).generate(world, rand, lowerTeleporterPos, Rotation.CLOCKWISE_90);
        }
    }

    @Override
    public void readEntityFromNBT(CompoundTag compound) {
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

        super.readEntityFromNBT(compound);
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void addTrackingPlayer(ServerPlayer player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayer player) {
        super.removeTrackingPlayer(player);
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
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
    }

    @Override
    public int getBrightnessForRender() {
        return Math.min(super.getBrightnessForRender() + 60, 200);
    }

    @Override
    public Optional<Vec3> getTarget() {
        return Optional.ofNullable(this.chargeDir);
    }
}
