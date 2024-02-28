package com.barribob.mm.blocks;

import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.OnlyIn;
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
        super(BlockBehaviour.Properties.of(material).strength(hardness, resistance).sound(soundType).randomTicks().noCollission());
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
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState soil = pLevel.getBlockState(pPos.below());
        return pLevel.getBlockState(pPos).getMaterial().isReplaceable() && soil.getBlock() == grassBlock;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should
     * perform any checks during a neighbor change. Cases may include when redstone
     * power is updated, cactus blocks popping off due to a neighboring solid block,
     * etc.
     */
    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        this.checkAndDropBlock(worldIn, pos, state);
    }
    
    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    protected void checkAndDropBlock(Level worldIn, BlockPos pos, BlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            Block.dropResources(state, worldIn, pos);
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

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    	return Shapes.empty();
    }
    
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    	return Shapes.create(this.getBoundingBox(pState, pLevel, pPos));
    }
    
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return BUSH_AABB;
    }

    @OnlyIn(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
