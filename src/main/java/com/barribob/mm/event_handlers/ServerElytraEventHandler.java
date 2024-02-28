package com.barribob.mm.event_handlers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;
import java.util.WeakHashMap;

import com.barribob.mm.items.ItemModElytra;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;

/**
 * Code based heavily on {@link https://github.com/GlassPane/Powered-Elytra} Handles the server side ticking of custom Elytras
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ServerElytraEventHandler {

    /**
     * These two functions from {@link https://github.com/GlassPane/Powered-Elytra}
     */
    public static synchronized void setFlying(ServerPlayer playerMP, boolean flying) {
        LAST_TICK_FLIGHT.put(playerMP, flying);
        if (flying) {
            if (!playerMP.isElytraFlying()) {
                playerMP.setElytraFlying();
            }
        } else if (playerMP.isElytraFlying()) {
            playerMP.clearElytraFlying();
        }
    }

    public static boolean isFlying(ServerPlayer playerMP) {
        return LAST_TICK_FLIGHT.getOrDefault(playerMP, false);
    }

    private static final Map<ServerPlayer, Boolean> LAST_TICK_FLIGHT = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerUpdate(TickEvent.PlayerTickEvent event) {

        /**
         * Requires both pre and post tick to be updated. Post tick required to keep vanilla update from resetting the elytra travel. Pre tick solves some small bugs, such as getting stuck in the ground under
         * rare circumstances, and exiting flight mode too far above ground.
         */
        if (event.player instanceof ServerPlayer) {
            ServerPlayer mpPlayer = (ServerPlayer) event.player;
            ItemStack stack = mpPlayer.getItemStackFromSlot(EquipmentSlot.CHEST);

            boolean canContinueFly = stack.getItem() instanceof ItemModElytra && !mpPlayer.onGround && !mpPlayer.isRiding() && !mpPlayer.capabilities.isFlying;
            if (isFlying(mpPlayer)) {
                if (event.phase == TickEvent.Phase.END) {
                    ModUtils.handleElytraTravel(mpPlayer);
                    mpPlayer.fallDistance = 1.0f;
                }
                setFlying(mpPlayer, canContinueFly);
            }
        }
    }
}
