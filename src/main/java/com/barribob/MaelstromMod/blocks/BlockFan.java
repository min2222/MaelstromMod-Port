package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.entity.tileentity.TileEntityFan;
import com.barribob.MaelstromMod.util.ModRandom;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFan extends BlockBase {
    public static final DirectionProperty FACING = BlockDirectional.FACING;
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");

    public BlockFan(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(name, material, hardness, resistance, soundType);
        setDefaultState(blockState.getBaseState().withProperty(FACING, Direction.NORTH).withProperty(TRIGGERED, Boolean.valueOf(false)));
    }

    @Override
    public void randomDisplayTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        if (stateIn.getValue(TRIGGERED).booleanValue()) {
            Direction facing = stateIn.getValue(FACING);
            Vec3 vel = new Vec3(facing.getDirectionVec()).scale(0.8f);
            pos = pos.add(facing.getDirectionVec());
            worldIn.spawnParticle(ParticleTypes.CLOUD, false, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, vel.x, vel.y, vel.z);
        }

        if (rand.nextInt(24) == 0) {
            worldIn.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.ITEM_ELYTRA_FLYING, SoundSource.BLOCKS, 0.3F + ModRandom.getFloat(0.2f), rand.nextFloat() * 0.7F + 0.3F, false);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
        boolean flag1 = state.getValue(TRIGGERED).booleanValue();

        if (flag && !flag1) {
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)));
        } else if (!flag && flag1) {
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)));
        }
    }

    @Override
    public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        neighborChanged(state, worldIn, pos, null, null);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return this.getDefaultState().withProperty(FACING, Direction.getDirectionFromEntityLiving(pos, placer)).withProperty(TRIGGERED, Boolean.valueOf(false));
    }

    @Override
    protected StateDefinition createBlockState() {
        return new StateDefinition(this, new IProperty[]{FACING, TRIGGERED});
    }

    @Override
    public int getMetaFromState(BlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(TRIGGERED).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public static Direction getFacing(int meta) {
        return Direction.getFront(meta & 7);
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, Direction.getFront(meta & 7)).withProperty(TRIGGERED, Boolean.valueOf((meta & 8) > 0));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockEntity createTileEntity(Level world, BlockState state) {
        return new TileEntityFan();
    }
}
