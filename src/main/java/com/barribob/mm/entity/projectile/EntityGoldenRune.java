package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityGoldenRune extends ModProjectile {

    public EntityGoldenRune(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
        this.setNoGravity(true);
    }

    public EntityGoldenRune(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public EntityGoldenRune(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    private float getBlastRadius() {
        return 0.5f + (this.tickCount * 0.04f);
    }

    @Override
    public void tick() {
        Vec3 vel = this.getDeltaMovement();

        super.tick();

        ModUtils.setEntityVelocity(this, vel);

        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.MAGIC)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(getBlastRadius() * 0.75f, (e) -> this.getDamage(), this.shootingEntity, position(), source, 0, 0, false);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() != null) {
            return;
        }

        super.onHitEntity(result);
    }

    @Override
    protected void spawnParticles() {
        Vec3 vel = ModUtils.getEntityVelocity(this);
        Vec3 normVel = vel.normalize();
        ModUtils.circleCallback(getBlastRadius(), 36, pos -> {
            float f1 = (float) Math.sqrt(normVel.x * normVel.x + normVel.z * normVel.z);
            Vec3 outer = pos.xRot((float) (Mth.atan2(normVel.y, f1)))
                    .yRot((float) (Mth.atan2(normVel.x, normVel.z)))
                    .add(position())
                    .add(vel)
                    .add(ModRandom.randVec()
                            .scale(ModRandom.getFloat(0.1f)));
            ParticleManager.spawnSwirl(level, outer, ModColors.YELLOW, vel, 5);
        });
    }
}
