package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMob;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProjectileHomingFlame extends Projectile {
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
    public void onUpdate() {
        Vec3 prevVel = ModUtils.getEntityVelocity(this);
        super.onUpdate();
        ModUtils.setEntityVelocity(this, prevVel);

        double speed = 0.04;

        if (!this.world.isRemote &&
                this.shootingEntity != null &&
                this.shootingEntity instanceof Mob &&
                ((Mob) this.shootingEntity).getAttackTarget() != null) {
            ModUtils.homeToPosition(this, speed, ((Mob) this.shootingEntity).getAttackTarget().getPositionEyes(1));
        }

        if(!world.isRemote) {
            ModUtils.avoidOtherEntities(this, speed, 4, e -> e instanceof ProjectileHomingFlame);
        }

        if(!this.world.isRemote && this.ticksExisted > AGE) {
            onHit(null);
        }

        if(this.shootingEntity != null && this.shootingEntity.isDead) {
            this.setDead();
        }

        if (!this.world.isRemote && this.ticksExisted % 3 == 0) {
            this.playSound(SoundEvents.BLOCK_STONE_BREAK, 0.2f, ModRandom.getFloat(0.2f) + 0.3f);
        }
    }

    @Override
    protected void spawnImpactParticles() {
        ParticleManager.spawnColoredExplosion(world, getPositionVector(), Vec3.ZERO);
        super.spawnImpactParticles();
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            float colorAge = ModUtils.clamp((AGE - ticksExisted) / (float)AGE, 0.1, 1);
            ParticleManager.spawnColoredFire(world, rand,
                    getPositionVector().add(ModRandom.randVec().scale(0.25)),
                    new Vec3(0.8, 1.0, rand.nextFloat()).scale(colorAge));
        }
    }

    @Override
    protected void onHit(@Nullable RayTraceResult result) {
        if(result != null && EntityMaelstromMob.isMaelstromMob(result.entityHit)) {
            return;
        }

        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.PROJECTILE)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(0.6f, (e) -> getDamage(), shootingEntity, getPositionVector(), source, 0, 0, false);
        playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0f, 1.0f + ModRandom.getFloat(0.2f));
        super.onHit(result);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (!this.isDead && amount > 0) {
            this.setDead();
            this.onHit(null);
        }
        return super.attackEntityFrom(source, amount);
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
        float colorAge = ModUtils.clamp((AGE - ticksExisted) / (float)AGE, 0.0, 1) * 255;
        return (int) colorAge;
    }
}
