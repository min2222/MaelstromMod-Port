package com.barribob.MaelstromMod.entity.entities.gauntlet;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.entity.entities.EntityLeveledMob;
import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMob;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.OptionalDouble;

public class EntityCrimsonCrystal extends Entity {
    protected static final EntityDataAccessor<Float> CLOSEST_TARGET_DISTANCE =
            SynchedEntityData.createKey(EntityLeveledMob.class, EntityDataSerializers.FLOAT);
    private EntityLeveledMob shootingEntity = null;
    public static final float explosionDistance = (float) Main.mobsConfig.getDouble("alternative_maelstrom_gauntlet_stage_2.crystal_explosion_radius");
    public static float visualActivationDistance = explosionDistance * 4;
    public static final double VisualActivationDistanceSq = Math.pow(visualActivationDistance, 2);
    private static final double explosionRadiusSq = Math.pow(explosionDistance, 2);
    private static final float crystalLifespan = (float) Main.mobsConfig.getDouble("alternative_maelstrom_gauntlet_stage_2.crystal_lifespan");

    public EntityCrimsonCrystal(Level world) {
        super(world);
        this.setSize(1, 1);
    }

    public EntityCrimsonCrystal(Level world, EntityLeveledMob shootingEntity) {
        super(world);
        this.shootingEntity = shootingEntity;
        this.setSize(1, 1);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {

            if (shootingEntity == null) {
                this.setDead();
                return;
            }

            Vec3 pos = this.getPositionVector();
            OptionalDouble optionalDistance = ModUtils.getEntitiesInBox(this, ModUtils.makeBox(pos, pos).grow(20))
                    .stream()
                    .filter(EntityMaelstromMob.CAN_TARGET)
                    .mapToDouble((e) -> e.getDistanceSq(this))
                    .min();

            if (optionalDistance.isPresent()) {
                dataManager.set(CLOSEST_TARGET_DISTANCE, ((float) optionalDistance.getAsDouble()));

                if (optionalDistance.getAsDouble() < explosionRadiusSq) {
                    explodeAndDespawn();
                }
            } else {
                dataManager.set(CLOSEST_TARGET_DISTANCE, Float.POSITIVE_INFINITY);
            }

            boolean randomExplodeCondition = ticksExisted > crystalLifespan && rand.nextInt(50) == 0;
            if (randomExplodeCondition && !isDead) explodeAndDespawn();

        } else if (getTargetDistanceSq() < VisualActivationDistanceSq) {
            double distance = Math.sqrt(getTargetDistanceSq());
            double distanceToExplosion = (visualActivationDistance - distance) /
                    (visualActivationDistance - explosionDistance);
            int numParticles = (int) (distanceToExplosion * VisualActivationDistanceSq * 0.02);
            for (int i = 0; i < numParticles; i++) {
                Vec3 vec = ModRandom.randVec().normalize().scale(explosionDistance);
                ParticleManager.spawnEffect(world, getPositionVector().add(vec), getParticleColor());
            }
        }
    }

    public void explodeAndDespawn() {
        this.setDead();
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.EXPLOSION)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(shootingEntity.getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(explosionDistance, e -> shootingEntity.getAttack(), this.shootingEntity, getPositionVector(), source, 1, 0, false);
        world.newExplosion(shootingEntity, posX, posY, posZ, 1, false, ModUtils.mobGriefing(world, shootingEntity));
        playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f + ModRandom.getFloat(0.2f));
        world.setEntityState(this, ModUtils.PARTICLE_BYTE);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            for (int i = 0; i < 20; i++) {
                Vec3 randPos = radialRandVec();
                ParticleManager.spawnColoredExplosion(world, randPos, ModColors.RED);
            }

            for (int i = 0; i < 20; i++) {
                Vec3 randPos = radialRandVec();
                Vec3 outVelocity = ModUtils.direction(getPositionVector(), randPos).scale(0.2f);
                ParticleManager.spawnFluff(world, randPos, Vec3.ZERO, outVelocity);
            }
        }
        super.handleStatusUpdate(id);
    }

    public Vec3 radialRandVec() {
        return getPositionVector()
                .add(ModRandom.randVec().normalize()
                        .scale(rand.nextDouble() * explosionDistance));
    }

    private float getTargetDistanceSq() {
        return dataManager.get(CLOSEST_TARGET_DISTANCE);
    }

    public Vec3 getParticleColor() {
        double distance = Math.sqrt(Math.min(EntityCrimsonCrystal.VisualActivationDistanceSq, getTargetDistanceSq()));
        float distanceToExplosion = (float) ((EntityCrimsonCrystal.visualActivationDistance - distance) /
                (EntityCrimsonCrystal.visualActivationDistance - EntityCrimsonCrystal.explosionDistance));
        return new Vec3(1.0f, 1 - distanceToExplosion, 1 - distanceToExplosion);
    }

    @Override
    protected void entityInit() {
        dataManager.register(CLOSEST_TARGET_DISTANCE, Float.POSITIVE_INFINITY);
    }

    @Override
    protected void readEntityFromNBT(@Nonnull CompoundTag compound) {

    }

    @Override
    protected void writeEntityToNBT(@Nonnull CompoundTag compound) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    public final boolean canBeAttackedWithItem() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if(EntityMaelstromMob.CAN_TARGET.apply(source.getTrueSource()) && !world.isRemote && !isDead) {
            explodeAndDespawn();
        }
        return super.attackEntityFrom(source, amount);
    }
}
