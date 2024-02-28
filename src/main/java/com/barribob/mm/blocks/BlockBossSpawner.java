package com.barribob.mm.blocks;

import com.barribob.mm.entity.tileentity.TileEntityBossSpawner;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Material;

public class BlockBossSpawner extends BlockDisappearingSpawner {
    public BlockBossSpawner(String name) {
        super(name, Material.STONE);
    }

    @Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityBossSpawner();
    }
}
