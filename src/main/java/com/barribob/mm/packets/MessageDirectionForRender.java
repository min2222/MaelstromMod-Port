package com.barribob.mm.packets;

import com.barribob.mm.entity.util.DirectionalRender;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDirectionForRender implements IMessage {
    private CompoundTag data;

    public MessageDirectionForRender() {
    }

    public MessageDirectionForRender(Entity entity, Vec3 vec) {
        CompoundTag data = new CompoundTag();
        data.setInteger("entityId", entity.getEntityId());
        data.setFloat("posX", (float) vec.x);
        data.setFloat("posY", (float) vec.y);
        data.setFloat("posZ", (float) vec.z);
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, data);
    }

    public static class Handler implements IMessageHandler<MessageDirectionForRender, IMessage> {
        @Override
        public IMessage onMessage(MessageDirectionForRender message, MessageContext ctx) {
            if (PacketUtils.getPlayer() != null) {
                Player player = PacketUtils.getPlayer();
                if (message.data.hasKey("entityId") && message.data.hasKey("posX") && message.data.hasKey("posY") && message.data.hasKey("posZ")) {
                    Entity entity = player.world.getEntityByID(message.data.getInteger("entityId"));
                    if (entity instanceof DirectionalRender) {
                        ((DirectionalRender) entity).setRenderDirection(new Vec3(message.data.getFloat("posX"), message.data.getFloat("posY"), message.data.getFloat("posZ")));
                    }
                }
            }

            // No response message
            return null;
        }

    }
}