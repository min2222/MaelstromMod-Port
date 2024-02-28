package com.barribob.mm.init;

import net.minecraft.world.item.enchantment.Enchantment;

import com.barribob.mm.enchantments.*;
import com.barribob.mm.util.Reference;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Based on Jabelar's enchantment tutorial
 * https://jabelarminecraft.blogspot.com/p/minecraft-modding_6.html
 */
@ObjectHolder(Reference.MOD_ID)
public class ModEnchantments {
    public static final Enchantment reload = null;
    public static final Enchantment gun_power = null;
    public static final Enchantment impact = null;
    public static final Enchantment gun_flame = null;
    public static final Enchantment maelstrom_destroyer = null;
    public static final Enchantment critical_hit = null;

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onRegisterEnchantments(final RegistryEvent.Register<Enchantment> event) {
            final IForgeRegistry<Enchantment> registry = event.getRegistry();

            EquipmentSlot[] weaponSlots = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

            registry.register(new EnchantmentReload("reload", Enchantment.Rarity.UNCOMMON, weaponSlots));
            registry.register(new EnchantmentPower("gun_power", Enchantment.Rarity.COMMON, weaponSlots));
            registry.register(new EnchantmentImpact("impact", Enchantment.Rarity.RARE, weaponSlots));
            registry.register(new EnchantmentEnflame("gun_flame", Enchantment.Rarity.RARE, weaponSlots));
            registry.register(new EnchantmentMaelstromDestroyer("maelstrom_destroyer", Enchantment.Rarity.RARE, weaponSlots));
            registry.register(new EnchantmentCriticalHit("critical_hit", Enchantment.Rarity.RARE, weaponSlots));
        }
    }
}
