package com.barribob.mm.entity.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkHooks;

/*
 * An entity that spawns a portal from a key block
 */
public abstract class EntityPortalSpawn extends Entity {
    private List<BlockPosTuple> portal;
    protected int blockPlaceTime = 3;
    private int counter;
    private static final byte PARTICLE_BYTE = 3;

    protected static class BlockPosTuple {
        public Block block;
        public BlockPos pos;

        public BlockPosTuple(Block block, BlockPos pos) {
            this.block = block;
            this.pos = pos;
        }
    }

    protected abstract void spawnParticles();

    protected abstract Block getRimBlock();

    protected abstract Block getPortalBlock();

    public EntityPortalSpawn(EntityType<? extends EntityPortalSpawn> type, Level worldIn) {
        super(type, worldIn);
        this.setNoGravity(true);
        portal = getPortal();
    }

    public EntityPortalSpawn(EntityType<? extends EntityPortalSpawn> type, Level worldIn, double x, double y, double z) {
        this(type, worldIn);
        this.setPos(x, y, z);
    }

    /*
     * Lay out the blocks to be generated in order
     */
    protected List<BlockPosTuple> getPortal() {
        List<BlockPosTuple> list = new ArrayList<BlockPosTuple>();
        int offset = 2;
        int yOffset = -1;

        for (int x = -1; x < 6; x++) {
            for (int y = -2; y < 6; y++) {
                for (int z = -1; z < 6; z++) {
                    if ((x <= 0 && z <= 0) || (x <= 0 && z >= 4) || (x >= 4 && z <= 0) || (x >= 4 && z >= 4)) {
                        list.add(new BlockPosTuple(this.getRimBlock(), new BlockPos(x - offset, y + yOffset, z - offset)));
                    }
                }
            }
        }

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                if (x == 0 || z == 0 || x == 4 || z == 4) {
                    list.add(new BlockPosTuple(this.getRimBlock(), new BlockPos(x - offset, yOffset, z - offset)));
                }
            }
        }

        for (int x = 1; x < 4; x++) {
            for (int z = 1; z < 4; z++) {
                list.add(new BlockPosTuple(this.getPortalBlock(), new BlockPos(x - offset, yOffset, z - offset)));
            }
        }

        return list;
    }

    @Override
    public void tick() {
        super.tick();
        this.level.broadcastEntityEvent(this, EntityPortalSpawn.PARTICLE_BYTE);

        // Place the portal block by block
        if (this.tickCount % this.blockPlaceTime == 0 && counter < portal.size()) {
            level.setBlockAndUpdate(portal.get(counter).pos.offset(this.blockPosition()), portal.get(counter).block.defaultBlockState());
            counter++;
            if (counter == portal.size()) {
                this.discard();
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EntityPortalSpawn.PARTICLE_BYTE) {
            this.spawnParticles();
        }
        super.handleEntityEvent(id);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("counter")) {
            counter = compound.getInt("counter");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("counter", counter);
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
    	return NetworkHooks.getEntitySpawningPacket(this);
    }
}
