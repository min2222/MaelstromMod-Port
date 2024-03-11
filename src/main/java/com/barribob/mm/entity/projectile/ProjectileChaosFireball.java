package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileChaosFireball extends ProjectileGun {
    private static final int IMPACT_PARTICLE_AMOUNT = 20;
    private static final int EXPOSION_AREA_FACTOR = 2;
    public static final Vec3 FIREBALL_COLOR = new Vec3(1.0, 0.6, 0.5);

    public ProjectileChaosFireball(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileChaosFireball(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileChaosFireball(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        if ((this.tickCount / 5.0f) % 5 == 0) {
            this.playSound(SoundEvents.FIRE_EXTINGUISH, 0.2f, ModRandom.getFloat(0.2f) + 1.0f);
        }

        if (this.tickCount == 2) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }

        Vec3 vel = this.getDeltaMovement();

        super.tick();

        // Maintain the velocity the entity has
        ModUtils.setEntityVelocity(this, vel);
    }

    @Override
    protected void spawnParticles() {
        ParticleManager.spawnSmoke2(level, this.position().add(ModUtils.yVec(0.3f)), ModColors.FADED_RED, ModUtils.yVec(0.1));
    }

    @Override
    protected void spawnImpactParticles() {
        this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        for (int i = 0; i < IMPACT_PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnEffect(level, position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR * 2)), ModColors.RED);
            ParticleManager.spawnFluff(level, position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR * 2)), FIREBALL_COLOR, ModRandom.randVec().scale(0.1));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        float knockbackFactor = 1.1f + this.getKnockback() * 0.4f;
        int fireFactor = this.isOnFire() ? 8 : 3;

        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.EXPLOSION)
                .element(getElement())
                .indirectEntity(shootingEntity)
                .directEntity(this)
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, this::getGunDamage, this.shootingEntity, this.position(), source, knockbackFactor, fireFactor);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            ParticleManager.spawnSwirl2(level, position(), ModColors.RED, Vec3.ZERO);
        }
        super.handleEntityEvent(id);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        this.onHit(null);
        return super.hurt(source, amount);
    }
}