package com.barribob.mm.blocks;

import net.minecraft.world.level.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/**
 * A block desined to update lighting in a seamless manner
 */
public class BlockLightingUpdater extends BlockBase {
    public BlockLightingUpdater(String name, Material material) {
        super(name, material);
    }

    public EnumBlockRenderType getRenderType(BlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    public AABB getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean canCollideCheck(BlockState state, boolean hitIfLiquid) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean isReplaceable(BlockGetter worldIn, BlockPos pos) {
        return true;
    }

    public BlockFaceShape getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }
}
