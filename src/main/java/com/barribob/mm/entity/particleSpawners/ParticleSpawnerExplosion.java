package com.barribob.mm.entity.particleSpawners;

import net.minecraft.world.phys.Vec3;

import com.barribob.mm.entity.util.EntityParticleSpawner;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.level.Level;

public class ParticleSpawnerExplosion extends EntityParticleSpawner {
    public ParticleSpawnerExplosion(Level worldIn) {
        super(worldIn);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            Vec3 pos2 = position().add(ModRandom.randVec().scale(1));
            Vec3 pos3 = position().add(ModRandom.randVec().scale(1));
            ParticleManager.spawnMaelstromExplosion(world, rand, pos2);
        }
    }
}
