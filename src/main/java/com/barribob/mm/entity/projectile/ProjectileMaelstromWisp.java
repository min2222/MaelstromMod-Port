package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileMaelstromWisp extends ModProjectile {
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
        for (int i = 0; i < ProjectileMaelstromWisp.PARTICLE_AMOUNT; i++) {
            ModUtils.circleCallback(AREA_FACTOR, 30, (pos) -> {
                Vec3 vel = this.getDeltaMovement().normalize();

                // Conversion code taken from projectile shoot method
                float f1 = (float) Math.sqrt(vel.x * vel.x + vel.z * vel.z);
                Vec3 outer = pos.xRot((float) (Mth.atan2(vel.y, f1))).yRot((float) (Mth.atan2(vel.x, vel.z))).add(position());
                Vec3 inner = pos.scale(0.85f).xRot((float) (Mth.atan2(vel.y, f1))).yRot((float) (Mth.atan2(vel.x, vel.z))).add(position());
                ParticleManager.spawnMaelstromSmoke(level, random, outer, true);
                ParticleManager.spawnMaelstromPotionParticle(level, random, inner, false);
            });
        }
    }

    @Override
    public void tick() {
        super.tick();
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.MAGIC)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .element(getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(AREA_FACTOR, (e) -> this.getDamage(), this.shootingEntity, position(), source);
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
