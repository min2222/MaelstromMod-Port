package com.barribob.mm.init;

import com.barribob.mm.potions.ModPotion;
import com.barribob.mm.util.Reference;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Reference.MOD_ID);
    public static final RegistryObject<ModPotion> WATER_STRIDER = MOB_EFFECTS.register("water_strider", () -> new ModPotion("water_strider", false, 0x00CCFF, 0, 0));
}
