package com.barribob.mm.world.gen.maelstrom_fortress;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Rotation;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Random;

import com.barribob.mm.world.gen.MapGenModStructure;

/**
 * Determines where to spawn the maelstrom fortress
 */
public class MapGenMaelstromFortress extends MapGenModStructure {
    public MapGenMaelstromFortress(int spacing, int offset, int odds) {
        super(spacing, offset, odds);
    }

    @Override
    public String getStructureName() {
        return "Maelstrom Fortress";
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new MapGenMaelstromFortress.Start(this.world, this.rand, chunkX, chunkZ);
    }

    public static class Start extends StructureStart {
        public Start() {
        }

        public Start(Level worldIn, Random random, int chunkX, int chunkZ) {
            super(chunkX, chunkZ);
            this.create(worldIn, random, chunkX, chunkZ);
        }

        private void create(Level worldIn, Random rnd, int chunkX, int chunkZ) {
            Random random = new Random(chunkX + chunkZ * 10387313);
            Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
            int y = 95;

            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, y, chunkZ * 16 + 8);
            MaelstromFortress.startFortress(worldIn, worldIn.getSaveHandler().getStructureTemplateManager(), blockpos, rotation, this.components, rnd);
            this.updateBoundingBox();
        }
    }
}