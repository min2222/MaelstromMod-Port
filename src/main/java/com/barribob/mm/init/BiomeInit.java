package com.barribob.mm.init;

import com.barribob.mm.util.Reference;
import com.barribob.mm.world.biome.BiomeAzure;
import com.barribob.mm.world.biome.BiomeCliffPlateau;
import com.barribob.mm.world.biome.BiomeCliffSwamp;
import com.barribob.mm.world.biome.BiomeCrimsonKingdom;
import com.barribob.mm.world.biome.BiomeNexus;

import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Credit for Jabelar for giving basic structure of biome init
 * https://github.com/jabelar/ExampleMod-1.12/blob/master/src/main/java/com/blogspot/jabelarminecraft/examplemod/init/ModBiomes.java#L65
 */
public class BiomeInit {
    public static final Biome AZURE = null;
    public static final Biome AZURE_LIGHT = null;
    public static final Biome AZURE_PLAINS = null;
    public static final Biome NEXUS = null;
    public static final Biome HIGH_CLIFF = null;
    public static final Biome CLIFF_SWAMP = null;
    public static final Biome CRIMSON_KINGDOM = null;

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onEvent(final RegistryEvent.Register<Biome> event) {
            final IForgeRegistry<Biome> registry = event.getRegistry();

            initBiome(registry, new BiomeAzure(5, 8), "azure", BiomeType.WARM, Type.HILLS);
            initBiome(registry, new BiomeAzure(1, 8), "azure_light", BiomeType.WARM, Type.HILLS);
            initBiome(registry, new BiomeAzure(1, 2), "azure_plains", BiomeType.WARM, Type.HILLS);
            initBiome(registry, new BiomeNexus(), "nexus", BiomeType.WARM, Type.PLAINS);
            initBiome(registry, new BiomeCliffPlateau(), "high_cliff", BiomeType.WARM, Type.PLAINS);
            initBiome(registry, new BiomeCliffSwamp(), "cliff_swamp", BiomeType.WARM, Type.SWAMP);
            initBiome(registry, new BiomeCrimsonKingdom(), "crimson_kingdom", BiomeType.WARM, Type.PLAINS);
        }
    }
}
