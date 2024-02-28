package com.barribob.mm.util.teleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Teleporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import javax.annotation.Nullable;

import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.world.dimension.nexus.DimensionNexus;

public class ToStructuralDimensionTeleporter extends Teleporter {
    private BlockPos portalOffset;
    private int spacing;
    private WorldGenerator structure;
    private static BlockPos oldDarkNexusSpawnOffset = new BlockPos(24, 64, 24);

    public ToStructuralDimensionTeleporter(ServerLevel worldIn, BlockPos portalOffset, @Nullable WorldGenerator structure) {
        super(worldIn);
        this.portalOffset = portalOffset;
        spacing = DimensionNexus.NexusStructureSpacing * 16;
        this.structure = structure;
    }

    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {
        this.placeInExistingPortal(entityIn, rotationYaw);
    }

    /**
     * Places the entity in the portal (of which we know the location beforehand)
     */
    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        int x = Mth.floor(entityIn.posX / spacing) * spacing + portalOffset.getX();
        int z = Mth.floor(entityIn.posZ / spacing) * spacing + portalOffset.getZ();
        int y = portalOffset.getY();
        Vec3 entityOffset = new Vec3(0.5, 1, -1.5);

        if (entityIn instanceof ServerPlayer) {
            if (structure != null && !this.world.isChunkGeneratedAt(x >> 4, z >> 4)) {
                // Round the position to the nearest 64th chunk square
                int chunkX = Math.floorDiv((x >> 4), DimensionNexus.NexusStructureSpacing) * DimensionNexus.NexusStructureSpacing;
                int chunkZ = Math.floorDiv((z >> 4), DimensionNexus.NexusStructureSpacing) * DimensionNexus.NexusStructureSpacing;
                structure.generate(world, random, new BlockPos(chunkX * 16 + 8, 50, chunkZ * 16 + 8));
            }

            // Check if the player probably generated the structure before it was changed to contain black sky blocks around it
            if (entityIn.dimension == ModDimensions.DARK_NEXUS.getId() && world.getBlockState(new BlockPos(x, y, z)).getBlock() != ModBlocks.DARK_NEXUS_PORTAL) {
                x = Mth.floor(entityIn.posX / spacing) * spacing + oldDarkNexusSpawnOffset.getX();
                z = Mth.floor(entityIn.posZ / spacing) * spacing + oldDarkNexusSpawnOffset.getZ();
                y = oldDarkNexusSpawnOffset.getY();
            }

            ((ServerPlayer) entityIn).connection.setPlayerLocation(x + entityOffset.x, y + entityOffset.y, z + entityOffset.z, entityIn.rotationYaw, entityIn.rotationPitch);
        } else {
            // Check if the player probably generated the structure before it was changed to contain black sky blocks around it
            if (entityIn.dimension == ModDimensions.DARK_NEXUS.getId() && world.getBlockState(new BlockPos(x, y, z)) != ModBlocks.DARK_NEXUS_PORTAL) {
                x = Mth.floor(entityIn.posX / spacing) * spacing + oldDarkNexusSpawnOffset.getX();
                z = Mth.floor(entityIn.posZ / spacing) * spacing + oldDarkNexusSpawnOffset.getZ();
                y = oldDarkNexusSpawnOffset.getY();
            }

            entityIn.setLocationAndAngles(x + entityOffset.x, y + entityOffset.y, z + entityOffset.z, entityIn.rotationYaw, entityIn.rotationPitch);
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
