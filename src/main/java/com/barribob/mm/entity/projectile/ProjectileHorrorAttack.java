package com.barribob.mm.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileHorrorAttack extends ModProjectile {
    private static final int PARTICLE_AMOUNT = 1;
    private static final int IMPACT_PARTICLE_AMOUNT = 20;
    private static final int EXPOSION_AREA_FACTOR = 2;

    public ProjectileHorrorAttack(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileHorrorAttack(Level worldIn) {
        super(worldIn);
    }

    public ProjectileHorrorAttack(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < ProjectileHorrorAttack.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnColoredSmoke(level, position(), getElement().particleColor, new Vec3(0, 0.1, 0));
        }
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < ProjectileHorrorAttack.IMPACT_PARTICLE_AMOUNT; i++) {
            Vec3 vec1 = ModRandom.randVec().scale(EXPOSION_AREA_FACTOR * 0.25).add(position());
            ParticleManager.spawnColoredExplosion(level, vec1, getElement().particleColor);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        DamageSource source = ModDamageSource.builder()
                .indirectEntity(shootingEntity)
                .directEntity(this)
                .type(ModDamageSource.EXPLOSION)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> this.getDamage(), this.shootingEntity, this.position(), source);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }
}
