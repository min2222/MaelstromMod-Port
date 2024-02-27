package com.barribob.MaelstromMod.world.gen.maelstrom_stronghold;

import com.barribob.MaelstromMod.world.gen.MapGenModStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Rotation;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Random;

/**
 * Determines where to spawn the maelstrom fortress
 */
public class MapGenMaelstromStronghold extends MapGenModStructure {
    public MapGenMaelstromStronghold(int spacing, int offset, int odds) {
        super(spacing, offset, odds);
    }

    @Override
    public String getStructureName() {
        return "Maelstrom Stronghold";
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new MapGenMaelstromStronghold.Start(this.world, this.rand, chunkX, chunkZ);
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
            int y = 1;

            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, y, chunkZ * 16 + 8);
            MaelstromStronghold stronghold = new MaelstromStronghold(worldIn, worldIn.getSaveHandler().getStructureTemplateManager(), components);
            stronghold.startStronghold(blockpos, rotation);
            this.updateBoundingBox();
        }
    }
}