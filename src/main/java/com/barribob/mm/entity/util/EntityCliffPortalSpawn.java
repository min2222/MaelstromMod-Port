package com.barribob.mm.entity.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.level.Level;

public class EntityCliffPortalSpawn extends EntityPortalSpawn {
    public EntityCliffPortalSpawn(Level worldIn) {
        super(worldIn);
    }

    public EntityCliffPortalSpawn(Level worldIn, float x, float y, float z) {
        super(worldIn, x, y, z);
    }

    /*
     * Spawns a bunch of particles in fancy order using sin and cos functions
     */
    @Override
    protected void spawnParticles() {
        int offset = 0;
        int sectors = 90;
        int degreesPerSector = 360 / sectors;
        double size = 3;
        for (int i = 0; i < sectors; i++) {
            double x = this.posX + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            double y = this.posY + 3.5 + Math.sin(i * degreesPerSector) * Math.cos(this.tickCount) * size + offset;
            double z = this.posZ + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            ParticleManager.spawnEffect(world, new Vec3(x, y, this.posZ + 0.5), ModColors.YELLOW);
            ParticleManager.spawnEffect(world, new Vec3(this.posX + 0.5, y, z), ModColors.YELLOW);
        }
    }

    @Override
    protected Block getRimBlock() {
        return Blocks.QUARTZ_BLOCK;
    }

    @Override
    protected Block getPortalBlock() {
        return ModBlocks.CLIFF_PORTAL;
    }
}
