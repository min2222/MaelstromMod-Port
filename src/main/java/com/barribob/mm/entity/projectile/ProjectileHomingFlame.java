package com.barribob.mm.entity.projectile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileHomingFlame extends ModProjectile {
    private static final int AGE = 20 * 8;
    public ProjectileHomingFlame(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
    }

    public ProjectileHomingFlame(Level worldIn) {
        super(worldIn);
    }

    public ProjectileHomingFlame(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public void tick() {
        Vec3 prevVel = ModUtils.getEntityVelocity(this);
        super.tick();
        ModUtils.setEntityVelocity(this, prevVel);

        double speed = 0.04;

        if (!this.level.isClientSide &&
                this.shootingEntity != null &&
                this.shootingEntity instanceof Mob &&
                ((Mob) this.shootingEntity).getTarget() != null) {
            ModUtils.homeToPosition(this, speed, ((Mob) this.shootingEntity).getTarget().getEyePosition(1));
        }

        if(!level.isClientSide) {
            ModUtils.avoidOtherEntities(this, speed, 4, e -> e instanceof ProjectileHomingFlame);
        }

        if(!this.level.isClientSide && this.tickCount > AGE) {
            onHit(null);
        }

        if(this.shootingEntity != null && !this.shootingEntity.isAlive()) {
            this.discard();
        }

        if (!this.level.isClientSide && this.tickCount % 3 == 0) {
            this.playSound(SoundEvents.STONE_BREAK, 0.2f, ModRandom.getFloat(0.2f) + 0.3f);
        }
    }

    @Override
    protected void spawnImpactParticles() {
        ParticleManager.spawnColoredExplosion(level, position(), Vec3.ZERO);
        super.spawnImpactParticles();
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            float colorAge = ModUtils.clamp((AGE - tickCount) / (float)AGE, 0.1, 1);
            ParticleManager.spawnColoredFire(level, random,
                    position().add(ModRandom.randVec().scale(0.25)),
                    new Vec3(0.8, 1.0, random.nextFloat()).scale(colorAge));
        }
    }

    @Override
    protected void onHitEntity(@Nullable EntityHitResult result) {
        if(result != null && result.getEntity() instanceof LivingEntity living) {
        	if(EntityMaelstromMob.isMaelstromMob(living)) {
                return;
        	}
        }

        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.PROJECTILE)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(0.6f, (e) -> getDamage(), shootingEntity, position(), source, 0, 0, false);
        playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0f, 1.0f + ModRandom.getFloat(0.2f));
        super.onHitEntity(result);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (this.isAlive() && amount > 0) {
            this.discard();
            this.onHit(null);
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return true;
    }

    @Override
    public int getBrightnessForRender() {
        float colorAge = ModUtils.clamp((AGE - tickCount) / (float)AGE, 0.0, 1) * 255;
        return (int) colorAge;
    }
}