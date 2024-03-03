package com.barribob.mm.entity.projectile;

import java.util.List;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

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
        for (int i = 0; i < ProjectileWillOTheWisp.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnMaelstromSmoke(level, random,
                    new Vec3(this.getX() + ModRandom.getFloat(f1), this.getY() + ModRandom.getFloat(f1), this.getZ() + ModRandom.getFloat(f1)), true);
            level.addParticle(ParticleTypes.FLAME, this.getX() + ModRandom.getFloat(f2), this.getY() + ModRandom.getFloat(f2), this.getZ() + ModRandom.getFloat(f2), 0, 0, 0);
        }
    }

    @Override
    public void tick() {
        super.tick();

        /*
         * Find all entities in a certain area and deal damage to them
         */
        List list = level.getEntities(this, this.getBoundingBox().inflate(AREA_FACTOR));
        if (list != null) {
            for (Object entity : list) {
                if (entity instanceof LivingEntity && this.shootingEntity != null && entity != this.shootingEntity) {
                    int burnTime = this.isOnFire() ? 10 : 5;
                    ((LivingEntity) entity).setSecondsOnFire(burnTime);

                    ((LivingEntity) entity).hurt(ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()),
                            this.getGunDamage(((LivingEntity) entity)));
                    ((LivingEntity) entity).push(0, 0.1D, 0);

                    float f1 = (float) Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);

                    if (f1 > 0.0F) {
                        ((LivingEntity) entity).push(this.getDeltaMovement().x * this.getKnockback() * 0.6000000238418579D / f1, 0.0D,
                                this.getDeltaMovement().z * this.getKnockback() * 0.6000000238418579D / f1);
                    }
                }
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Only destroy if the collision is a block
        if (result.getEntity() != null) {
            return;
        }

        super.onHitEntity(result);
    }
}
