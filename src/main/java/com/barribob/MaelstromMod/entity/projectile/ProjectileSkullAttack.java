package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMob;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * An attack very similar to the will-o-the-wisp attack
 */
public class ProjectileSkullAttack extends Projectile {
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
        for (int i = 0; i < this.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnMaelstromSmoke(world, rand,
                    new Vec3(this.posX + ModRandom.getFloat(f1), this.posY + ModRandom.getFloat(f1), this.posZ + ModRandom.getFloat(f1)), true);
            world.spawnParticle(ParticleTypes.FLAME, this.posX + ModRandom.getFloat(f2), this.posY + ModRandom.getFloat(f2), this.posZ + ModRandom.getFloat(f2), 0, 0, 0);
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        /*
         * Find all entities in a certain area and deal damage to them
         */
        List list = world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(AREA_FACTOR));
        if (list != null) {
            for (Object entity : list) {
                if (entity instanceof LivingEntity && this.shootingEntity != null && entity != this.shootingEntity && EntityMaelstromMob.CAN_TARGET.apply(((LivingEntity) entity))) {
                    int burnTime = 5;
                    ((LivingEntity) entity).setFire(burnTime);

                    ((LivingEntity) entity).attackEntityFrom(ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()), this.getDamage());
                    ((LivingEntity) entity).addVelocity(0, 0.1D, 0);
                }
            }
        }

        super.onHit(result);
    }
}
