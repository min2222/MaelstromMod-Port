package com.barribob.MaelstromMod.world.gen.golden_ruins;

import java.util.Random;

import com.barribob.MaelstromMod.entity.tileentity.TileEntityMobSpawner;
import com.barribob.MaelstromMod.init.ModBlocks;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.Reference;
import com.barribob.MaelstromMod.util.handlers.LootTableHandler;
import com.barribob.MaelstromMod.world.gen.ModStructureTemplate;
import com.barribob.MaelstromMod.world.gen.WorldGenStructure;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;

/**
 * 
 * The specific template used for generating the maelstrom fortress
 *
 */
public class RuinsTemplate extends ModStructureTemplate
{
    public RuinsTemplate()
    {
    }

    public RuinsTemplate(TemplateManager manager, String type, BlockPos pos, Rotation rotation, int distance, boolean overwriteIn)
    {
	super(manager, type, pos, distance, rotation, overwriteIn);
    }

    /**
     * Loads structure block data markers and handles them by their name
     */
    @Override
    protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb)
    {
	if (function.startsWith("chest"))
	{
	    worldIn.setBlockToAir(pos);
	    BlockPos blockpos = pos.down();
	    if (rand.nextFloat() < 0.3)
	    {
		if (sbb.isVecInside(blockpos))
		{
		    TileEntity tileentity = worldIn.getTileEntity(blockpos);

		    if (tileentity instanceof TileEntityChest)
		    {
			((TileEntityChest) tileentity).setLootTable(LootTableHandler.GOLDEN_RUINS, rand.nextLong());
		    }
		}
	    }
	    else
	    {
		worldIn.setBlockToAir(blockpos);
	    }
	}
	else if (function.startsWith("final_chest"))
	{
	    worldIn.setBlockToAir(pos);
	    BlockPos blockpos = pos.down();

	    if (sbb.isVecInside(blockpos))
	    {
		TileEntity tileentity = worldIn.getTileEntity(blockpos);

		if (tileentity instanceof TileEntityChest)
		{
		    ((TileEntityChest) tileentity).setLootTable(LootTableHandler.GOLDEN_RUINS, rand.nextLong());
		}
	    }
	}
	else if (function.startsWith("mob"))
	{
	    if (rand.nextFloat() > 0.3)
	    {
		worldIn.setBlockState(pos, ModBlocks.DISAPPEARING_SPAWNER.getDefaultState(), 2);
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof TileEntityMobSpawner)
		{
		    String[] entities = { "golden_mage", "golden_shade", "golden_pillar" };
		    int[] maxAmounts = { 3, 3, 1 };
		    int i = ModRandom.range(0, 3);
		    ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setEntities(new ResourceLocation(Reference.MOD_ID + ":" + entities[i]),
			    ModRandom.range(1, maxAmounts[i] + 1));
		}
	    }
	    else
	    {
		worldIn.setBlockToAir(pos);
	    }
	}
	else if (function.startsWith("boss"))
	{
	    worldIn.setBlockState(pos, ModBlocks.DISAPPEARING_SPAWNER.getDefaultState(), 2);
	    TileEntity tileentity = worldIn.getTileEntity(pos);

	    if (tileentity instanceof TileEntityMobSpawner)
	    {
		((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setEntities(new ResourceLocation(Reference.MOD_ID + ":golden_boss"), 1);
	    }
	}
	else if (function.startsWith("lava"))
	{
	    if (rand.nextInt(3) == 0)
	    {
		WorldGenStructure lavaPool = new WorldGenStructure("golden_ruins/" + ModRandom.choice(new String[] { "lava_fountain", "lava_pool", "lava_statue" }));
		lavaPool.generate(worldIn, rand, pos.add(new BlockPos(-2, -2, -2)));
	    }
	    else
	    {
		worldIn.setBlockToAir(pos);
	    }
	}
    }

    @Override
    public String templateLocation()
    {
	return "golden_ruins";
    }
}
