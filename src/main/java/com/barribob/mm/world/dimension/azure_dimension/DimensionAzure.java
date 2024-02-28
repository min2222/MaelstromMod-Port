package com.barribob.mm.world.dimension.azure_dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;

import java.util.Arrays;
import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.init.BiomeInit;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.renderer.AzureSkyRenderHandler;
import com.barribob.mm.world.biome.BiomeProviderMultiple;

/**
 * The Azure dimension attributes are defined here
 */
public class DimensionAzure extends WorldProvider {
    // Overridden to change the biome provider
    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new BiomeProviderMultiple(this.world.getWorldInfo()) {
            @Override
            public List<Biome> getBiomesToSpawnIn() {
                return Arrays.asList(new Biome[]{BiomeInit.AZURE, BiomeInit.AZURE_LIGHT, BiomeInit.AZURE_PLAINS});
            }
        };
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.AZURE;
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorAzure(world, world.getSeed(), true, "");
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
    public Vec3 getFogColor(float time, float p_76562_2_) {
        float f = Mth.cos(time * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        float f1 = 0.7529412F;
        float f2 = 0.84705883F;
        float f3 = 1.0F;
        f1 = f1 * (f * 0.70F + 0.06F);
        f2 = f2 * (f * 0.84F + 0.06F);
        f3 = f3 * (f * 0.70F + 0.09F);
        return new Vec3(f1, f2, f3);
    }

    @Override
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
        float f = cameraEntity.world.getCelestialAngle(partialTicks);
        float f1 = Mth.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.5F;
        f1 = Mth.clamp(f1, 0.1F, 1.0F);
        int i = Mth.floor(cameraEntity.posX);
        int j = Mth.floor(cameraEntity.posY);
        int k = Mth.floor(cameraEntity.posZ);
        return new Vec3(194 / 255f, 239 / 255f, 239 / 255f).scale(f1);
    }

    @Override
    public IRenderHandler getSkyRenderer() {
        return ModConfig.shaders.render_custom_sky ? new AzureSkyRenderHandler() : null;
    }

    @Override
    public double getVoidFogYFactor() {
        return 8.0f / 256f;
    }
}
