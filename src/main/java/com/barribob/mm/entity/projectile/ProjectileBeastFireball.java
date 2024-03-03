package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileBeastFireball extends ModProjectile {
    private static final int PARTICLE_AMOUNT = 15;
    private static final int IMPACT_PARTICLE_AMOUNT = 150;
    private static final int EXPOSION_AREA_FACTOR = 3;

    public ProjectileBeastFireball(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
        this.setNoGravity(true);
    }

    public ProjectileBeastFireball(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileBeastFireball(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    protected void spawnParticles() {
        float size = 0.5f;
        for (int i = 0; i < PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnDarkFlames(this.level, random, ModUtils.entityPos(this).add(ModRandom.randVec().scale(size)));
        }
    }

    @Override
    protected void spawnImpactParticles() {
        float size = (float) (EXPOSION_AREA_FACTOR * this.getBoundingBox().inflate(EXPOSION_AREA_FACTOR).getSize() * 0.5f);
        for (int i = 0; i < IMPACT_PARTICLE_AMOUNT; i++) {
            Vec3 pos = ModUtils.entityPos(this).add(ModRandom.randVec().scale(size));
            if (random.nextInt(2) == 0) {
                ParticleManager.spawnDarkFlames(this.level, random, pos, ModRandom.randVec().scale(0.5f));
            } else {
                this.level.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f));
            }
        }
        for (int i = 0; i < 10; i++) {
            Vec3 pos = ModUtils.entityPos(this).add(ModRandom.randVec().scale(size));
            this.level.addParticle(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f), ModRandom.getFloat(0.25f));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if(!level.isClientSide && shootingEntity != null) {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.EXPLOSION)
                    .directEntity(this)
                    .indirectEntity(shootingEntity)
                    .element(getElement())
                    .stoppedByArmorNotShields().build();

            ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> {
                if (e instanceof LivingEntity) {
                    ((LivingEntity) e).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
                }
                return this.getDamage();
            }, this.shootingEntity, this.position(), source, 1, 0);

            level.explode(shootingEntity, getX(), getY(), getZ(), 2.0f, ModUtils.mobGriefing(level, this.shootingEntity) ? BlockInteraction.DESTROY : BlockInteraction.NONE);
        }
        super.onHit(result);
    }
}
