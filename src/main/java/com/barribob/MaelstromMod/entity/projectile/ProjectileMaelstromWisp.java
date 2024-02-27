package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileMaelstromWisp extends Projectile {
    private static final int PARTICLE_AMOUNT = 6;
    private static final int AREA_FACTOR = 2;

    public ProjectileMaelstromWisp(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
        this.setNoGravity(true);
    }

    public ProjectileMaelstromWisp(Level worldIn) {
        super(worldIn);
    }

    public ProjectileMaelstromWisp(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < this.PARTICLE_AMOUNT; i++) {
            ModUtils.circleCallback(AREA_FACTOR, 30, (pos) -> {
                Vec3 vel = new Vec3(this.motionX, this.motionY, this.motionZ).normalize();

                // Conversion code taken from projectile shoot method
                float f1 = Mth.sqrt(vel.x * vel.x + vel.z * vel.z);
                Vec3 outer = pos.rotatePitch((float) (Mth.atan2(vel.y, f1))).rotateYaw((float) (Mth.atan2(vel.x, vel.z))).add(getPositionVector());
                Vec3 inner = pos.scale(0.85f).rotatePitch((float) (Mth.atan2(vel.y, f1))).rotateYaw((float) (Mth.atan2(vel.x, vel.z))).add(getPositionVector());
                ParticleManager.spawnMaelstromSmoke(world, rand, outer, true);
                ParticleManager.spawnMaelstromPotionParticle(world, rand, inner, false);
            });
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.MAGIC)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(AREA_FACTOR, (e) -> this.getDamage(), this.shootingEntity, getPositionVector(), source);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        // Only destroy if the collision is a block
        if (result.entityHit != null) {
            return;
        }

        super.onHit(result);
    }
}
