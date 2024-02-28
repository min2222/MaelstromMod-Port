package com.barribob.mm.world.gen.cliff;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

import com.barribob.mm.entity.entities.EntityBeast;
import com.barribob.mm.entity.tileentity.TileEntityMobSpawner;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.world.gen.ModStructureTemplate;

/**
 * The specific template used for generating the maelstrom fortress
 */
public class HoleTempleTemplate extends ModStructureTemplate {
    public HoleTempleTemplate() {
    }

    public HoleTempleTemplate(TemplateManager manager, String type, BlockPos pos, Rotation rotation, int distance, boolean overwriteIn) {
        super(manager, type, pos, distance, rotation, overwriteIn);
    }

    /**
     * Loads structure block data markers and handles them by their name
     */
    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand, StructureBoundingBox sbb) {
        worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
        BlockEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(ModEntities.getID(EntityBeast.class), 1, LevelHandler.CLIFF_OVERWORLD, 20);
        }
    }

    @Override
    public String templateLocation() {
        return "cliff";
    }
}
