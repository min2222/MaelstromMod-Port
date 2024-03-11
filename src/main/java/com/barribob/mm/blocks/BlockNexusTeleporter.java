package com.barribob.mm.blocks;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

import java.util.Random;

import com.barribob.mm.entity.tileentity.TileEntityTeleporter;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.util.ModRandom;

public class BlockNexusTeleporter extends Block implements EntityBlock {
    public BlockNexusTeleporter(String name, Material material, SoundType soundType) {
        super(name, material, 50, 2000, soundType);
        this.hasTileEntity = true;
        this.setTickRandomly(true);
    }

    @Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityTeleporter();
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updatedmod
     */
    @Override
    public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public void onBlockAdded(Level worldIn, BlockPos pos, BlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        worldIn.scheduleBlockUpdate(pos, this, 100 + ModRandom.range(0, 100), 0); // All blocks in initial range get updated
    }

    @Override
    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);

        BlockPos[] positions = new BlockPos[]{pos.down(), pos.up(), pos.west(), pos.east(), pos.north(), pos.south()};
        boolean wasAir = false;

        for (BlockPos nPos : positions) {
            // Remove all surrounding lighting updater blocks after the seconds pass
            if (worldIn.getBlockState(nPos).getBlock().equals(ModBlocks.LIGHTING_UPDATER)) {
                worldIn.setBlockToAir(nPos);
            } else if (worldIn.isAirBlock(nPos)) // Set all surrounding air blocks to lighting updater blocks
            {
                wasAir = true;
                worldIn.setBlockState(nPos, ModBlocks.LIGHTING_UPDATER.getDefaultState());
            }
        }

        if (wasAir) {
            worldIn.scheduleBlockUpdate(pos, this, 1, 0); // Next update will remove the set lighting updater blocks
        }
    }
}