package com.barribob.mm.blocks;

import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockGrate extends BlockBase {
    protected static final AABB BOTTOM_AABB = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D);
    protected static final AABB TOP_AABB = new AABB(0.0D, 0.8125D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final EnumProperty<TrapDoorBlock.DoorHalf> HALF = EnumProperty.<TrapDoorBlock.DoorHalf>create("half", TrapDoorBlock.DoorHalf.class);

    public BlockGrate(String name, Material material) {
        super(name, material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, TrapDoorBlock.DoorHalf.BOTTOM));
    }

    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        AABB axisalignedbb;

        if (state.getValue(HALF) == TrapDoorBlock.DoorHalf.TOP) {
            axisalignedbb = TOP_AABB;
        } else {
            axisalignedbb = BOTTOM_AABB;
        }

        return axisalignedbb;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState iblockstate = this.getDefaultState();

        if (facing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.withProperty(HALF, hitY > 0.5F ? TrapDoorBlock.DoorHalf.TOP : TrapDoorBlock.DoorHalf.BOTTOM);
        } else {
            iblockstate = iblockstate.withProperty(HALF, facing == Direction.UP ? TrapDoorBlock.DoorHalf.BOTTOM : TrapDoorBlock.DoorHalf.TOP);
        }

        return iblockstate;
    }

    @Override
    public boolean canPlaceBlockOnSide(Level worldIn, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(HALF, (meta & 8) == 0 ? TrapDoorBlock.DoorHalf.BOTTOM : TrapDoorBlock.DoorHalf.TOP);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public int getMetaFromState(BlockState state) {
        int i = 0;

        if (state.getValue(HALF) == TrapDoorBlock.DoorHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    @Override
    protected StateDefinition createBlockState() {
        return new StateDefinition(this, new IProperty[]{HALF});
    }

    @Override
    public BlockFaceShape getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return (face == Direction.UP && state.getValue(HALF) == TrapDoorBlock.DoorHalf.TOP
                || face == Direction.DOWN && state.getValue(HALF) == TrapDoorBlock.DoorHalf.BOTTOM) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}

