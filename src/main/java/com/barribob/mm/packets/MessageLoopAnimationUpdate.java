package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.entity.animation.AnimationManager;
import com.barribob.mm.init.ModBBAnimations;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

/**
 * Updates a looping animation in case it doens't exist on the client due to various circumstances
 */
public class MessageLoopAnimationUpdate {
    private int animationId;
    private int entityId;

    public MessageLoopAnimationUpdate(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public MessageLoopAnimationUpdate(int animationId, int id) {
        this.animationId = animationId;
        this.entityId = id;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        this.animationId = buf.readInt();
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.animationId);
        buf.writeInt(this.entityId);
    }

    public static class Handler {
        public static boolean onMessage(MessageLoopAnimationUpdate message, Supplier<NetworkEvent.Context> ctx) {
            Minecraft.getInstance().doRunTask(() -> {
                Entity entity = PacketUtils.getWorld().getEntity(message.entityId);
                if (entity != null && entity instanceof LivingEntity) {
                    AnimationManager.updateLoopingAnimation((LivingEntity) entity, ModBBAnimations.getAnimationName(message.animationId));
                }
            });
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
