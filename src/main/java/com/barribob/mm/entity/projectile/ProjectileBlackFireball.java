package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileBlackFireball extends ModProjectile {
    private static final int PARTICLE_AMOUNT = 15;
    private static final int IMPACT_PARTICLE_AMOUNT = 150;
    private static final int EXPOSION_AREA_FACTOR = 3;

    public ProjectileBlackFireball(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
        this.setNoGravity(true);
    }

    public ProjectileBlackFireball(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileBlackFireball(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    protected void spawnParticles() {
        float size = 0.5f;
        for (int i = 0; i < this.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnDarkFlames(this.world, rand, ModUtils.entityPos(this).add(ModRandom.randVec().scale(size)));
        }
    }

    @Override
    protected void spawnImpactParticles() {
        float size = (float) (EXPOSION_AREA_FACTOR * this.getBoundingBox().grow(EXPOSION_AREA_FACTOR).getAverageEdgeLength() * 0.5f);
        for (int i = 0; i < this.IMPACT_PARTICLE_AMOUNT; i++) {
            Vec3 pos = ModUtils.entityPos(this).add(ModRandom.randVec().scale(size));
            if (rand.nextInt(2) == 0) {
                ParticleManager.spawnDarkFlames(this.world, rand, pos, ModRandom.randVec().scale(0.5f));
            } else {
                this.world.spawnParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f));
            }
        }
        for (int i = 0; i < 10; i++) {
            Vec3 pos = ModUtils.entityPos(this).add(ModRandom.randVec().scale(size));
            this.world.spawnParticle(ParticleTypes.EXPLOSION_LARGE, pos.x, pos.y, pos.z, ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.EXPLOSION)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> {
                    if (e instanceof LivingEntity) {
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 80, 0));
                    }
                    return this.getDamage();
                }, this.shootingEntity, this.position(), source, 1, 0);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }
}
