package com.barribob.mm.entity.particleSpawners;

import com.barribob.mm.entity.util.EntityParticleSpawner;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleSpawnerRainbow extends EntityParticleSpawner {
    public ParticleSpawnerRainbow(Level worldIn) {
        super(worldIn);
    }

    @Override
    protected void spawnParticles() {
        ParticleManager.spawnFluff(level, position().add(ModUtils.yVec(2.0f)), ModColors.RED, Vec3.ZERO);
        ParticleManager.spawnFluff(level, position().add(ModUtils.yVec(1.7f)), ModColors.ORANGE, Vec3.ZERO);
        ParticleManager.spawnFluff(level, position().add(ModUtils.yVec(1.4f)), ModColors.YELLOW, Vec3.ZERO);
        ParticleManager.spawnFluff(level, position().add(ModUtils.yVec(1.1f)), ModColors.GREEN, Vec3.ZERO);
        ParticleManager.spawnFluff(level, position().add(ModUtils.yVec(0.8f)), ModColors.BLUE, Vec3.ZERO);
        ParticleManager.spawnFluff(level, position().add(ModUtils.yVec(0.5f)), ModColors.PURPLE, Vec3.ZERO);
    }
}
