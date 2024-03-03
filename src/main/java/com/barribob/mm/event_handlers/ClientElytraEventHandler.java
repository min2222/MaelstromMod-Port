package com.barribob.mm.event_handlers;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.barribob.mm.Main;
import com.barribob.mm.entity.model.LayerModElytra;
import com.barribob.mm.items.ItemModElytra;
import com.barribob.mm.packets.MessageStartElytraFlying;
import com.barribob.mm.util.Reference;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ClientElytraEventHandler {
    static boolean prevJumpTick;
    private static Set<Player> layeredPlayers = Collections.newSetFromMap(new WeakHashMap<Player, Boolean>());

    @SubscribeEvent
    public static void onPressKey(InputUpdateEvent event) {
        if (event.getEntityPlayer() instanceof LocalPlayer) {
            LocalPlayer player = (LocalPlayer) event.getEntityPlayer();
            if (!prevJumpTick && player.input.jumping && !player.isOnGround() && player.getDeltaMovement().y < 0.0D && !player.isFallFlying() && !player.getAbilities().flying) {
                ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);

                if (itemstack.getItem() instanceof ItemModElytra) {
                    Main.NETWORK.sendToServer(new MessageStartElytraFlying());
                }
            }
            prevJumpTick = player.input.jumping;
        }
    }

    // Jenky way of registering layers for player
    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> event) {
        if (event.getEntity() instanceof Player && !layeredPlayers.contains(event.getEntity())) {
            event.getRenderer().addLayer(new LayerModElytra(event.getRenderer()));
            layeredPlayers.add((Player) event.getEntity());
        }
    }
}
