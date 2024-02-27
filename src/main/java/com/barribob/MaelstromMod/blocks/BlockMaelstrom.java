package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMob;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockMaelstrom extends BlockBase {
    protected static final AABB MAELSTROM_COLLISION_AABB = new AABB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D);
    protected final int damage;

    public BlockMaelstrom(String name, Material material, float hardness, float resistance, SoundType soundType, int damage) {
        super(name, material, hardness, resistance, soundType);
        this.damage = damage;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    @Nullable
    public AABB getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
        return MAELSTROM_COLLISION_AABB;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    @Override
    public void onEntityCollidedWithBlock(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        if (entityIn instanceof LivingEntity && !EntityMaelstromMob.isMaelstromMob(entityIn)) {
            entityIn.attackEntityFrom(ModDamageSource.MAELSTROM_DAMAGE, damage);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        for (int i = 0; i < 3; i++) {
            ParticleManager.spawnMaelstromParticle(worldIn, rand, new Vec3(pos.getX() + rand.nextDouble(), pos.getY() + 1.1f, pos.getZ() + rand.nextDouble()));
        }
        if (rand.nextInt(3) == 0) {
            ParticleManager.spawnMaelstromPotionParticle(worldIn, rand, new Vec3(pos.getX() + rand.nextDouble(), pos.getY() + 1.1f, pos.getZ() + rand.nextDouble()), false);
        }
    }
}
