package com.barribob.MaelstromMod.blocks;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * represents a double-block-tall grass block
 */
public class BlockDoubleBrownedGrass extends BlockModBush {
    public static final EnumProperty<DoublePlantBlock.EnumBlockHalf> HALF = EnumProperty.<DoublePlantBlock.EnumBlockHalf>create("half", DoublePlantBlock.EnumBlockHalf.class);

    public BlockDoubleBrownedGrass(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(name, material, Blocks.GRASS, hardness, resistance, soundType);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, DoublePlantBlock.EnumBlockHalf.LOWER));
    }

    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    @Override
    public boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
    }

    @Override
    protected void checkAndDropBlock(Level worldIn, BlockPos pos, BlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            boolean flag = state.getValue(HALF) == DoublePlantBlock.EnumBlockHalf.UPPER;
            BlockPos blockpos = flag ? pos : pos.up();
            BlockPos blockpos1 = flag ? pos.down() : pos;
            Block block = flag ? this : worldIn.getBlockState(blockpos).getBlock();
            Block block1 = flag ? worldIn.getBlockState(blockpos1).getBlock() : this;

            if (!flag)
                this.dropBlockAsItem(worldIn, pos, state, 0); // Forge move above the setting to air.

            if (block == this) {
                worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
            }

            if (block1 == this) {
                worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
            }
        }
    }

    @Override
    public boolean canBlockStay(Level worldIn, BlockPos pos, BlockState state) {
        if (state.getBlock() != this)
            return super.canBlockStay(worldIn, pos, state); // Forge: This function is called during world gen and placement, before this
        // block is set, so if we are not 'here' then assume it's the pre-check.
        if (state.getValue(HALF) == DoublePlantBlock.EnumBlockHalf.UPPER) {
            return worldIn.getBlockState(pos.down()).getBlock() == this;
        } else {
            BlockState iblockstate = worldIn.getBlockState(pos.up());
            return iblockstate.getBlock() == this && super.canBlockStay(worldIn, pos, iblockstate);
        }
    }

    public void placeAt(Level worldIn, BlockPos lowerPos, int flags) {
        worldIn.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, DoublePlantBlock.EnumBlockHalf.LOWER), flags);
        worldIn.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, DoublePlantBlock.EnumBlockHalf.UPPER), flags);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place
     * logic
     */
    @Override
    public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, DoublePlantBlock.EnumBlockHalf.UPPER), 2);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public BlockState getStateFromMeta(int meta) {
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, DoublePlantBlock.EnumBlockHalf.UPPER)
                : this.getDefaultState().withProperty(HALF, DoublePlantBlock.EnumBlockHalf.LOWER);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(BlockState state) {
        return state.getValue(HALF) == DoublePlantBlock.EnumBlockHalf.UPPER ? 8 : 0;
    }

    @Override
    protected StateDefinition createBlockState() {
        return new StateDefinition(this, new IProperty[]{HALF});
    }

    /**
     * Get the OffsetType for this Block. Determines if the model is rendered
     * slightly offset.
     */
    @Override
    public Block.EnumOffsetType getOffsetType() {
        return Block.EnumOffsetType.XZ;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }
}
