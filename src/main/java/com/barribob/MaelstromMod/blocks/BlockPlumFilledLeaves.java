package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;

import java.util.Random;

public class BlockPlumFilledLeaves extends BlockLeavesBase {
    public BlockPlumFilledLeaves(String name, float hardness, float resistance, SoundType soundType) {
        super(name, hardness, resistance, soundType);
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return ModItems.PLUM;
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, BlockGetter world, BlockPos pos, BlockState state, int fortune) {
        Random rand = world instanceof Level ? ((Level) world).rand : new Random();

        ItemStack drop = new ItemStack(getItemDropped(state, rand, fortune), this.quantityDropped(rand), damageDropped(state));

        drops.add(drop);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1 + random.nextInt(3);
    }
}
