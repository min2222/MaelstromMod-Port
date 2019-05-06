package com.barribob.MaelstromMod.init;

import com.barribob.MaelstromMod.trades.AzureTrades;
import com.barribob.MaelstromMod.trades.NexusTrades;
import com.barribob.MaelstromMod.util.Reference;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

/**
 * 
 * Based on Jabelar's villager profession tutorial
 * https://jabelarminecraft.blogspot.com/p/minecraft-forge-modding-villagers.html
 *
 */
@ObjectHolder(Reference.MOD_ID)
public class ModProfessions
{
    private static VillagerProfession AZURE_VILLAGER = null;
    private static VillagerProfession NEXUS_VILLAGER = null;

    public static VillagerCareer AZURE_WEAPONSMITH;
    public static VillagerCareer NEXUS_GUNSMITH;

    public static void associateCareersAndTrades()
    {
	AZURE_VILLAGER = new VillagerProfession(Reference.MOD_ID + ":azure_villager", Reference.MOD_ID + ":textures/entity/azure_villager.png",
		"minecraft:textures/entity/zombie_villager/zombie_farmer.png");

	NEXUS_VILLAGER = new VillagerProfession(Reference.MOD_ID + ":nexus_villager", Reference.MOD_ID + ":textures/entity/nexus_villager.png",
		"minecraft:textures/entity/zombie_villager/zombie_farmer.png");

	AZURE_WEAPONSMITH = new VillagerCareer(AZURE_VILLAGER, "azure_weaponsmith");
	AZURE_WEAPONSMITH.addTrade(1, new AzureTrades.ConstructArmor());
	AZURE_WEAPONSMITH.addTrade(1, new AzureTrades.RangedWeapons());
	AZURE_WEAPONSMITH.addTrade(1, new AzureTrades.CrystalsForEmeralds());
	AZURE_WEAPONSMITH.addTrade(1, new EntityVillager.ListEnchantedBookForEmeralds());
	AZURE_WEAPONSMITH.addTrade(1, new AzureTrades.EnchantedIronForEmeralds());

	NEXUS_GUNSMITH = new VillagerCareer(NEXUS_VILLAGER, "nexus_weaponsmith");
	NEXUS_GUNSMITH.addTrade(1, new NexusTrades.Flintlock());
	NEXUS_GUNSMITH.addTrade(1, new NexusTrades.Repeater());
	NEXUS_GUNSMITH.addTrade(1, new NexusTrades.Rifle());
    }

}
