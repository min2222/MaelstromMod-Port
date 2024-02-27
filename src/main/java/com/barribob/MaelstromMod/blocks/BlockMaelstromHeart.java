package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.entity.util.EntityMaelstromTowerDestroyer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.Random;

public class BlockMaelstromHeart extends BlockBase {
    public BlockMaelstromHeart(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(name, material, hardness, resistance, soundType);
    }

    @Override
    public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
        EntityMaelstromTowerDestroyer entity = new EntityMaelstromTowerDestroyer(worldIn, new Vec3(pos).subtract(new Vec3(21, 48, 13)));
        worldIn.spawnEntity(entity);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }
}
