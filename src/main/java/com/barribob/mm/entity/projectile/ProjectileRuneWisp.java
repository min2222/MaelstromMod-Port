package com.barribob.mm.entity.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileRuneWisp extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 6;
    private static final int RADIUS = 2;

    public ProjectileRuneWisp(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileRuneWisp(Level worldIn) {
        super(worldIn);
    }

    public ProjectileRuneWisp(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < PARTICLE_AMOUNT; i++) {
            ModUtils.circleCallback(RADIUS, 30, (pos) -> {
                Vec3 vel = new Vec3(this.motionX, this.motionY, this.motionZ).normalize();

                // Conversion code taken from projectile shoot method
                float f1 = Mth.sqrt(vel.x * vel.x + vel.z * vel.z);
                Vec3 outer = pos.rotatePitch((float) (Mth.atan2(vel.y, f1))).rotateYaw((float) (Mth.atan2(vel.x, vel.z))).add(position());
                Vec3 inner = pos.scale(0.85f).rotatePitch((float) (Mth.atan2(vel.y, f1))).rotateYaw((float) (Mth.atan2(vel.x, vel.z))).add(position());
                ParticleManager.spawnWisp(world, outer, this.getElement().particleColor, Vec3.ZERO);
                ParticleManager.spawnEffect(world, inner, getElement().particleColor);
            });
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.shootingEntity != null && this.tickCount % 4 == 1) {
            float knockbackFactor = 1.0f + this.getKnockback() * 0.3f;
            int fireFactor = this.isBurning() ? 5 : 0;
            ModUtils.handleAreaImpact(
                    RADIUS,
                    (e) -> this.getDamage(),
                    this.shootingEntity,
                    this.position(),
                    ModDamageSource.causeElementalThrownDamage(this, this.shootingEntity, this.getElement()),
                    knockbackFactor,
                    fireFactor);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        // Only destroy if the collision is a block
        if (result.entityHit != null) {
            return;
        }

        super.onHit(result);
    }
}
