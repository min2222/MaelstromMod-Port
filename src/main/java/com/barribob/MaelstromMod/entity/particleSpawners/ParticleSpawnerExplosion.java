package com.barribob.MaelstromMod.entity.particleSpawners;

import com.barribob.MaelstromMod.entity.util.EntityParticleSpawner;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ParticleSpawnerExplosion extends EntityParticleSpawner {
    public ParticleSpawnerExplosion(Level worldIn) {
        super(worldIn);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            Vec3 pos2 = getPositionVector().add(ModRandom.randVec().scale(1));
            Vec3 pos3 = getPositionVector().add(ModRandom.randVec().scale(1));
            ParticleManager.spawnMaelstromExplosion(world, rand, pos2);
        }
    }
}
