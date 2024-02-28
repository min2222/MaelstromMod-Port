package com.barribob.mm.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

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
        ParticleManager.spawnDarkFlames(world, this.rand, this.position());
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.entityHit != null && !EntityMaelstromMob.isMaelstromMob(result.entityHit) && this.shootingEntity != null) {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MAGIC)
                    .indirectEntity(this)
                    .directEntity(shootingEntity)
                    .element(getElement())
                    .stoppedByArmorNotShields().build();

            result.entityHit.attackEntityFrom(source, this.getDamage());
        }
        this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0f + ModRandom.getFloat(0.2f), 1.0f + ModRandom.getFloat(0.2f));
        super.onHit(result);
    }
}
