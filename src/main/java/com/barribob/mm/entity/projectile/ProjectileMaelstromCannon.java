package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * The projectile for the maelstrom cannon item
 */
public class ProjectileMaelstromCannon extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 1;
    private static final int IMPACT_PARTICLE_AMOUNT = 20;
    private static final int EXPOSION_AREA_FACTOR = 2;

    public ProjectileMaelstromCannon(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
    }

    public ProjectileMaelstromCannon(Level worldIn) {
        super(worldIn);
    }

    public ProjectileMaelstromCannon(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        for (int i = 0; i < ProjectileMaelstromCannon.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnMaelstromSmoke(level, random, this.position() true);
        }
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < ProjectileMaelstromCannon.IMPACT_PARTICLE_AMOUNT; i++) {
            Vec3 vec1 = ModRandom.randVec().scale(EXPOSION_AREA_FACTOR * 0.25).add(position());
            ParticleManager.spawnMaelstromExplosion(level, random, vec1);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        float knockbackFactor = 1 + this.getKnockback() * 0.4f;
        int fireFactor = this.isOnFire() ? 5 : 0;
        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> this.getGunDamage(e), this.shootingEntity, this.position(),
                ModDamageSource.causeElementalExplosionDamage(shootingEntity, getElement()), knockbackFactor, fireFactor);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));

        super.onHit(result);
    }
}
