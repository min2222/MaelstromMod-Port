package com.barribob.MaelstromMod.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

/**
 * A base class for mod foliage
 */
public class BlockModBush extends BlockBase implements IPlantable {
    protected static final AABB BUSH_AABB = new AABB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 0.6000000238418579D,
            0.699999988079071D);
    private Block grassBlock;

    public BlockModBush(String name, Material material, Block grassBlock, float hardness, float resistance, SoundType soundType) {
        super(name, material, hardness, resistance, soundType);
        this.setTickRandomly(true);
        this.grassBlock = grassBlock;
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getPlant(BlockGetter world, BlockPos pos) {
    	BlockState state = world.getBlockState(pos);
        if (state.getBlock() != this)
            return defaultBlockState();
        return state;
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    public boolean canPlaceBlockAt(Level worldIn, BlockPos pos) {
        BlockState soil = worldIn.getBlockState(pos.below());
        return super.canPlaceBlockAt(worldIn, pos) && soil.getBlock() == grassBlock;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should
     * perform any checks during a neighbor change. Cases may include when redstone
     * power is updated, cactus blocks popping off due to a neighboring solid block,
     * etc.
     */
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.checkAndDropBlock(worldIn, pos, state);
    }

    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    protected void checkAndDropBlock(Level worldIn, BlockPos pos, BlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public boolean canBlockStay(Level worldIn, BlockPos pos, BlockState state) {
        if (state.getBlock() == this) // Forge: This function is called during world gen and placement, before this
        // block is set, so if we are not 'here' then assume it's the pre-check.
        {
            BlockState soil = worldIn.getBlockState(pos.below());
            return soil.getBlock() == grassBlock;
        }
        return this.canSustainBush(worldIn.getBlockState(pos.below()));
    }

    /**
     * Return true if the block can sustain a Bush
     */
    protected boolean canSustainBush(BlockState state) {
        return state.getBlock() == grassBlock;
    }

    @Nullable
    public AABB getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return BUSH_AABB;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for
     * render
     */
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    @OnlyIn(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is
     * used to decide whether things like buttons are allowed to be placed on the
     * face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED},
     * which represents something that does not fit the other descriptions and will
     * generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     */
    public BlockFaceShape getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return BlockFaceShape.UNDEFINED;
    }
}
