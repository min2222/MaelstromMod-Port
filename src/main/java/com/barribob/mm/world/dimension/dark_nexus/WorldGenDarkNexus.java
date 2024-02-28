package com.barribob.mm.world.dimension.dark_nexus;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.Rotation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.level.Level;

import java.util.Random;

import com.barribob.mm.entity.entities.Herobrine;
import com.barribob.mm.entity.tileentity.TileEntityMobSpawner;
import com.barribob.mm.entity.tileentity.MobSpawnerLogic.MobSpawnData;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.world.gen.WorldGenStructure;

public class WorldGenDarkNexus extends WorldGenStructure {
    public WorldGenDarkNexus() {
        super("nexus/dark_nexus");
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        this.generateStructure(worldIn, position, Rotation.NONE);
        return true;
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand) {
        worldIn.setBlockToAir(pos);
        if (function.startsWith("herobrine")) {
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(new MobSpawnData(ModEntities.getID(Herobrine.class), Element.NONE), 1, LevelHandler.INVASION, 20);
            }
        } else if (function.startsWith("cookie_stash")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ItemStack herobrinesCookies = new ItemStack(Items.COOKIE, 13);
                herobrinesCookies.setStackDisplayName(new TextComponentTranslation("herobrines_cookies").getFormattedText());
                ((TileEntityLockableLoot) tileentity).setInventorySlotContents(13, herobrinesCookies);
            }
        }
    }
}
