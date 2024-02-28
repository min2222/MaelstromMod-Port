package com.barribob.mm.entity.projectile;

import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

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
        world.spawnParticle(ParticleTypes.FLAME, this.posX, this.posY, this.posZ, 0, 0, 0);
    }

    @Override
    protected void onHit(HitResult result) {
        this.setFire(1);
        ModUtils.handleBulletImpact(result.entityHit, this, this.getDamage(), ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()));
        super.onHit(result);
    }
}
