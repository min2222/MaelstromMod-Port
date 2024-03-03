package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.gui.InGameGui;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.ManaProvider;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class MessageMana {
    public MessageMana(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public MessageMana(float mana) {
        this.mana = mana;
    }

    private float mana;

    public void fromBytes(FriendlyByteBuf buf) {
        mana = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(mana);
    }

    public static class MessageHandler {
        public static boolean onMessage(MessageMana message, Supplier<NetworkEvent.Context> ctx) {
            if (PacketUtils.getPlayer() != null) {
                Player player = PacketUtils.getPlayer();
                IMana mana = player.getCapability(ManaProvider.MANA).orElse(null);

                // Handle flash animation
                if (message.mana - mana.getMana() >= 0.5) {
                    InGameGui.setManaFlashCounter(InGameGui.MAX_FLASH_COUNTER);
                } else if (message.mana - mana.getMana() <= -0.5) {
                    InGameGui.setManaFlashCounter(-InGameGui.MAX_FLASH_COUNTER);
                }

                mana.set(message.mana);

                if (mana.isLocked()) {
                    mana.setLocked(false);
                }
            }
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
