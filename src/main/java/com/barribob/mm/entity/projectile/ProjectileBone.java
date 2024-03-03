package com.barribob.mm.entity.projectile;

import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileBone extends ModProjectile {
    private static final int IMPACT_PARTICLE_AMOUNT = 10;
    private static final int EXPOSION_AREA_FACTOR = 1;

    public ProjectileBone(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileBone(Level worldIn) {
        super(worldIn);
    }

    public ProjectileBone(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < IMPACT_PARTICLE_AMOUNT; i++) {
            Vec3 vec1 = ModRandom.randVec()
                    .scale(EXPOSION_AREA_FACTOR * 2)
                    .add(position())
                    .add(ModUtils.yVec(0.8f));
            ParticleManager.spawnEffect(level, vec1, ModColors.WHITE);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> this.getDamage(), this.shootingEntity, this.position(),
                ModDamageSource.causeElementalExplosionDamage(this.shootingEntity, getElement()));
        this.playSound(SoundEvents.SKELETON_HURT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }
}
