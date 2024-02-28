package com.barribob.mm.enchantments;

import com.barribob.mm.items.gun.ItemGun;
import com.barribob.mm.items.gun.ItemStaff;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Increases gun damage
 */
public class EnchantmentPower extends Enchantment {
    public EnchantmentPower(String registryName, Rarity rarityIn, EquipmentSlot[] slots) {
        // The enum enchantment type doesn't matter here
        super(rarityIn, EnchantmentCategory.BREAKABLE, slots);
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinCost(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    @Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 15;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return !(ench instanceof EnchantmentMaelstromDestroyer) && super.checkCompatibility(ench);
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof ItemGun || (stack.getItem() instanceof ItemStaff && ((ItemStaff) stack.getItem()).doesDamage());
    }
}
