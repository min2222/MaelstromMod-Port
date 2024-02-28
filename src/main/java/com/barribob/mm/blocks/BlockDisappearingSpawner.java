package com.barribob.mm.blocks;

import com.barribob.mm.entity.tileentity.TileEntityDisappearingSpawner;

import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Spawns maelstrom mobs, and then disappears, like a one-time mob spawner
 */
public class BlockDisappearingSpawner extends BaseEntityBlock {
    public BlockDisappearingSpawner(String name, Material material) {
        super(BlockBehaviour.Properties.copy(Blocks.BEDROCK));
    }

    @Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityDisappearingSpawner();
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
    /**
     * The type of render function called. MODEL for mixed tesr and static model,
     * MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to
     * skip all rendering
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
