package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.event_handlers.ServerElytraEventHandler;
import com.barribob.mm.init.ModItems;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

/**
 * Message from the client to indicate that the player has attempted to start elytra flying
 */
public class MessageStartElytraFlying {
	
	public MessageStartElytraFlying() {
	}
	
    public MessageStartElytraFlying(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public MessageStartElytraFlying(int entityId) {
    }

    public void fromBytes(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static class Handler {
        public static boolean onMessage(MessageStartElytraFlying message, Supplier<NetworkEvent.Context> ctx) {
            final ServerPlayer player = ctx.get().getSender();

            player.getServer().addTickable(() -> {
                boolean canFly = false;
                if (!player.isOnGround() && player.getDeltaMovement().y < 0.0D && !player.isFallFlying() && !player.isInWater()) {
                    ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);
                    // Hardcoded for security reasons. If an instanceof check is used, someone could extend and add a new elytra item to hack on the client
                    canFly = itemstack.getItem() == ModItems.ELYSIUM_WINGS;
                }
                ServerElytraEventHandler.setFlying(player, canFly);
            });
            
            ctx.get().setPacketHandled(true);

            // No response message
            return true;
        }
    }
}