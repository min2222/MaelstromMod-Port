package com.barribob.mm.init;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.util.Reference;
import com.barribob.mm.world.dimension.azure_dimension.DimensionAzure;
import com.barribob.mm.world.dimension.cliff.DimensionCliff;
import com.barribob.mm.world.dimension.crimson_kingdom.DimensionCrimsonKingdom;
import com.barribob.mm.world.dimension.dark_nexus.DimensionDarkNexus;
import com.barribob.mm.world.dimension.nexus.DimensionNexus;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class ModDimensions {
	public static final ResourceKey<Level> AZURE_KEY = createKey("azure");
	public static final ResourceKey<Level> NEXUS_KEY = createKey("nexus");
	public static final ResourceKey<Level> CLIFF_KEY = createKey("cliff");
	public static final ResourceKey<Level> DARK_NEXUS_KEY = createKey("dark_nexus");
	public static final ResourceKey<Level> CRIMSON_KINGDOM_KEY = createKey("crimson_kingdom");
	
    public static final DimensionType AZURE = DimensionType.register("azure", "_azure", ModConfig.world.fracture_dimension_id, DimensionAzure.class, false);
    public static final DimensionType NEXUS = DimensionType.register("nexus", "_nexus", ModConfig.world.nexus_dimension_id, DimensionNexus.class, false);
    public static final DimensionType CLIFF = DimensionType.register("cliff", "_cliff", ModConfig.world.cliff_dimension_id, DimensionCliff.class, false);
    public static final DimensionType DARK_NEXUS = DimensionType.register("dark_nexus", "_dark_nexus", ModConfig.world.dark_nexus_dimension_id, DimensionDarkNexus.class, false);
    public static final DimensionType CRIMSON_KINGDOM = DimensionType.register("crimson_kingdom", "_crimson_kingdom", ModConfig.world.crimson_kingdom_dimension_id, DimensionCrimsonKingdom.class, false);

    public static void registerDimensions() {
        if(!ModConfig.world.disableDimensions) {
            DimensionManager.registerDimension(ModConfig.world.fracture_dimension_id, AZURE);
            DimensionManager.registerDimension(ModConfig.world.nexus_dimension_id, NEXUS);
            DimensionManager.registerDimension(ModConfig.world.cliff_dimension_id, CLIFF);
            DimensionManager.registerDimension(ModConfig.world.dark_nexus_dimension_id, DARK_NEXUS);
            DimensionManager.registerDimension(ModConfig.world.crimson_kingdom_dimension_id, CRIMSON_KINGDOM);
        }
    }
    
    public static ResourceKey<Level> createKey(String name) {
    	return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Reference.MOD_ID, name));
    }
}
