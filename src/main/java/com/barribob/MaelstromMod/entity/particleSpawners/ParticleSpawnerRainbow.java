package com.barribob.MaelstromMod.entity.particleSpawners;

import com.barribob.MaelstromMod.entity.util.EntityParticleSpawner;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ParticleSpawnerRainbow extends EntityParticleSpawner {
    public ParticleSpawnerRainbow(Level worldIn) {
        super(worldIn);
    }

    @Override
    protected void spawnParticles() {
        ParticleManager.spawnFluff(world, getPositionVector().add(ModUtils.yVec(2.0f)), ModColors.RED, Vec3.ZERO);
        ParticleManager.spawnFluff(world, getPositionVector().add(ModUtils.yVec(1.7f)), ModColors.ORANGE, Vec3.ZERO);
        ParticleManager.spawnFluff(world, getPositionVector().add(ModUtils.yVec(1.4f)), ModColors.YELLOW, Vec3.ZERO);
        ParticleManager.spawnFluff(world, getPositionVector().add(ModUtils.yVec(1.1f)), ModColors.GREEN, Vec3.ZERO);
        ParticleManager.spawnFluff(world, getPositionVector().add(ModUtils.yVec(0.8f)), ModColors.BLUE, Vec3.ZERO);
        ParticleManager.spawnFluff(world, getPositionVector().add(ModUtils.yVec(0.5f)), ModColors.PURPLE, Vec3.ZERO);
    }
}
