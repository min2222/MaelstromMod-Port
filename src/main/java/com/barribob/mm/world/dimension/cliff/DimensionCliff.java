package com.barribob.mm.world.dimension.cliff;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Arrays;
import java.util.List;

import com.barribob.mm.init.BiomeInit;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.world.biome.BiomeProviderMultiple;

/**
 * The Cliff dimension attributes are defined here
 */
public class DimensionCliff extends WorldProvider {
    // Overridden to change the biome provider
    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new BiomeProviderMultiple(this.world.getWorldInfo()) {
            @Override
            public List<Biome> getBiomesToSpawnIn() {
                return Arrays.asList(new Biome[]{BiomeInit.HIGH_CLIFF, BiomeInit.CLIFF_SWAMP});
            }
        };
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.CLIFF;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorCliff(world, world.getSeed(), true, "");
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean isSurfaceWorld() {
        return true;
    }

    @Override
    public WorldSleepResult canSleepAt(Player player, BlockPos pos) {
        return WorldSleepResult.DENY;
    }

    @Override
    public double getVoidFogYFactor() {
        return 8.0f / 256f;
    }

    @Override
    public float getCloudHeight() {
        return 242;
    }

    @Override
    public Vec3 getCloudColor(float partialTicks) {
        return new Vec3(0.5f, 0.43f, 0.5f);
    }

    @Override
    public Vec3 getFogColor(float time, float p_76562_2_) {
        float f1 = 0.4f;
        float f2 = 0.3f;
        float f3 = 0.2F;
        f1 = f1 * (0.70F + 0.06F);
        f2 = f2 * (0.84F + 0.06F);
        f3 = f3 * (0.70F + 0.09F);
        return new Vec3(f1, f2, f3);
    }

    @Override
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
        float f = cameraEntity.world.getCelestialAngle(partialTicks);
        float f1 = Mth.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = Mth.clamp(f1, 0.1F, 1.0F);
        return new Vec3(0.60f, 0.55f, 0.7f).scale(f1);
    }
}
