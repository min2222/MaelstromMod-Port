package com.barribob.mm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.barribob.mm.commands.CommandDimensionTeleport;
import com.barribob.mm.commands.CommandInvasion;
import com.barribob.mm.commands.CommandReloadConfigs;
import com.barribob.mm.commands.CommandRunUnitTests;
import com.barribob.mm.config.JsonConfigManager;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.init.ModEnchantments;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.init.ModPotions;
import com.barribob.mm.init.ModProfessions;
import com.barribob.mm.init.ModRecipes;
import com.barribob.mm.init.ModStructures;
import com.barribob.mm.loot.functions.ModEnchantWithLevels;
import com.barribob.mm.proxy.ClientProxy;
import com.barribob.mm.proxy.CommonProxy;
import com.barribob.mm.util.Reference;
import com.barribob.mm.util.handlers.SoundsHandler;
import com.barribob.mm.world.gen.WorldGenCustomStructures;
import com.barribob.mm.world.gen.WorldGenOre;
import com.typesafe.config.Config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
    	SoundsHandler.SOUND_EVENTS.register(bus);
    	ModEnchantments.ENCHANTMENTS.register(bus);
    	ModPotions.MOB_EFFECTS.register(bus);
    	ModEntities.ENTITY_TYPES.register(bus);
    	bus.addListener(this::setup);
	}

    /**
     * Basically initializes the entire mod by calling all of the init methods in
     * the static classes
     */
    public void setup(FMLCommonSetupEvent event) {
        loadConfigs();
        PROXY.init();

        GameRegistry.registerWorldGenerator(new WorldGenOre(), 2);
        GameRegistry.registerWorldGenerator(new WorldGenCustomStructures(), 3);

        ModEntities.registerEntities();

        ModBBAnimations.registerAnimations();
        ModDimensions.registerDimensions();
        LootFunctionManager.registerFunction(new ModEnchantWithLevels.Serializer());
    }

    public static void Init(FMLInitializationEvent event) {
        ModRecipes.init();
        ModStructures.registerStructures();
        ModProfessions.associateCareersAndTrades();
    }

    public static void serverLoad(RegisterCommandsEvent event) {
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
