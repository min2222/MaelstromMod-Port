package com.barribob.mm.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBlockUpdater {
    public void update(Level world, BlockPos pos);
}
