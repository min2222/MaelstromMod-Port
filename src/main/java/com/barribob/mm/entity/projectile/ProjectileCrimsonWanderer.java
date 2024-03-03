package com.barribob.mm.entity.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.entities.gauntlet.EntityCrimsonCrystal;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

public class ProjectileCrimsonWanderer extends ModProjectile {
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
    public void tick() {
        Vec3 prevVel = ModUtils.getEntityVelocity(this);
        super.tick();
        ModUtils.setEntityVelocity(this, prevVel);

        if(!level.isClientSide) {
            ModUtils.avoidOtherEntities(this, 0.03, 3, e -> e instanceof ProjectileCrimsonWanderer || e == this.shootingEntity);
        }

        if(!this.level.isClientSide && this.tickCount > AGE) {
             onImpact();
            this.discard();
        }
    }

    private void onImpact() {
        if(shootingEntity != null && shootingEntity instanceof EntityLeveledMob) {
            EntityCrimsonCrystal crystal = new EntityCrimsonCrystal(level, (EntityLeveledMob) shootingEntity);
            ModUtils.setEntityPosition(crystal, position());
            level.addFreshEntity(crystal);
        }
    }

    @Override
    protected void onHit(@Nullable HitResult result) {
        if(!level.isClientSide) {
            onImpact();
        }
        super.onHit(result);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            float colorAge = ModUtils.clamp(Math.pow(tickCount / (float)AGE, 2), 0.1, 1);

            ParticleManager.spawnSplit(level,
                    position().add(ModRandom.randVec().scale(0.25)),
                    new Vec3(1, colorAge, colorAge), Vec3.ZERO);
        }
    }
}
