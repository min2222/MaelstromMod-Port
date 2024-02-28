package com.barribob.mm.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Represents the simple saplings in the mod (huge trees may not work as well)
 */
public class BlockSaplingBase extends BlockModBush implements IGrowable {
    private static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
    protected static final AABB SAPLING_AABB = new AABB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);
    private final WorldGenerator treeGenerator;

    public BlockSaplingBase(String name, Block grassBlock, float hardness, float resistance, SoundType soundType, WorldGenerator treeGenerator) {
        super(name, Material.PLANTS, grassBlock, hardness, resistance, soundType);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, Integer.valueOf(0)));
        this.setCreativeTab(CreativeModeTab.DECORATIONS);
        this.treeGenerator = treeGenerator;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return SAPLING_AABB;
    }

    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        if (!worldIn.isRemote) {
            super.updateTick(worldIn, pos, state, rand);

            if (!worldIn.isAreaLoaded(pos, 1))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light
            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
                this.grow(worldIn, pos, state, rand);
            }
        }
    }

    public void grow(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        if (((Integer) state.getValue(STAGE)).intValue() == 0) {
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {
            this.generateTree(worldIn, pos, state, rand);
        }
    }

    public void generateTree(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) return;

        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);

        if (!treeGenerator.generate(worldIn, rand, pos)) {
            worldIn.setBlockState(pos, state, 4);
        }
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(Level worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean canUseBonemeal(Level worldIn, Random rand, BlockPos pos, BlockState state) {
        return (double) worldIn.rand.nextFloat() < 0.45D;
    }

    public void grow(Level worldIn, Random rand, BlockPos pos, BlockState state) {
        this.grow(worldIn, pos, state, rand);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(BlockState state) {
        int i = 0;
        i = i | ((Integer) state.getValue(STAGE)).intValue() << 3;
        return i;
    }

    protected StateDefinition createBlockState() {
        return new StateDefinition(this, new IProperty[]{STAGE});
    }
}
