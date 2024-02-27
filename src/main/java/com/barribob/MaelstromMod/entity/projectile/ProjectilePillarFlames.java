package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;

public class ProjectilePillarFlames extends Projectile {
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
    protected void onHit(RayTraceResult result) {
        this.setFire(1);
        ModUtils.handleBulletImpact(result.entityHit, this, this.getDamage(), ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()));
        super.onHit(result);
    }
}
