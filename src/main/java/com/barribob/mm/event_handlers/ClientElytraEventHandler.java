package com.barribob.mm.event_handlers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.barribob.mm.Main;
import com.barribob.mm.entity.model.LayerModElytra;
import com.barribob.mm.items.ItemModElytra;
import com.barribob.mm.packets.MessageStartElytraFlying;
import com.barribob.mm.util.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ClientElytraEventHandler {
    static boolean prevJumpTick;
    private static Set<Player> layeredPlayers = Collections.newSetFromMap(new WeakHashMap<Player, Boolean>());

    @SubscribeEvent
    public static void onPressKey(InputUpdateEvent event) {
        if (event.getEntityPlayer() instanceof LocalPlayer) {
            LocalPlayer player = (LocalPlayer) event.getEntityPlayer();
            if (!prevJumpTick && player.movementInput.jump && !player.onGround && player.motionY < 0.0D && !player.isElytraFlying() && !player.capabilities.isFlying) {
                ItemStack itemstack = player.getItemStackFromSlot(EquipmentSlot.CHEST);

                if (itemstack.getItem() instanceof ItemModElytra) {
                    Main.network.sendToServer(new MessageStartElytraFlying());
                }
            }
            prevJumpTick = player.movementInput.jump;
        }
    }

    // Jenky way of registering layers for player
    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<AbstractClientPlayer> event) {
        if (event.getEntity() instanceof Player && !layeredPlayers.contains(event.getEntity())) {
            event.getRenderer().addLayer(new LayerModElytra(event.getRenderer()));
            layeredPlayers.add((Player) event.getEntity());
        }
    }
}
