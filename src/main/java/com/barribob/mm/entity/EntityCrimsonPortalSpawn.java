package com.barribob.mm.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import com.barribob.mm.entity.util.EntityPortalSpawn;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.level.Level;

public class EntityCrimsonPortalSpawn extends EntityPortalSpawn {
    public EntityCrimsonPortalSpawn(Level worldIn) {
        super(worldIn);
    }

    public EntityCrimsonPortalSpawn(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        int offset = 0;
        int sectors = 90;
        int degreesPerSector = 360 / sectors;
        double size = 3;
        for (int i = 0; i < sectors; i++) {
            double x = this.getX() + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            double y = this.getY() + 3.5 + Math.sin(i * degreesPerSector) * Math.cos(this.tickCount) * size + offset;
            double z = this.getZ() + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            ParticleManager.spawnEffect(level, new Vec3(x, y, this.getZ()), ModColors.RED);
            ParticleManager.spawnEffect(level, new Vec3(this.getX(), y, z), ModColors.RED);
        }
    }

    @Override
    protected Block getRimBlock() {
        return ModBlocks.FURNACE_BRICKS;
    }

    @Override
    protected Block getPortalBlock() {
        return ModBlocks.CRIMSON_PORTAL;
    }
}
