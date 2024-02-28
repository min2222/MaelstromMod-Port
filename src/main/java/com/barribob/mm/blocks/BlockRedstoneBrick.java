package com.barribob.mm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;

public class BlockRedstoneBrick extends BlockBase {
    public BlockRedstoneBrick(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(name, material, hardness, resistance, soundType);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    ;

    @Override
    public int getWeakPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return 15;
    }

    ;

}
