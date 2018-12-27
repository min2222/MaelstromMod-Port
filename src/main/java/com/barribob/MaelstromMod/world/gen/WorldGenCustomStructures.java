package com.barribob.MaelstromMod.world.gen;

import java.util.ArrayList;
import java.util.Random;

import com.barribob.MaelstromMod.init.BiomeInit;
import com.barribob.MaelstromMod.init.DimensionInit;
import com.barribob.MaelstromMod.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import scala.actors.threadpool.Arrays;

/**
 * 
 * Keeps track of all the custom structures
 *
 */
public class WorldGenCustomStructures implements IWorldGenerator
{
	public static final WorldGenStructure TEST = new WorldGenStructure("test");

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) 
	{
		switch(world.provider.getDimension())
		{
		case DimensionInit.DIMENSION_AZURE_ID:
			generateStructure(TEST, world, random, chunkX, chunkZ, 25, ModBlocks.AZURE_GRASS, BiomeInit.AZURE.getClass());
			break;
			
		// The nether
		case 1:
			break;
			
		// The overworld
		case 0:
			break;
			
		// The end
		case -1:
			break;
		}
	}
	
	/**
	 * Generates a structure in the world, calculating floor height and only placing on the top block
	 * @param generator
	 * @param world
	 * @param rand
	 * @param chunkX
	 * @param chunkZ
	 * @param chance
	 * @param topBlock
	 * @param classes
	 */
	private void generateStructure(WorldGenerator generator, World world, Random rand, int chunkX, int chunkZ, int chance, Block topBlock, Class<?> ... classes)
	{
		ArrayList<Class<?>> classesList = new ArrayList<Class<?>>(Arrays.asList(classes));
		
		int x = chunkX * 16 + rand.nextInt(15);
		int z = chunkZ * 16 + rand.nextInt(15);
		int y = calculateGenerationHeight(world, x, z, topBlock);
		BlockPos pos = new BlockPos(x, y, z);
		
		Class<?> biome = world.provider.getBiomeForCoords(pos).getClass();
		
		if(world.getWorldType() != WorldType.FLAT)
		{
			if(classesList.contains(biome))
			{
				if(rand.nextInt(chance) == 0)
				{
					generator.generate(world, rand, pos);
				}
			}
		}
	}
	
	/**
	 * Calculates what the best height to put a structure is i.e. the top block
	 * @param world
	 * @param x
	 * @param z
	 * @param topBlock
	 * @return
	 */
	private static int calculateGenerationHeight(World world, int x, int z, Block topBlock)
	{
		int y = world.getHeight();
		boolean foundGround = false;
		
		while(!foundGround && y-- >= 0)
		{
			Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
			foundGround = block == topBlock;
		}
		
		return y;
	}
}