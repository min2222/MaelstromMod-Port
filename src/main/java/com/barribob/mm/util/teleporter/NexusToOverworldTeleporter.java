package com.barribob.mm.util.teleporter;

import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.world.dimension.nexus.DimensionNexus;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Teleporter;
import net.minecraft.world.level.Level;

/**
 * Finds a portal in the overworld from the nexus, or builds one Uses known offsets to teleport precisely to the portal
 */
public class NexusToOverworldTeleporter extends Teleporter {
    public static final int yPortalOffset = 139;
    private int spacing;

    public NexusToOverworldTeleporter(ServerLevel worldIn) {
        super(worldIn);
        if (this.world.provider.getDimensionType().getId() != 0) {
            System.err.println("The nexus to overworld teleporter is being used for the wrong dimension!");
        }
        spacing = DimensionNexus.NexusStructureSpacing * 16;
    }

    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {
        if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
            this.makePortal(entityIn);
            this.placeInExistingPortal(entityIn, rotationYaw);
        }
    }

    /**
     * Finds an existing portal to teleport the player to
     */
    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        int startX = Mth.floor(entityIn.posX / spacing) * spacing;
        int startZ = Mth.floor(entityIn.posZ / spacing) * spacing;
        Vec3 entityOffset = new Vec3(1.5, 1, -0.5);

        /**
         * This is an algorithm that depends on the assumption that the create portal will always be at a certain height, and that the portal will be at least 3 x 3 wide.
         */
        for (int x = startX; x < startX + spacing; x += 3) {
            for (int z = startZ; z < startZ + spacing; z += 3) {
                if (this.world.isChunkGeneratedAt(x >> 4, z >> 4) && this.world.getBlockState(new BlockPos(x, yPortalOffset, z)).getBlock() == ModBlocks.NEXUS_PORTAL) {
                    // Find the corner of the portal to make sure that the portal offset applies correctly (otherwise there is a good chance of spawning inside the portal)
                    BlockPos portalCorner = new BlockPos(x, yPortalOffset, z);
                    for (int x1 = 2; x1 >= -2; x1--) {
                        for (int z1 = 2; z1 >= -2; z1--) {
                            if (world.getBlockState(new BlockPos(x + x1, yPortalOffset, z + z1)).getBlock() == ModBlocks.NEXUS_PORTAL) {
                                portalCorner = new BlockPos(x + x1, yPortalOffset, z + z1);
                            }
                        }
                    }

                    x = portalCorner.getX();
                    z = portalCorner.getZ();

                    if (entityIn instanceof ServerPlayer) {
                        ((ServerPlayer) entityIn).connection.setPlayerLocation(x + entityOffset.x, yPortalOffset + entityOffset.y, z + entityOffset.z, entityIn.rotationYaw, entityIn.rotationPitch);
                    } else {
                        entityIn.setLocationAndAngles(x + entityOffset.x, yPortalOffset + entityOffset.y, z + entityOffset.z, entityIn.rotationYaw, entityIn.rotationPitch);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a simple portal
     */
    @Override
    public boolean makePortal(Entity entity) {
        int i = Mth.floor(entity.posX / spacing) * spacing;
        int k = Mth.floor(entity.posZ / spacing) * spacing;
        int j = yPortalOffset;

        // Clear the area of air blocks
        int size = 5;
        for (int x = i; x < i + size; x++) {
            for (int z = k; z < k + size; z++) {
                for (int y = j; y < j + 2; y++) {
                    world.setBlockToAir(new BlockPos(x, y, z));
                }
            }
        }

        // Add the portal blocks
        for (int x = i; x < i + size; x++) {
            for (int z = k; z < k + size; z++) {
                world.setBlockState(new BlockPos(x, j, z), Blocks.QUARTZ_BLOCK.getDefaultState());
                world.setBlockState(new BlockPos(x, j - 1, z), Blocks.QUARTZ_BLOCK.getDefaultState());
            }
        }

        int size2 = size - 2;
        for (int x = i + 1; x < i + size2 + 1; x++) {
            for (int z = k + 1; z < k + size2 + 1; z++) {
                world.setBlockState(new BlockPos(x, j, z), ModBlocks.NEXUS_PORTAL.getDefaultState());
            }
        }

        return true;
    }

    @Override
    public void placeEntity(Level world, Entity entity, float yaw) {
        if (entity instanceof ServerPlayer)
            placeInPortal(entity, yaw);
        else
            placeInExistingPortal(entity, yaw);
    }
}
