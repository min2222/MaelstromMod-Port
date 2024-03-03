package com.barribob.mm.entity.projectile;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectileMaelstromMissile extends ModProjectile {
    public ProjectileMaelstromMissile(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
        this.setNoGravity(true);
    }

    public ProjectileMaelstromMissile(Level worldIn) {
        super(worldIn);
    }

    public ProjectileMaelstromMissile(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        ParticleManager.spawnDarkFlames(level, this.random, this.position());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() != null && result.getEntity() instanceof LivingEntity living && !EntityMaelstromMob.isMaelstromMob(living) && this.shootingEntity != null) {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MAGIC)
                    .indirectEntity(this)
                    .directEntity(shootingEntity)
                    .element(getElement())
                    .stoppedByArmorNotShields().build();

            result.getEntity().hurt(source, this.getDamage());
        }
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0f + ModRandom.getFloat(0.2f), 1.0f + ModRandom.getFloat(0.2f));
        super.onHit(result);
    }
}
