package com.barribob.mm.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.structure.MapGenStructure;

public abstract class MapGenModStructure extends MapGenStructure {
    private int spacing;
    private int offset;
    private int odds;

    public MapGenModStructure(int spacing, int offset, int odds) {
        this.spacing = spacing;
        this.offset = offset;
        this.odds = odds;
    }

    @Override
    public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return Math.abs(chunkX % spacing) == offset && Math.abs(chunkZ % spacing) == offset && rand.nextInt(odds) == 0;
    }

    @Override
    public BlockPos getNearestStructurePos(Level worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;
        return findNearestStructurePosBySpacing(worldIn, this, pos, 20, 11, 10387313, true, 100, findUnexplored);
    }
}
