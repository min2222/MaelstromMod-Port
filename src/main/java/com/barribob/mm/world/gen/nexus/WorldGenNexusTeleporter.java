package com.barribob.mm.world.gen.nexus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Rotation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Random;

import com.barribob.mm.entity.tileentity.TileEntityTeleporter;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.world.gen.WorldGenStructure;

public class WorldGenNexusTeleporter extends WorldGenStructure {
    Vec3 offset;

    public WorldGenNexusTeleporter(Vec3 offset) {
        super("nexus/nexus_teleporter");
        this.offset = offset;
    }

    public boolean generate(Level worldIn, Random rand, BlockPos position, Rotation rotation) {
        generateStructure(worldIn, position, rotation);
        return true;
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level world, Random rand) {
        if (function.startsWith("teleporter")) {
            world.setBlockState(pos, ModBlocks.NEXUS_TELEPORTER.getDefaultState());
            BlockEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityTeleporter) {
                ((TileEntityTeleporter) tileentity).setRelTeleportPos(offset);
            }
        }
    }
}
