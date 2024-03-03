package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileFireball extends ProjectileGun {
    private static final int IMPACT_PARTICLE_AMOUNT = 30;
    private static final int EXPOSION_AREA_FACTOR = 4;
    public static final Vec3 FIREBALL_COLOR = new Vec3(1.0, 0.6, 0.5);

    public ProjectileFireball(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileFireball(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileFireball(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    protected void spawnParticles() {
        float size = 0.25f;
        for (int i = 0; i < 2; i++) {
            ParticleManager.spawnCustomSmoke(this.level,
                    this.position().add(new Vec3(ModRandom.getFloat(size), ModRandom.getFloat(size), ModRandom.getFloat(size))),
                    FIREBALL_COLOR,
                    ModUtils.yVec(0.1f));
        }
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < IMPACT_PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnColoredExplosion(level, this.position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR * 2)), ModColors.FIREBALL_ORANGE);
            this.level.addParticle(ParticleTypes.FLAME, this.getX() + ModRandom.getFloat(EXPOSION_AREA_FACTOR), this.getY() + ModRandom.getFloat(EXPOSION_AREA_FACTOR),
                    this.getZ() + ModRandom.getFloat(EXPOSION_AREA_FACTOR), 0, 0, 0);
            ParticleManager.spawnEffect(level, position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR * 2)), FIREBALL_COLOR);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        float knockbackFactor = 1.1f + this.getKnockback() * 0.4f;
        int fireFactor = this.isOnFire() ? 10 : 5;
        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, this::getGunDamage, this.shootingEntity, this.position(),
                DamageSource.explosion(this.shootingEntity), knockbackFactor, fireFactor);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }

    @Override
    public Item getItemToRender() {
        return Items.FIRE_CHARGE;
    }
}
