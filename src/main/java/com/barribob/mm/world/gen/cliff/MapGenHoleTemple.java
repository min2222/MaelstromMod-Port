package com.barribob.mm.world.gen.cliff;

import net.minecraft.util.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Random;

import com.barribob.mm.util.GenUtils;
import com.barribob.mm.world.dimension.WorldChunkGenerator;
import com.barribob.mm.world.gen.MapGenModStructure;

public class MapGenHoleTemple extends MapGenModStructure {
    WorldChunkGenerator gen;

    public MapGenHoleTemple(int spacing, int offset, int odds, WorldChunkGenerator gen) {
        super(spacing, offset, odds);
        this.gen = gen;
    }

    @Override
    public String getStructureName() {
        return "Cliff Hole Temple";
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new MapGenHoleTemple.Start(this.world, this.rand, chunkX, chunkZ, gen);
    }

    public static class Start extends StructureStart {
        WorldChunkGenerator gen;

        public Start() {
        }

        public Start(Level worldIn, Random random, int chunkX, int chunkZ, WorldChunkGenerator gen) {
            super(chunkX, chunkZ);
            this.gen = gen;
            this.create(worldIn, random, chunkX, chunkZ);
        }

        private void create(Level worldIn, Random rnd, int chunkX, int chunkZ) {
            Random random = new Random(chunkX + chunkZ * 10387313);
            Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];

            BlockPos blockpos = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);
            HoleTempleTemplate template = new HoleTempleTemplate(worldIn.getSaveHandler().getStructureTemplateManager(), "hole_temple", blockpos, rotation, chunkZ, true);
            int y = GenUtils.getGroundHeight(template, gen, rotation);
            template.offset(0, y - 21, 0);

            this.components.add(template);
            this.updateBoundingBox();
        }
    }
}