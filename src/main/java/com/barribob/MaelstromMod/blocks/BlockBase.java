package com.barribob.MaelstromMod.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

/**
 * The base class for a new mod block
 */
public class BlockBase extends Block {
    public BlockBase(Material material) {
        super(BlockBehaviour.Properties.of(material));
    }

    public BlockBase(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(BlockBehaviour.Properties.of(material).strength(hardness, resistance).sound(soundType));
    }
}
