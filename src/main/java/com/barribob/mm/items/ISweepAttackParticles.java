package com.barribob.mm.items;

import net.minecraft.world.phys.Vec3;

/**
 * Adds additional sweep attack particle to items with sweep attacks Works in
 * conjunction with ParticleSpawnerSwordSwing
 *
 * @see com.barribob.mm.entity.particleSpawners.ParticleSpawnerSwordSwing
 */
public interface ISweepAttackParticles {
    public Vec3 getColor();

    public float getSize();
}
