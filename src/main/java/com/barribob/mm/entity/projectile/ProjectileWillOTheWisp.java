package com.barribob.mm.entity.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

/**
 * Launched from the will o the wisp staff
 */
public class ProjectileWillOTheWisp extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 6;
    private static final int AREA_FACTOR = 2;

    public ProjectileWillOTheWisp(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileWillOTheWisp(Level worldIn) {
        super(worldIn);
    }

    public ProjectileWillOTheWisp(Level worldIn, double x, double y, double z) {
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
    public void onUpdate() {
        super.onUpdate();

        /*
         * Find all entities in a certain area and deal damage to them
         */
        List list = world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(AREA_FACTOR));
        if (list != null) {
            for (Object entity : list) {
                if (entity instanceof LivingEntity && this.shootingEntity != null && entity != this.shootingEntity) {
                    int burnTime = this.isBurning() ? 10 : 5;
                    ((LivingEntity) entity).setFire(burnTime);

                    ((LivingEntity) entity).attackEntityFrom(ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()),
                            this.getGunDamage(((LivingEntity) entity)));
                    ((LivingEntity) entity).addVelocity(0, 0.1D, 0);

                    float f1 = Mth.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                    if (f1 > 0.0F) {
                        ((LivingEntity) entity).addVelocity(this.motionX * this.getKnockback() * 0.6000000238418579D / f1, 0.0D,
                                this.motionZ * this.getKnockback() * 0.6000000238418579D / f1);
                    }
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        // Only destroy if the collision is a block
        if (result.entityHit != null) {
            return;
        }

        super.onHit(result);
    }
}
