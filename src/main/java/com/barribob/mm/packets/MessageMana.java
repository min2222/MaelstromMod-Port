package com.barribob.mm.packets;

import com.barribob.mm.gui.InGameGui;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.ManaProvider;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageMana implements IMessage {
    public MessageMana() {
    }

    public MessageMana(float mana) {
        super();
        this.mana = mana;
    }

    private float mana;

    @Override
    public void fromBytes(ByteBuf buf) {
        mana = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(mana);
    }

    public static class MessageHandler implements IMessageHandler<MessageMana, IMessage> {
        @Override
        public IMessage onMessage(MessageMana message, MessageContext ctx) {
            if (PacketUtils.getPlayer() != null) {
                Player player = PacketUtils.getPlayer();
                IMana mana = player.getCapability(ManaProvider.MANA, null);

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
            return null;
        }
    }
}
