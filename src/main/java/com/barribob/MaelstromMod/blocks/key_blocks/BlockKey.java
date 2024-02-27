package com.barribob.MaelstromMod.blocks.key_blocks;

import com.barribob.MaelstromMod.blocks.BlockBase;
import com.barribob.MaelstromMod.entity.tileentity.TileEntityUpdater;
import com.barribob.MaelstromMod.entity.util.EntityAzurePortalSpawn;
import com.barribob.MaelstromMod.init.ModCreativeTabs;
import com.barribob.MaelstromMod.util.IBlockUpdater;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import com.google.common.base.Predicate;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

public class BlockKey extends BlockBase implements IBlockUpdater, EntityBlock {
    private Item activationItem;
    protected static final net.minecraft.world.phys.AABB AABB = new AABB(0.0D, 0.0D, 0.0D, 1.0D, 1.25D, 1.0D);
    int counter = 0;
    BiFunction<Level, BlockPos, Entity> spawnPortal;

    public BlockKey(String name) {
        this(name, null, (world, pos) -> new EntityAzurePortalSpawn(world, pos.getX(), pos.getY(), pos.getZ()));
    }

    public BlockKey(String name, Item item, BiFunction<Level, BlockPos, Entity> spawnPortal) {
        super(name, Material.ROCK, 1000, 10000, SoundType.STONE);
        this.setBlockUnbreakable();
        this.activationItem = item;
        this.hasTileEntity = true;
        this.spawnPortal = spawnPortal;
        this.setCreativeTab(ModCreativeTabs.BLOCKS);
    }

    @Override
    public net.minecraft.world.phys.AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return AABB;
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
    public void update(Level world, BlockPos pos) {
        counter++;
        if (counter % 5 == 0) {
            List<LocalPlayer> list = world.<LocalPlayer>getPlayers(LocalPlayer.class, new Predicate<LocalPlayer>() {
                @Override
                public boolean apply(@Nullable LocalPlayer player) {
                    return player.getHeldItem(InteractionHand.MAIN_HAND).getItem() == activationItem;
                }
            });

            if (list.size() > 0) {
                ModUtils.performNTimes(50, (i) -> {
                    ParticleManager.spawnFirework(world, new Vec3(pos).add(new Vec3(0.5, 1 + i, 0.5)), ModColors.WHITE, ModUtils.yVec(-0.1f));
                });
            }
        }
    }

    @Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityUpdater();
    }

    @Override
    public void breakBlock(Level worldIn, BlockPos pos, BlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(Level worldIn, BlockPos pos, BlockState state, Player playerIn, InteractionHand hand, Direction facing, float hitX, float hitY,
                                    float hitZ) {
        if (playerIn.getHeldItemMainhand() != null && playerIn.getHeldItemMainhand().getItem() == this.activationItem) {
            worldIn.spawnEntity(this.spawnPortal.apply(worldIn, pos));
            worldIn.setBlockToAir(pos);
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
