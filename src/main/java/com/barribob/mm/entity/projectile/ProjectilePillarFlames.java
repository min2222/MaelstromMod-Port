package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectilePillarFlames extends ModProjectile {
    public ProjectilePillarFlames(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
        this.setNoGravity(true);
    }

    public ProjectilePillarFlames(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectilePillarFlames(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    protected void spawnParticles() {
        level.addParticle(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        this.setSecondsOnFire(1);
        ModUtils.handleBulletImpact(result.getEntity(), this, this.getDamage(), ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()));
        super.onHitEntity(result);
    }
}
