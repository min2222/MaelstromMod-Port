package com.barribob.mm.entity.util;

import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EntityAzurePortalSpawn extends EntityPortalSpawn {
    public EntityAzurePortalSpawn(Level worldIn) {
        super(ModEntities.AZURE_PORTAL_SPAWN.get(), worldIn);
    }

    public EntityAzurePortalSpawn(Level worldIn, float x, float y, float z) {
        super(ModEntities.AZURE_PORTAL_SPAWN.get(), worldIn, x, y, z);
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
            double x = this.getX() + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            double y = this.getY() + 3.5 + Math.sin(i * degreesPerSector) * Math.cos(this.tickCount) * size + offset;
            double z = this.getZ() + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            ParticleManager.spawnEffect(level, new Vec3(x, y, this.getZ() + 0.5), new Vec3(0.3, 0.4, 1));
            ParticleManager.spawnEffect(level, new Vec3(this.getX() + 0.5, y, z), new Vec3(0.3, 0.4, 1));
        }
    }

    @Override
    protected Block getRimBlock() {
        return Blocks.QUARTZ_BLOCK;
    }

    @Override
    protected Block getPortalBlock() {
        return ModBlocks.AZURE_PORTAL;
    }
}