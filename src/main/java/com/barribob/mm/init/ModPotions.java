package com.barribob.mm.init;

import com.barribob.mm.potions.ModPotion;
import com.barribob.mm.util.Reference;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod.EventBusSubscriber()
@ObjectHolder(Reference.MOD_ID)
public class ModPotions {
    public static final MobEffect water_strider = null;

    @SubscribeEvent
    public static void onPotionRegistry(final RegistryEvent.Register<MobEffect> event) {
        event.getRegistry().registerAll(new ModPotion("water_strider", false, 0x00ccff, 0, 0));
    }
}
