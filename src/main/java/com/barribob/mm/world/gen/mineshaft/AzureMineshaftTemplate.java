package com.barribob.mm.world.gen.mineshaft;

import net.minecraft.core.BlockPos;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Rotation;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

import com.barribob.mm.entity.entities.EntityAzureVillager;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.world.gen.ModStructureTemplate;

/**
 * The specific template used for generating the maelstrom fortress
 */
public class AzureMineshaftTemplate extends ModStructureTemplate {
    public AzureMineshaftTemplate() {
    }

    public AzureMineshaftTemplate(TemplateManager manager, String type, BlockPos pos, Rotation rotation, int distance, boolean overwriteIn) {
        super(manager, type, pos, distance, rotation, overwriteIn);
    }

    /**
     * Loads structure block data markers and handles them by their name
     */
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand, StructureBoundingBox sbb) {
        if (function.startsWith("chest")) {
            worldIn.setBlockToAir(pos);
            BlockPos blockpos = pos.down();

            if (sbb.isVecInside(blockpos)) {
                BlockEntity tileentity = worldIn.getTileEntity(blockpos);

                if (tileentity instanceof TileEntityChest) {
                    ((TileEntityChest) tileentity).setLootTable(LootTableHandler.AZURE_MINESHAFT, rand.nextLong());
                }
            }
        } else if (function.startsWith("villager_below")) {
            worldIn.setBlockToAir(pos);
            EntityAzureVillager entity = new EntityAzureVillager(worldIn);
            entity.setPosition((double) pos.getX() + 0.5D, (double) pos.getY() - 0.5D, (double) pos.getZ() + 0.5D);
            worldIn.spawnEntity(entity);
        } else if (function.startsWith("minecart")) {
            worldIn.setBlockToAir(pos);
            EntityMinecartEmpty minecart = new EntityMinecartEmpty(worldIn);
            minecart.setPosition((double) pos.getX() + 0.5D, (double) pos.getY() - 0.5D, (double) pos.getZ() + 0.5D);
            worldIn.spawnEntity(minecart);
        }
    }

    @Override
    public String templateLocation() {
        return "mineshaft";
    }
}
