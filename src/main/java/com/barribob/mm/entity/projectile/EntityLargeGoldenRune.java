package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityLargeGoldenRune extends ModProjectile {
    private static final byte PARTICLE_BYTE = 3;
    private int tickDelay = 30;
    protected int blastRadius = 4;

    public EntityLargeGoldenRune(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
        this.setNoGravity(true);
    }

    public EntityLargeGoldenRune(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public EntityLargeGoldenRune(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount >= this.tickDelay) {
            this.onHit(null);
        }
    }

    public void setDelay(int delay) {
        this.tickDelay = delay;
    }

    @Override
    protected void onHit(HitResult result) {
        if (result != null) {
            return;
        }
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.MAGIC)
                .indirectEntity(shootingEntity)
                .directEntity(this)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(blastRadius, (e) -> {
                    if (e instanceof LivingEntity && !level.isClientSide) {
                        blastEffect((LivingEntity) e);
                    }
                    return this.getDamage();
                }, this.shootingEntity, this.position(), source, 1, 0, false);
        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }

    protected void blastEffect(LivingEntity e) {
        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
    }

    @Override
    protected void spawnImpactParticles() {
        ModUtils.performNTimes(10, (i) -> {
            ModUtils.circleCallback(blastRadius, 30,
                    (offset) -> ParticleManager.spawnWisp(level, ModUtils.entityPos(this).add(new Vec3(offset.x, i * 0.5, offset.y)), ModColors.YELLOW, Vec3.ZERO));
            ModUtils.circleCallback(blastRadius - 1, 30,
                    (offset) -> ParticleManager.spawnWisp(level, ModUtils.entityPos(this).add(new Vec3(offset.x, i * 0.5, offset.y)), ModColors.YELLOW, Vec3.ZERO));
        });
    }

    @Override
    protected void spawnParticles() {
        if (this.tickCount % 10 == 0) {
            ModUtils.circleCallback(this.blastRadius, 45,
                    (offset) -> ParticleManager.spawnSwirl(level, ModUtils.entityPos(this).add(new Vec3(offset.x, 0.5f, offset.y)), ModColors.YELLOW, Vec3.ZERO, ModRandom.range(10, 15)));
            ModUtils.circleCallback(this.blastRadius - 1, 30,
                    (offset) -> ParticleManager.spawnSwirl(level, ModUtils.entityPos(this).add(new Vec3(offset.x, 0.6f, offset.y)), ModColors.YELLOW, Vec3.ZERO, ModRandom.range(10, 15)));
            ModUtils.circleCallback(this.blastRadius - 2, 30,
                    (offset) -> ParticleManager.spawnSwirl(level, ModUtils.entityPos(this).add(new Vec3(offset.x, 0.7f, offset.y)), ModColors.YELLOW, Vec3.ZERO, ModRandom.range(10, 15)));
        }
    }
}
