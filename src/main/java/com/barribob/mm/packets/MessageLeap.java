package com.barribob.mm.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class MessageLeap {
	
	public MessageLeap() {
	}
	
	public MessageLeap(FriendlyByteBuf buf) {
		this.fromBytes(buf);
	}
	
    public void fromBytes(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static class MessageHandler {
        public static boolean onMessage(MessageLeap message, Supplier<NetworkEvent.Context> ctx) {
            if (PacketUtils.getPlayer() != null) {
                Player player = PacketUtils.getPlayer();
                float maxVelocityIncrease = 0.6f;
                player.push(Math.min(Math.max(player.getDeltaMovement().x, -maxVelocityIncrease), maxVelocityIncrease), 0.8f,
                        Math.min(Math.max(player.getDeltaMovement().z, -maxVelocityIncrease), maxVelocityIncrease));
                player.setDeltaMovement(player.getDeltaMovement().x, Math.min(1.0f, player.getDeltaMovement().y), player.getDeltaMovement().z);
            }
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
