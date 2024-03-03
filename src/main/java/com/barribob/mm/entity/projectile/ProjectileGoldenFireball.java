package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileGoldenFireball extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 15;
    private static final int IMPACT_PARTICLE_AMOUNT = 10;
    private static final int EXPOSION_AREA_FACTOR = 4;

    public ProjectileGoldenFireball(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileGoldenFireball(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileGoldenFireball(Level worldIn, double x, double y, double z) {
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
        float size = 0.5f;
        for (int i = 0; i < ProjectileGoldenFireball.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnCustomSmoke(level, position().add(ModRandom.randVec().scale(size)), ModColors.YELLOW, ModUtils.yVec(0.1f));
        }
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < 1000; i++) {
            Vec3 unit = new Vec3(0, 1, 0);
            unit = unit.xRot((float) (Math.PI * ModRandom.getFloat(1)));
            unit = unit.yRot((float) (Math.PI * ModRandom.getFloat(1)));
            unit = unit.normalize().scale(EXPOSION_AREA_FACTOR);
            ParticleManager.spawnSplit(level, unit.add(position()), ModColors.YELLOW, Vec3.ZERO);
        }
        for (int i = 0; i < ProjectileGoldenFireball.IMPACT_PARTICLE_AMOUNT; i++) {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.getX() + ModRandom.getFloat(EXPOSION_AREA_FACTOR),
                    this.getY() + ModRandom.getFloat(EXPOSION_AREA_FACTOR), this.getZ() + ModRandom.getFloat(EXPOSION_AREA_FACTOR), 0, 0, 0);
            this.level.addParticle(ParticleTypes.FLAME, this.getX() + ModRandom.getFloat(EXPOSION_AREA_FACTOR), this.getY() + ModRandom.getFloat(EXPOSION_AREA_FACTOR),
                    this.getZ() + ModRandom.getFloat(EXPOSION_AREA_FACTOR), 0, 0, 0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        float knockbackFactor = 1.1f + this.getKnockback() * 0.4f;
        int fireFactor = this.isOnFire() ? 10 : 5;
        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> this.getGunDamage((e)), this.shootingEntity, this.position(),
                ModDamageSource.causeElementalExplosionDamage(shootingEntity, getElement()), knockbackFactor, fireFactor);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));

        super.onHit(result);
    }
}
