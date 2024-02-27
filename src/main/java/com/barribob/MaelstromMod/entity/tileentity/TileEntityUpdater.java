package com.barribob.MaelstromMod.entity.tileentity;

import com.barribob.MaelstromMod.util.IBlockUpdater;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

public class TileEntityUpdater extends BlockEntity implements ITickable {

    @Override
    public void update() {
        if (world.isRemote && this.getBlockType() instanceof IBlockUpdater) {
            ((IBlockUpdater) this.getBlockType()).update(world, pos);
        }
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbttagcompound = this.writeToNBT(new CompoundTag());
        return nbttagcompound;
    }
}
