package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class EntityGoldenRune extends Projectile {

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
        return 0.5f + (this.ticksExisted * 0.04f);
    }

    @Override
    public void onUpdate() {
        Vec3 vel = new Vec3(this.motionX, this.motionY, this.motionZ);

        super.onUpdate();

        ModUtils.setEntityVelocity(this, vel);

        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.MAGIC)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(getBlastRadius() * 0.75f, (e) -> this.getDamage(), this.shootingEntity, getPositionVector(), source, 0, 0, false);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result.entityHit != null) {
            return;
        }

        super.onHit(result);
    }

    @Override
    protected void spawnParticles() {
        Vec3 vel = ModUtils.getEntityVelocity(this);
        Vec3 normVel = vel.normalize();
        ModUtils.circleCallback(getBlastRadius(), 36, pos -> {
            float f1 = Mth.sqrt(normVel.x * normVel.x + normVel.z * normVel.z);
            Vec3 outer = pos.rotatePitch((float) (Mth.atan2(normVel.y, f1)))
                    .rotateYaw((float) (Mth.atan2(normVel.x, normVel.z)))
                    .add(getPositionVector())
                    .add(vel)
                    .add(ModRandom.randVec()
                            .scale(ModRandom.getFloat(0.1f)));
            ParticleManager.spawnSwirl(world, outer, ModColors.YELLOW, vel, 5);
        });
    }
}
