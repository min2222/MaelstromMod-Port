package com.barribob.mm.packets;

import com.barribob.mm.entity.animation.AnimationManager;
import com.barribob.mm.init.ModBBAnimations;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Updates a looping animation in case it doens't exist on the client due to various circumstances
 */
public class MessageLoopAnimationUpdate implements IMessage {
    private int animationId;
    private int entityId;

    public MessageLoopAnimationUpdate() {
    }

    public MessageLoopAnimationUpdate(int animationId, int id) {
        this.animationId = animationId;
        this.entityId = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.animationId = buf.readInt();
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.animationId);
        buf.writeInt(this.entityId);
    }

    public static class Handler implements IMessageHandler<MessageLoopAnimationUpdate, IMessage> {
        @Override
        public IMessage onMessage(MessageLoopAnimationUpdate message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = PacketUtils.getWorld().getEntityByID(message.entityId);
                if (entity != null && entity instanceof LivingEntity) {
                    AnimationManager.updateLoopingAnimation((LivingEntity) entity, ModBBAnimations.getAnimationName(message.animationId));
                }
            });
            return null;
        }
    }
}
