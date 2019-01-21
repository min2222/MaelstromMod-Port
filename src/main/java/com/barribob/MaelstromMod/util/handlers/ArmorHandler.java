package com.barribob.MaelstromMod.util.handlers;

import com.barribob.MaelstromMod.init.ModEnchantments;
import com.barribob.MaelstromMod.items.armor.ModArmorBase;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * 
 * Get the maelstrom armor from a player
 *
 */
public class ArmorHandler
{
    /**
     * Get the total maelstrom armor of an entity
     */
    public static double getMaelstromArmor(Entity entity)
    {
	double totalArmor = 0;

	for (ItemStack equipment : entity.getArmorInventoryList())
	{
	    if (equipment.getItem() instanceof ModArmorBase)
	    {
		totalArmor += ((ModArmorBase) equipment.getItem()).getMaelstromArmor(equipment);
	    }
	}

	return totalArmor;
    }
    
    /*
     * Applies the maelstrom protection enchantment, where each enchantment level reduces the incoming damage by 0.025 for a max of 50% reduction
     */
    public static float getMaelstromProtection(Entity entity)
    {
	float damageReduction = 0;
	float reductionPerLevel = 0.025f;

	for (ItemStack equipment : entity.getArmorInventoryList())
	{
	    int maelstromProtection = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.maelstrom_protection, equipment);
	    damageReduction += (reductionPerLevel * maelstromProtection);
	}

	return damageReduction;
    }
    
    /*
     * Returns the exponential armor factor, for example 0.5 would be 2, and 0.75 would be 4
     * For gui display
     */
    public static int getMaelstromArmorBars(Entity entity)
    {
	double totalArmor = 0;

	for (ItemStack equipment : entity.getArmorInventoryList())
	{
	    if (equipment.getItem() instanceof ModArmorBase)
	    {
		totalArmor += ((ModArmorBase) equipment.getItem()).getMaelstromArmorBars();
	    }
	}
	
	return (int)Math.round(totalArmor);
    }
}
