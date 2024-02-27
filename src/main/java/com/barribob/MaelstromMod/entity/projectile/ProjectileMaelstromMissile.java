package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMob;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;

public class ProjectileMaelstromMissile extends Projectile {
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
        ParticleManager.spawnDarkFlames(world, this.rand, this.getPositionVector());
    }

    @Override
    protected void onHit(RayTraceResult result) {
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
