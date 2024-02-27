package com.barribob.MaelstromMod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.barribob.MaelstromMod.commands.CommandDimensionTeleport;
import com.barribob.MaelstromMod.commands.CommandInvasion;
import com.barribob.MaelstromMod.commands.CommandReloadConfigs;
import com.barribob.MaelstromMod.commands.CommandRunUnitTests;
import com.barribob.MaelstromMod.config.JsonConfigManager;
import com.barribob.MaelstromMod.init.ModBBAnimations;
import com.barribob.MaelstromMod.init.ModBlocks;
import com.barribob.MaelstromMod.init.ModDimensions;
import com.barribob.MaelstromMod.init.ModEntities;
import com.barribob.MaelstromMod.init.ModProfessions;
import com.barribob.MaelstromMod.init.ModRecipes;
import com.barribob.MaelstromMod.init.ModStructures;
import com.barribob.MaelstromMod.loot.functions.ModEnchantWithLevels;
import com.barribob.MaelstromMod.proxy.ClientProxy;
import com.barribob.MaelstromMod.proxy.CommonProxy;
import com.barribob.MaelstromMod.util.Reference;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import com.barribob.MaelstromMod.world.gen.WorldGenCustomStructures;
import com.barribob.MaelstromMod.world.gen.WorldGenOre;
import com.electronwill.nightconfig.core.Config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Main mod class Many of the base boilerplate here is thanks to loremaster's
 * tutorials https://www.youtube.com/channel/UC3n-lKS-MYlunVtErgzSFZg Entities,
 * world generation, and dimension frameworks are thanks to Harry Talks
 * https://www.youtube.com/channel/UCUAawSqNFBEj-bxguJyJL9g Also thanks to
 * Julian Abelar for a bunch of modding tutorials and articles
 * https://jabelarminecraft.blogspot.com/
 * <p>
 * Also other tools that I used: World Edit from Single Player Commands, as well as MCEdit
 */
@Mod(Reference.MOD_ID)
public class Main {
    public static final CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Reference.MOD_ID, Reference.NETWORK_CHANNEL_NAME),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

    public static final JsonConfigManager CONFIG_MANAGER = new JsonConfigManager();
    public static Config itemsConfig;
    public static Config invasionsConfig;
    public static Config mobsConfig;
    public static Config soundsConfig;
    public static Config manaConfig;
    public static Config maelstromFriendsConfig;

    public static final Logger LOG = LogManager.getLogger();

    public static final String CONFIG_DIRECTORY_NAME = "Maelstrom Mod";
    
    public Main() 
    {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	ModBlocks.BLOCKS.register(bus);
	}

    /**
     * Basically initializes the entire mod by calling all of the init methods in
     * the static classes
     */
    @EventHandler
    public static void PreInit(FMLPreInitializationEvent event) {
        log = event.getModLog();

        loadConfigs();

        GameRegistry.registerWorldGenerator(new WorldGenOre(), 2);
        GameRegistry.registerWorldGenerator(new WorldGenCustomStructures(), 3);

        ModEntities.registerEntities();
        proxy.init();

        ModBBAnimations.registerAnimations();
        ModDimensions.registerDimensions();
        LootFunctionManager.registerFunction(new ModEnchantWithLevels.Serializer());
    }

    @EventHandler
    public static void Init(FMLInitializationEvent event) {
        ModRecipes.init();
        SoundsHandler.registerSounds();
        ModStructures.registerStructures();
        ModProfessions.associateCareersAndTrades();
    }

    @EventHandler
    public static void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDimensionTeleport());
        event.registerServerCommand(new CommandReloadConfigs());
        event.registerServerCommand(new CommandInvasion());
        event.registerServerCommand(new CommandRunUnitTests());
    }

    public static void loadConfigs() {
        itemsConfig = CONFIG_MANAGER.handleConfigLoad(CONFIG_DIRECTORY_NAME, "items");
        invasionsConfig = CONFIG_MANAGER.handleConfigLoad(CONFIG_DIRECTORY_NAME, "invasions");
        mobsConfig = CONFIG_MANAGER.handleConfigLoad(CONFIG_DIRECTORY_NAME, "mobs");
        soundsConfig = CONFIG_MANAGER.handleConfigLoad(CONFIG_DIRECTORY_NAME, "sounds");
        manaConfig = CONFIG_MANAGER.handleConfigLoad(CONFIG_DIRECTORY_NAME, "mana");
        maelstromFriendsConfig = CONFIG_MANAGER.handleConfigLoad(CONFIG_DIRECTORY_NAME, "maelstrom_friends");
    }
}
