package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.entity.tileentity.TileEntityBossSpawner;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Material;

public class BlockBossSpawner extends BlockDisappearingSpawner {
    public BlockBossSpawner(String name) {
        super(name, Material.ROCK);
    }

    @Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityBossSpawner();
    }
}
