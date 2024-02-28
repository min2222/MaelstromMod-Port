package com.barribob.mm.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileMeteor extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 15;
    private static final int EXPOSION_AREA_FACTOR = 6;

    public ProjectileMeteor(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileMeteor(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileMeteor(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        float size = 0.25f;
        for (int i = 0; i < this.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnEffect(this.world, position().add(ModRandom.randVec().scale(size)), ModColors.PURPLE);
        }
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < 1000; i++) {
            Vec3 unit = new Vec3(0, 1, 0);
            unit = unit.rotatePitch((float) (Math.PI * ModRandom.getFloat(1)));
            unit = unit.rotateYaw((float) (Math.PI * ModRandom.getFloat(1)));
            unit = unit.normalize().scale(this.EXPOSION_AREA_FACTOR);
            ParticleManager.spawnWisp(world, unit.add(position()), ModColors.PURPLE, Vec3.ZERO);
        }
        for (int i = 0; i < 100; i++) {
            ParticleManager.spawnMaelstromExplosion(world, rand, position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR)));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        float knockbackFactor = 1.3f + this.getKnockback() * 0.4f;
        int fireFactor = this.isBurning() ? 5 : 0;
        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> this.getGunDamage((e)), this.shootingEntity, this.position(),
                DamageSource.causeExplosionDamage(this.shootingEntity), knockbackFactor, fireFactor);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }
}
