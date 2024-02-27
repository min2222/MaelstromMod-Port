package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.util.ModRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockChain extends BlockBase {
    private AABB PILLAR_AABB;

    public BlockChain(String name, Material material, float hardness, float resistance, SoundType soundType, AABB collision) {
        super(name, material, hardness, resistance, soundType);
        PILLAR_AABB = collision;
    }

    @Override
    public void onEntityCollidedWithBlock(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        // Some hacky checks to make climbing sounds
        if (entityIn instanceof LivingEntity && ForgeHooks.isLivingOnLadder(state, worldIn, pos, (LivingEntity) entityIn) && !entityIn.onGround && Math.abs(entityIn.motionY) > 0.1 && entityIn.ticksExisted % 15 == 0) {
            entityIn.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.4f, 1.0f + ModRandom.getFloat(0.2f));
        }
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
    }

    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return PILLAR_AABB;
    }

    @Override
    public AABB getCollisionBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return PILLAR_AABB.grow(-0.15, 0, -0.15);
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
    @OnlyIn(Dist.CLIENT)
    public boolean shouldSideBeRendered(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public boolean isLadder(BlockState state, BlockGetter world, BlockPos pos, LivingEntity entity) {
        return true;
    }
}
