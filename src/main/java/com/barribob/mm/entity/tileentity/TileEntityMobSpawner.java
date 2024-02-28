package com.barribob.mm.entity.tileentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

/**
 * The tile entity for spawning maelstrom mobs, a one time spawner that sets
 * itself to air
 * <p>
 * NOTE: Because mincraft uses .newInstance() to instantiate the tile entities,
 * contructors with arguments don't work :(
 */
public abstract class TileEntityMobSpawner extends BlockEntity implements ITickable {
    private final MobSpawnerLogic spawnerLogic = this.getSpawnerLogic();

    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        this.spawnerLogic.readFromNBT(compound);
    }

    public CompoundTag writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        this.spawnerLogic.writeToNBT(compound);
        return compound;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update() {
        this.spawnerLogic.updateSpawner();
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
    }

    public CompoundTag getUpdateTag() {
        CompoundTag nbttagcompound = this.writeToNBT(new CompoundTag());
        nbttagcompound.removeTag("SpawnPotentials");
        return nbttagcompound;
    }

    public boolean onlyOpsCanSetNbt() {
        return true;
    }

    /*
     * Override this to specify what kind of logic you want to use
     */
    protected abstract MobSpawnerLogic getSpawnerLogic();

    public MobSpawnerLogic getSpawnerBaseLogic() {
        return this.spawnerLogic;
    }
}