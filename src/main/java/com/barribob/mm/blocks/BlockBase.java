package com.barribob.mm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The base class for a new mod block
 */
public class BlockBase extends Block {
    public BlockBase(Material material) {
        super(BlockBehaviour.Properties.of(material));
    }

    public BlockBase(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(BlockBehaviour.Properties.of(material).strength(hardness, resistance).sound(soundType));
    }
    
    public BlockBase(BlockBehaviour.Properties properties) {
        super(properties);
    }
    
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    	return Shapes.create(this.getBoundingBox(pState, pLevel, pPos));
    }
    
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0, 0, 0, 0, 0, 0);
    }
    
    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    	return Shapes.create(this.getCollisionBoundingBox(pState, pLevel, pPos));
    }

    public AABB getCollisionBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0, 0, 0, 0, 0, 0);
    }
}
