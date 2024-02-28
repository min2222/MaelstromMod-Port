package com.barribob.mm.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.potion.PotionEffect;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileStatueMaelstromMissile extends ModProjectile {
    public ProjectileStatueMaelstromMissile(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileStatueMaelstromMissile(Level worldIn) {
        super(worldIn);
    }

    public ProjectileStatueMaelstromMissile(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        ParticleManager.spawnSwirl2(world, this.position(), ModColors.PURPLE, Vec3.ZERO);
    }

    @Override
    protected void onHit(HitResult result) {
        if(!level.isClientSide && result.entityHit instanceof LivingEntity) {
            ((LivingEntity)result.entityHit).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 0));
        }
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.PROJECTILE)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleBulletImpact(result.entityHit, this, this.getDamage(), source);
        super.onHit(result);
    }
}
