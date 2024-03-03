package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileGoldenMissile extends ModProjectile {
    public ProjectileGoldenMissile(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileGoldenMissile(Level worldIn) {
        super(worldIn);
    }

    public ProjectileGoldenMissile(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        ParticleManager.spawnSwirl2(level, this.position(), ModColors.YELLOW, Vec3.ZERO);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.PROJECTILE)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleBulletImpact(result.getEntity(), this, this.getDamage(), source);
        super.onHitEntity(result);
    }
}
