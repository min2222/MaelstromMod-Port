package com.barribob.mm.init;

import com.barribob.mm.enchantments.EnchantmentCriticalHit;
import com.barribob.mm.enchantments.EnchantmentEnflame;
import com.barribob.mm.enchantments.EnchantmentImpact;
import com.barribob.mm.enchantments.EnchantmentMaelstromDestroyer;
import com.barribob.mm.enchantments.EnchantmentPower;
import com.barribob.mm.enchantments.EnchantmentReload;
import com.barribob.mm.util.Reference;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Reference.MOD_ID);
    public static final EquipmentSlot[] WEAPON_SLOTS = new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    public static final RegistryObject<Enchantment> RELOAD = ENCHANTMENTS.register("reload", () -> new EnchantmentReload("reload", Enchantment.Rarity.UNCOMMON, WEAPON_SLOTS));
    public static final RegistryObject<Enchantment> GUN_POWER = ENCHANTMENTS.register("gun_power", () -> new EnchantmentPower("gun_power", Enchantment.Rarity.COMMON, WEAPON_SLOTS));
    public static final RegistryObject<Enchantment> IMPACT = ENCHANTMENTS.register("impact", () -> new EnchantmentImpact("impact", Enchantment.Rarity.RARE, WEAPON_SLOTS));
    public static final RegistryObject<Enchantment> GUN_FLAME = ENCHANTMENTS.register("gun_flame", () -> new EnchantmentEnflame("gun_flame", Enchantment.Rarity.RARE, WEAPON_SLOTS));
    public static final RegistryObject<Enchantment> MAELSTROM_DESTROYER = ENCHANTMENTS.register("maelstrom_destroyer", () -> new EnchantmentMaelstromDestroyer("maelstrom_destroyer", Enchantment.Rarity.RARE, WEAPON_SLOTS));
    public static final RegistryObject<Enchantment> CRITICAL_HIT = ENCHANTMENTS.register("critical_hit", () -> new EnchantmentCriticalHit("critical_hit", Enchantment.Rarity.RARE, WEAPON_SLOTS));
}
