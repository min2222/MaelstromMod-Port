package com.barribob.mm.entity.tileentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

import com.barribob.mm.util.IBlockUpdater;

public class TileEntityUpdater extends BlockEntity implements ITickable {

    @Override
    public void update() {
        if (level.isClientSide && this.getBlockType() instanceof IBlockUpdater) {
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
