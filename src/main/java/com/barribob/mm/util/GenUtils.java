package com.barribob.mm.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Rotation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import java.util.ArrayList;

import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.world.dimension.WorldChunkGenerator;
import com.barribob.mm.world.gen.ModStructureTemplate;

public class GenUtils {
    public static int getGroundHeight(ModStructureTemplate template, WorldChunkGenerator gen, Rotation rotation) {
        StructureBoundingBox box = template.getBoundingBox();
        int corner1 = getGroundHeight(new BlockPos(box.maxX, 0, box.maxZ), gen, rotation);
        int corner2 = getGroundHeight(new BlockPos(box.minX, 0, box.maxZ), gen, rotation);
        int corner3 = getGroundHeight(new BlockPos(box.maxX, 0, box.minZ), gen, rotation);
        int corner4 = getGroundHeight(new BlockPos(box.minX, 0, box.minZ), gen, rotation);
        return Math.min(Math.min(corner3, corner4), Math.max(corner2, corner1));
    }

    /*
     * From MapGenEndCity: determines the ground height
     */
    public static int getGroundHeight(BlockPos pos, WorldChunkGenerator gen, Rotation rotation) {
        BlockPos chunk = ModUtils.posToChunk(pos);
        ChunkPrimer chunkprimer = new ChunkPrimer();
        gen.setBlocksInChunk(chunk.getX(), chunk.getZ(), chunkprimer);
        int i = 5;
        int j = 5;

        if (rotation == Rotation.CLOCKWISE_90) {
            i = -5;
        } else if (rotation == Rotation.CLOCKWISE_180) {
            i = -5;
            j = -5;
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            j = -5;
        }

        int k = chunkprimer.findGroundBlockIdx(7, 7);
        int l = chunkprimer.findGroundBlockIdx(7, 7 + j);
        int i1 = chunkprimer.findGroundBlockIdx(7 + i, 7);
        int j1 = chunkprimer.findGroundBlockIdx(7 + i, 7 + j);
        int k1 = Math.min(Math.min(k, l), Math.min(i1, j1));
        return k1;
    }

    /*
     * Dig blocks in a blobby sort of way
     */
    public static void digBlockToVoid(int size, BlockPos pos, Level world) {
        ArrayList<BlockPos> queue = new ArrayList<BlockPos>();
        queue.add(pos);

        for (int i = 0; i < size; i++) {
            if (queue.size() == 0)
                return;

            BlockPos randPos = queue.get(world.random.nextInt(queue.size()));
            queue.remove(randPos);
            BlockPos[] adjacents = {randPos.north(), randPos.south(), randPos.east(), randPos.west()};

            for (int y = randPos.getY(); y >= 0; y--) {
                world.setBlockAndUpdate(new BlockPos(randPos.getX(), y, randPos.getZ()), Blocks.AIR.defaultBlockState());
                for (BlockPos adj : adjacents) {
                    if (!world.isEmptyBlock(new BlockPos(adj.getX(), y, adj.getZ()))) {
                        world.setBlockAndUpdate(new BlockPos(adj.getX(), y, adj.getZ()), ModBlocks.AZURE_STONEBRICK.defaultBlockState());
                    }
                }
            }

            for (BlockPos adj : adjacents) {
                if (!world.isEmptyBlock(adj)) {
                    queue.add(adj);
                }
            }
        }
    }

    public static int getTerrainVariation(Level world, int x, int z, int sizeX, int sizeZ) {
        sizeX = x + sizeX;
        sizeZ = z + sizeZ;
        int corner1 = ModUtils.calculateGenerationHeight(world, x, z);
        int corner2 = ModUtils.calculateGenerationHeight(world, sizeX, z);
        int corner3 = ModUtils.calculateGenerationHeight(world, x, sizeZ);
        int corner4 = ModUtils.calculateGenerationHeight(world, sizeX, sizeZ);

        int max = Math.max(Math.max(corner3, corner4), Math.max(corner1, corner2));
        int min = Math.min(Math.min(corner3, corner4), Math.min(corner1, corner2));
        return max - min;
    }
}
