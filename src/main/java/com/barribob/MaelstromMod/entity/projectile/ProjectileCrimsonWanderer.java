package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.entity.entities.EntityLeveledMob;
import com.barribob.MaelstromMod.entity.entities.gauntlet.EntityCrimsonCrystal;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ProjectileCrimsonWanderer extends Projectile {
    private static final int AGE = 20 * 4;

    public ProjectileCrimsonWanderer(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
    }

    public ProjectileCrimsonWanderer(Level worldIn) {
        super(worldIn);
    }

    public ProjectileCrimsonWanderer(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public void onUpdate() {
        Vec3 prevVel = ModUtils.getEntityVelocity(this);
        super.onUpdate();
        ModUtils.setEntityVelocity(this, prevVel);

        if(!world.isRemote) {
            ModUtils.avoidOtherEntities(this, 0.03, 3, e -> e instanceof ProjectileCrimsonWanderer || e == this.shootingEntity);
        }

        if(!this.world.isRemote && this.ticksExisted > AGE) {
             onImpact();
            this.setDead();
        }
    }

    private void onImpact() {
        if(shootingEntity != null && shootingEntity instanceof EntityLeveledMob) {
            EntityCrimsonCrystal crystal = new EntityCrimsonCrystal(world, (EntityLeveledMob) shootingEntity);
            ModUtils.setEntityPosition(crystal, getPositionVector());
            world.spawnEntity(crystal);
        }
    }

    @Override
    protected void onHit(@Nullable RayTraceResult result) {
        if(!world.isRemote) {
            onImpact();
        }
        super.onHit(result);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            float colorAge = ModUtils.clamp(Math.pow(ticksExisted / (float)AGE, 2), 0.1, 1);

            ParticleManager.spawnSplit(world,
                    getPositionVector().add(ModRandom.randVec().scale(0.25)),
                    new Vec3(1, colorAge, colorAge), Vec3.ZERO);
        }
    }
}
