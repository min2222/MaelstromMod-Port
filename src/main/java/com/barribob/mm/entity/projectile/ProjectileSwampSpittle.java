package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectileSwampSpittle extends ModProjectile {
    public ProjectileSwampSpittle(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileSwampSpittle(Level worldIn) {
        super(worldIn);
    }

    public ProjectileSwampSpittle(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        ParticleManager.spawnEffect(level, this.position(), ModColors.CLIFF_STONE);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        ModUtils.handleBulletImpact(result.getEntity(), this, this.getDamage(), ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()));
        super.onHitEntity(result);
    }
}
