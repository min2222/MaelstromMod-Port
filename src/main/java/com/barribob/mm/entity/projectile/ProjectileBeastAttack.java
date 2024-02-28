package com.barribob.mm.entity.projectile;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.potion.PotionEffect;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

/**
 * The simple attacks that the beast outputs during its ranged mode
 */
public class ProjectileBeastAttack extends ModProjectile {
    private static final int PARTICLE_AMOUNT = 3;
    private static final int IMPACT_PARTICLE_AMOUNT = 20;

    public ProjectileBeastAttack(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileBeastAttack(Level worldIn) {
        super(worldIn);
    }

    public ProjectileBeastAttack(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        for (int i = 0; i < PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnSmoke2(world, this.position().add(ModRandom.randVec().scale(0.5f)), this.getElement().particleColor, ModUtils.yVec(0.1f));
        }
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < IMPACT_PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnSmoke2(world, this.position().add(ModRandom.randVec()), this.getElement().particleColor, ModUtils.yVec(0.1f));
        }
    }

    @Override
    protected void onHit(HitResult result) {
        ModUtils.handleBulletImpact(result.entityHit, this, this.getDamage(), ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()), 1, (proj, entity) -> {
            if (this.getElement().equals(Element.CRIMSON) && entity instanceof LivingEntity) {
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(MobEffects.WITHER, 100));
            }
        }, (proj, entity) -> {
        });
        super.onHit(result);
    }
}
