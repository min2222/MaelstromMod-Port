package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

/**
 * Projectile for herobrins slash attack
 */
public class ProjectileHerobrineQuake extends ProjectileQuake {
    public static final int PARTICLE_AMOUNT = 1;

    public ProjectileHerobrineQuake(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage, null);
        this.setSize(0.25f, 1);
    }

    public ProjectileHerobrineQuake(Level worldIn) {
        super(worldIn);
    }

    public ProjectileHerobrineQuake(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Spawns maelstrom particles in an incomplete column
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        BlockState block = level.getBlockState(this.blockPosition());
        if (block.isFullCube()) {
            Vec3 color = new Vec3(0.5, 0.3, 0.5);
            Vec3 vel = new Vec3(0, 0.1, 0);
            for (int i = 0; i < ProjectileHerobrineQuake.PARTICLE_AMOUNT; i++) {
                float height = 2 + ModRandom.getFloat(0.5f);
                for (float y = 0; y < height; y += 0.2f) {
                    Vec3 pos = ModUtils.entityPos(this).add(new Vec3(this.getDeltaMovement().x * ModRandom.getFloat(0.5f), y, this.getDeltaMovement().z * ModRandom.getFloat(0.5f)));
                    ParticleManager.spawnSwirl(level, pos, ModColors.AZURE, Vec3.ZERO, ModRandom.range(10, 15));
                }
            }
        }
    }
}
