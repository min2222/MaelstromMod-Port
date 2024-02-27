package com.barribob.MaelstromMod.world.gen.cliff;

import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.world.gen.WorldGenCustomStructures;
import com.barribob.MaelstromMod.world.gen.WorldGenStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

public class WorldGenCliffLedge extends WorldGenStructure {
    int yOffset;

    public WorldGenCliffLedge(String name, int yOffset) {
        super(name);
        this.yOffset = yOffset;
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        BlockPos center = this.getCenter(worldIn);
        if (WorldGenCustomStructures.canLedgeGenerate(worldIn, position)) {
            return super.generate(worldIn, rand, position.subtract(center).add(new BlockPos(0, yOffset, 0)));
        }
        return false;
    }

    @Override
    public int getYGenHeight(Level world, int x, int z) {
        return ModUtils.calculateGenerationHeight(world, x, z);
    }
}
