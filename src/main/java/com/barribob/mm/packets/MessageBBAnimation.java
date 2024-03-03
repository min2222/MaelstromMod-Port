package com.barribob.mm.packets;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;

import com.barribob.mm.entity.animation.AnimationManager;
import com.barribob.mm.init.ModBBAnimations;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

/**
 * Sends an entity animation to play to the client side
 */
public class MessageBBAnimation {
    private int animationId;
    private int entityId;
    private boolean remove;

    public MessageBBAnimation(int animationId, int id, boolean remove) {
        this.animationId = animationId;
        this.entityId = id;
        this.remove = remove;
    }
    
    public MessageBBAnimation(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        this.animationId = buf.readInt();
        this.entityId = buf.readInt();
        this.remove = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.animationId);
        buf.writeInt(this.entityId);
        buf.writeBoolean(this.remove);
    }

    public static class Handler {
    	
        public static boolean onMessage(MessageBBAnimation message, Supplier<NetworkEvent.Context> ctx) {
            Minecraft.getInstance().doRunTask(() -> {

                /**
                 * Will spin for a second just in case the packet updating the entityID doesn't come before. That's why I put this in a separate thread so the client doesn't block
                 */
                Entity entity = PacketUtils.getWorld().getEntity(message.entityId);
                long time = System.currentTimeMillis();
                while (entity == null) {
                    if (System.currentTimeMillis() - time > 1000) {
                        LogManager.getLogger().warn("Failed to recieve entity id for animation.");
                        break;
                    }
                    entity = PacketUtils.getWorld().getEntity(message.entityId);
                }

                if (entity != null && entity instanceof LivingEntity) {
                    AnimationManager.updateAnimation((LivingEntity) entity, ModBBAnimations.getAnimationName(message.animationId), message.remove);
                }
            });
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
