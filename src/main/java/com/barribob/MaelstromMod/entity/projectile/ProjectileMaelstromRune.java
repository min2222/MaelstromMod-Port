package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileMaelstromRune extends EntityLargeGoldenRune {
    public ProjectileMaelstromRune(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
    }

    public ProjectileMaelstromRune(Level worldIn) {
        super(worldIn);
    }

    public ProjectileMaelstromRune(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnImpactParticles() {
        ModUtils.performNTimes(10, (i) -> {
            ModUtils.circleCallback(blastRadius, 30,
                    (offset) -> ParticleManager.spawnWisp(world, ModUtils.entityPos(this).add(new Vec3(offset.x, i * 0.5, offset.y)), ModColors.PURPLE, Vec3.ZERO));
            ModUtils.circleCallback(blastRadius - 1, 30,
                    (offset) -> ParticleManager.spawnWisp(world, ModUtils.entityPos(this).add(new Vec3(offset.x, i * 0.5, offset.y)), ModColors.PURPLE, Vec3.ZERO));
        });
    }

    @Override
    protected void spawnParticles() {
        if (this.ticksExisted % 5 == 0) {
            ModUtils.circleCallback(this.blastRadius, 45,
                    (offset) -> ParticleManager.spawnSwirl(world, ModUtils.entityPos(this).add(new Vec3(offset.x, 0.5f, offset.y)), ModColors.PURPLE, ModUtils.getEntityVelocity(this), 5));
            ModUtils.circleCallback(this.blastRadius - 1, 30,
                    (offset) -> ParticleManager.spawnSwirl(world, ModUtils.entityPos(this).add(new Vec3(offset.x, 0.6f, offset.y)), ModColors.PURPLE, ModUtils.getEntityVelocity(this), 5));
            ModUtils.circleCallback(this.blastRadius - 2, 30,
                    (offset) -> ParticleManager.spawnSwirl(world, ModUtils.entityPos(this).add(new Vec3(offset.x, 0.7f, offset.y)), ModColors.PURPLE, ModUtils.getEntityVelocity(this), 5));
        }
    }

    @Override
    protected void blastEffect(LivingEntity e) {
        e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 40, 0));
    }
}
