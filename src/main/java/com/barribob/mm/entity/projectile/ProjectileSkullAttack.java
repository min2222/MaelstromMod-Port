package com.barribob.mm.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

/**
 * An attack very similar to the will-o-the-wisp attack
 */
public class ProjectileSkullAttack extends ModProjectile {
    private static final int PARTICLE_AMOUNT = 6;
    private static final int AREA_FACTOR = 2;

    public ProjectileSkullAttack(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
        this.setNoGravity(true);
    }

    public ProjectileSkullAttack(Level worldIn) {
        super(worldIn);
    }

    public ProjectileSkullAttack(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        float f1 = 1.25f;
        float f2 = 0.15f;
        for (int i = 0; i < ProjectileSkullAttack.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnMaelstromSmoke(level, random,
                    new Vec3(this.getX() + ModRandom.getFloat(f1), this.getY() + ModRandom.getFloat(f1), this.getZ() + ModRandom.getFloat(f1)), true);
            level.addParticle(ParticleTypes.FLAME, this.getX() + ModRandom.getFloat(f2), this.getY() + ModRandom.getFloat(f2), this.getZ() + ModRandom.getFloat(f2), 0, 0, 0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        /*
         * Find all entities in a certain area and deal damage to them
         */
        List list = level.getEntities(this, this.getBoundingBox().inflate(AREA_FACTOR));
        if (list != null) {
            for (Object entity : list) {
                if (entity instanceof LivingEntity && this.shootingEntity != null && entity != this.shootingEntity && EntityMaelstromMob.CAN_TARGET.apply(((LivingEntity) entity))) {
                    int burnTime = 5;
                    ((LivingEntity) entity).setSecondsOnFire(burnTime);

                    ((LivingEntity) entity).hurt(ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()), this.getDamage());
                    ((LivingEntity) entity).push(0, 0.1D, 0);
                }
            }
        }

        super.onHit(result);
    }
}
