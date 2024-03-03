package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileBrownstoneCannon extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 1;
    private static final int IMPACT_PARTICLE_AMOUNT = 20;
    private static final int EXPOSION_AREA_FACTOR = 2;

    public ProjectileBrownstoneCannon(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
    }

    public ProjectileBrownstoneCannon(Level worldIn) {
        super(worldIn);
    }

    public ProjectileBrownstoneCannon(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        for (int i = 0; i < ProjectileBrownstoneCannon.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnEffect(level, position(), ModColors.BROWNSTONE);
        }
    }

    @Override
    protected void spawnImpactParticles() {
        ModUtils.circleCallback(EXPOSION_AREA_FACTOR, 9, (pos) -> {
            ModUtils.circleCallback((float) (pos.x), 32, (pos2) -> {
                ParticleManager.spawnSplit(level, new Vec3(pos2.x, pos.y, pos2.y).add(position()), ModColors.BROWNSTONE, Vec3.ZERO);
            });
        });
    }

    @Override
    protected void onHit(HitResult result) {
        float knockbackFactor = 1 + this.getKnockback() * 0.4f;
        int fireFactor = this.isOnFire() ? 5 : 0;
        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> this.getGunDamage(e), this.shootingEntity, this.position(),
                DamageSource.explosion(this.shootingEntity), knockbackFactor, fireFactor);
        this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }
}
