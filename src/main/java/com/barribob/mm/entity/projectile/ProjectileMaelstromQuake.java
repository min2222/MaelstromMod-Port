package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ProjectileMaelstromQuake extends ProjectileQuake {
    public static final int PARTICLE_AMOUNT = 4;

    public ProjectileMaelstromQuake(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage, null);
        this.setSize(0.25f, 1);
    }

    public ProjectileMaelstromQuake(Level worldIn) {
        super(worldIn);
    }

    public ProjectileMaelstromQuake(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        BlockState block = level.getBlockState(this.blockPosition());
        if (block.isFullCube()) {
            Vec3 color = new Vec3(0.5, 0.3, 0.5);
            Vec3 vel = new Vec3(0, -0.1, 0);
            for (int i = 0; i < ProjectileMaelstromQuake.PARTICLE_AMOUNT; i++) {
                Vec3 pos = ModUtils.entityPos(this).add(new Vec3(ModRandom.getFloat(AREA_FACTOR), 0.75 + ModRandom.getFloat(0.25f), ModRandom.getFloat(AREA_FACTOR)));
                ParticleManager.spawnDarkFlames(level, random, pos, vel);
            }
        }
    }

    @Override
    protected void playQuakeSound() {
        if (random.nextInt(10) == 0) {
            super.playQuakeSound();
        }
    }
}
