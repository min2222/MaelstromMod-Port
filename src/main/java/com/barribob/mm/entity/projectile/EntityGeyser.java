package com.barribob.mm.entity.projectile;

import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class EntityGeyser extends ModProjectile {
    private int tickDelay = 30;
    private int blastRadius = 3;

    public EntityGeyser(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
        this.setNoGravity(true);
    }

    public EntityGeyser(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public EntityGeyser(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    /**
     * Set the delay before "going off" in ticks
     */
    public void setDelay(int delay) {
        this.tickDelay = delay;
    }

    @Override
    public void tick() {
        super.onUpdate();

        if (this.tickCount >= this.tickDelay) {
            this.onHit(null);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (result != null) {
            return;
        }
        ModUtils.handleAreaImpact(blastRadius, (e) -> this.getDamage(), this.shootingEntity, this.position(), DamageSource.causeExplosionDamage(this.shootingEntity));
        this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }

    @Override
    protected void spawnImpactParticles() {
        ModUtils.performNTimes(1000, (i) -> {
            Vec3 offset = new Vec3(ModRandom.getFloat(1), 0, ModRandom.getFloat(1)).normalize().scale(ModRandom.getFloat(blastRadius));
            ParticleManager.spawnFirework(world, ModUtils.entityPos(this).add(offset), ModColors.CLIFF_STONE, ModUtils.yVec(rand.nextFloat() + 1).scale(0.5));
        });
    }

    @Override
    protected void spawnParticles() {
        ModUtils.circleCallback(this.blastRadius, 30,
                (offset) -> ParticleManager.spawnEffect(world, ModUtils.entityPos(this).add(new Vec3(offset.x, 0.5f, offset.y)), ModColors.CLIFF_STONE));
    }
}
