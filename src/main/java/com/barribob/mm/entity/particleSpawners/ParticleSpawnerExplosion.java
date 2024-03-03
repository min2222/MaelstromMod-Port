package com.barribob.mm.entity.particleSpawners;

import com.barribob.mm.entity.util.EntityParticleSpawner;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleSpawnerExplosion extends EntityParticleSpawner {
    public ParticleSpawnerExplosion(Level worldIn) {
        super(ModEntities.PARTICLE_SPAWNER_EXPLOSION.get(), worldIn);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < 4; i++) {
            Vec3 pos2 = position().add(ModRandom.randVec().scale(1));
            ParticleManager.spawnMaelstromExplosion(level, random, pos2);
        }
    }
}
