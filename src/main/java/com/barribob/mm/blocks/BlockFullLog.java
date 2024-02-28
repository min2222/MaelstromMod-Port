package com.barribob.mm.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;

import java.util.Random;

/*
 * Log for a log textured only with bark
 */
public class BlockFullLog extends BlockLogBase {
    private Block log;

    public BlockFullLog(String name, Block regularLog) {
        super(name);
        this.log = regularLog;
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(log);
    }
}
