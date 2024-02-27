package com.barribob.MaelstromMod.entity.tileentity;

import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TileEntityTeleporter extends BlockEntity implements ITickable {
    private Vec3 relTeleportPos;

    public void setRelTeleportPos(Vec3 pos) {
        this.relTeleportPos = pos;
    }

    @Override
    public void update() {
        float activationDistance = 0.7f;
        float particleDistance = 6f;
        Player player = this.world.getClosestPlayer(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, activationDistance, false);
        if (player != null && this.relTeleportPos != null) {
            player.setPositionAndUpdate(player.posX + this.relTeleportPos.x, player.posY + this.relTeleportPos.y, player.posZ + this.relTeleportPos.z);
            player.playSound(SoundEvents.BLOCK_METAL_HIT, 1.0f, 1.0f);
        }

        // Spawn a line of particles indicating what direction the teleport is
        player = this.world.getClosestPlayer(this.pos.getX() + 0.5f, this.pos.getY() + 0.5f, this.pos.getZ() + 0.5f, particleDistance, false);
        if (this.world.isRemote && player != null && this.relTeleportPos != null) {
            double spacing = 4;
            Vec3 pos = new Vec3(this.pos.getX() + 0.5, this.pos.getY() + 3, this.pos.getZ() + 0.5);
            double particles = this.relTeleportPos.lengthVector() / spacing;
            double granularity = 20;
            Vec3 timeOffset = this.relTeleportPos.normalize().scale((this.world.getTotalWorldTime() % granularity) / (granularity / spacing));
            for (int i = 0; i < particles; i++) {
                Vec3 offset = this.relTeleportPos.normalize().scale(i * spacing);
                float noiseFactor = 0.125f;
                Vec3 noise = new Vec3(ModRandom.getFloat(noiseFactor), ModRandom.getFloat(noiseFactor), ModRandom.getFloat(noiseFactor));
                ParticleManager.spawnEffect(this.world, pos.add(offset).add(timeOffset).add(noise), new Vec3(1, 1, 1));
            }
        }
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("teleport_pos_x") && compound.hasKey("teleport_pos_y") && compound.hasKey("teleport_pos_z")) {
            double x = compound.getDouble("teleport_pos_x");
            double y = compound.getDouble("teleport_pos_y");
            double z = compound.getDouble("teleport_pos_z");
            this.relTeleportPos = new Vec3(x, y, z);
        }
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        if (this.relTeleportPos != null) {
            compound.setDouble("teleport_pos_x", this.relTeleportPos.x);
            compound.setDouble("teleport_pos_y", this.relTeleportPos.y);
            compound.setDouble("teleport_pos_z", this.relTeleportPos.z);
        }
        return compound;
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
