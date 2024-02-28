package com.barribob.mm.blocks;

import com.barribob.mm.util.ModRandom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;

public class BlockChain extends BlockBase {
    private AABB PILLAR_AABB;

    public BlockChain(String name, Material material, float hardness, float resistance, SoundType soundType, AABB collision) {
        super(name, material, hardness, resistance, soundType);
        PILLAR_AABB = collision;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        // Some hacky checks to make climbing sounds
        if (entityIn instanceof LivingEntity && !ForgeHooks.isLivingOnLadder(state, worldIn, pos, (LivingEntity) entityIn).isEmpty() && !entityIn.isOnGround() && Math.abs(entityIn.getDeltaMovement().y) > 0.1 && entityIn.tickCount % 15 == 0) {
            entityIn.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.4f, 1.0f + ModRandom.getFloat(0.2f));
        }
    }

    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return PILLAR_AABB;
    }

    @Override
    public AABB getCollisionBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return PILLAR_AABB.inflate(-0.15, 0, -0.15);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }
}
