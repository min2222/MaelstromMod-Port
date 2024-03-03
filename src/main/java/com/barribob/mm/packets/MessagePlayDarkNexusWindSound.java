package com.barribob.mm.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class MessagePlayDarkNexusWindSound {
	public MessagePlayDarkNexusWindSound(FriendlyByteBuf buf) {
		this.fromBytes(buf);
	}
	
    public void fromBytes(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static class Handler {
        public static boolean onMessage(MessagePlayDarkNexusWindSound message, Supplier<NetworkEvent.Context> ctx) {
            if (PacketUtils.getPlayer() != null) {
                PacketUtils.playDarkNexusWindSound();
            }
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
