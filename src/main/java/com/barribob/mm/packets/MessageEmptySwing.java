package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.items.tools.ToolSword;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

/**
 * This packet sends info to the server that the player missed
 *
 * @author Barribob
 */
public class MessageEmptySwing {
	
	public MessageEmptySwing() {
	}
	
    public MessageEmptySwing(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public void fromBytes(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static class Handler {
        public static boolean onMessage(MessageEmptySwing message, Supplier<NetworkEvent.Context> ctx) {
            final ServerPlayer player = ctx.get().getSender();

            player.getServer().addTickable(new Runnable() {
                @Override
                public void run() {
                    if (player.getMainHandItem() == null) {
                        return;
                    }
                    Item sword = player.getMainHandItem().getItem();

                    if (sword instanceof ToolSword) {
                        float atkCooldown = player.getAttackStrengthScale(0.5F);
                        if (atkCooldown > 0.9F) {
                            ((ToolSword) sword).doSweepAttack(player, null);
                        }
                    }
                }
            });
            
            ctx.get().setPacketHandled(true);

            // No response message
            return true;
        }

    }
}