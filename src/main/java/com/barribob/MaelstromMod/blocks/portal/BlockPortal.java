package com.barribob.MaelstromMod.blocks.portal;

import com.barribob.MaelstromMod.blocks.BlockBase;
import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.teleporter.Teleport;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.Teleporter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * The base portal block class
 */
public abstract class BlockPortal extends BlockBase {
    private int entranceDimension;
    private int exitDimension;

    protected static final AABB QUARTER_AABB = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    public BlockPortal(String name, int entranceDimension, int exitDimension) {
        super(name, Material.ROCK, 1000, 1000, SoundType.STONE);
        this.setBlockUnbreakable();
        this.setLightLevel(0.5f);
        this.setLightOpacity(0);
        this.entranceDimension = entranceDimension;
        this.exitDimension = exitDimension;
    }

    /**
     * Teleport the player to the correct dimension on collision
     */
    @Override
    public void onEntityCollidedWithBlock(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        if (entityIn instanceof ServerPlayer && !entityIn.isRiding() && !entityIn.isBeingRidden() && !ModConfig.world.disableDimensions) {
            /**
             * Find the corner of the portal, so that the entire portal is treated as one
             * position.
             *
             * If this isn't done, then different parts of the same portal could potentially
             * send someone to different areas. (Assumes a simple 3x3 x-z portal layout)
             */
            BlockPos portalCorner = pos;
            for (int x = 0; x >= -2; x--) {
                for (int z = 0; z >= -2; z--) {
                    if (worldIn.getBlockState(pos.add(new BlockPos(x, 0, z))).getBlock() == this) {
                        portalCorner = pos.add(new BlockPos(x, 0, z));
                    }
                }
            }

            ServerPlayer player = (ServerPlayer) entityIn;
            player.connection.setPlayerLocation(portalCorner.getX(), portalCorner.getY(), portalCorner.getZ(), player.rotationYaw, player.rotationPitch);

            if (player.dimension == entranceDimension) {
                Teleport.teleportToDimension(player, exitDimension, getExitTeleporter(worldIn));
            } else {
                Teleport.teleportToDimension(player, entranceDimension, getEntranceTeleporter(worldIn));
            }
        }
    }

    protected abstract Teleporter getEntranceTeleporter(Level world);

    protected abstract Teleporter getExitTeleporter(Level world);

    @Override
    public AABB getCollisionBoundingBox(BlockState blockState, BlockGetter worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return QUARTER_AABB;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    /**
     * If the block is connected with itself, don't render the sides
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldSideBeRendered(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (side == Direction.NORTH && blockAccess.getBlockState(pos.north()).getBlock() == this) {
            return false;
        }

        if (side == Direction.SOUTH && blockAccess.getBlockState(pos.south()).getBlock() == this) {
            return false;
        }

        if (side == Direction.WEST && blockAccess.getBlockState(pos.west()).getBlock() == this) {
            return false;
        }

        if (side == Direction.EAST && blockAccess.getBlockState(pos.east()).getBlock() == this) {
            return false;
        }

        return true;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random p_149745_1_) {
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable Level player, List<String> tooltip, TooltipFlag advanced) {
        if(ModConfig.world.disableDimensions) {
            tooltip.add(ChatFormatting.RED + ModUtils.translateDesc("disabled"));
        }
    }
}