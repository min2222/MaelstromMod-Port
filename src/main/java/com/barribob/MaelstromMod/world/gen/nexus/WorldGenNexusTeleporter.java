package com.barribob.MaelstromMod.world.gen.nexus;

import com.barribob.MaelstromMod.entity.tileentity.TileEntityTeleporter;
import com.barribob.MaelstromMod.init.ModBlocks;
import com.barribob.MaelstromMod.world.gen.WorldGenStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Rotation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Random;

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
