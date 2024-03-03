package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

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
                Vec3 vel = this.getDeltaMovement().normalize();

                // Conversion code taken from projectile shoot method
                float f1 = (float) Math.sqrt(vel.x * vel.x + vel.z * vel.z);
                Vec3 outer = pos.xRot((float) (Mth.atan2(vel.y, f1))).yRot((float) (Mth.atan2(vel.x, vel.z))).add(position());
                Vec3 inner = pos.scale(0.85f).xRot((float) (Mth.atan2(vel.y, f1))).yRot((float) (Mth.atan2(vel.x, vel.z))).add(position());
                ParticleManager.spawnWisp(level, outer, this.getElement().particleColor, Vec3.ZERO);
                ParticleManager.spawnEffect(level, inner, getElement().particleColor);
            });
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shootingEntity != null && this.tickCount % 4 == 1) {
            float knockbackFactor = 1.0f + this.getKnockback() * 0.3f;
            int fireFactor = this.isOnFire() ? 5 : 0;
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
    protected void onHitEntity(EntityHitResult result) {
        // Only destroy if the collision is a block
        if (result.getEntity() != null) {
            return;
        }

        super.onHitEntity(result);
    }
}
