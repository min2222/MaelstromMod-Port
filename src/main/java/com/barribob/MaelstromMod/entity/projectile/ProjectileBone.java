package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileBone extends Projectile {
    private static final int IMPACT_PARTICLE_AMOUNT = 10;
    private static final int EXPOSION_AREA_FACTOR = 1;

    public ProjectileBone(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileBone(Level worldIn) {
        super(worldIn);
    }

    public ProjectileBone(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < IMPACT_PARTICLE_AMOUNT; i++) {
            Vec3 vec1 = ModRandom.randVec()
                    .scale(EXPOSION_AREA_FACTOR * 2)
                    .add(getPositionVector())
                    .add(ModUtils.yVec(0.8f));
            ParticleManager.spawnEffect(world, vec1, ModColors.WHITE);
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, (e) -> this.getDamage(), this.shootingEntity, this.getPositionVector(),
                ModDamageSource.causeElementalExplosionDamage(this.shootingEntity, getElement()));
        this.playSound(SoundEvents.ENTITY_SKELETON_HURT, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }
}
