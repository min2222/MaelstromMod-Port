package com.barribob.mm.packets;

import com.barribob.mm.items.IExtendedReach;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Taken from Jabelar's extended reach tutorial
 */
public class MessageExtendedReachAttack implements IMessage {
    private int entityId;

    public MessageExtendedReachAttack() {
    }

    public MessageExtendedReachAttack(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = ByteBufUtils.readVarInt(buf, 4);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeVarInt(buf, entityId, 4);
    }

    public static class Handler implements IMessageHandler<MessageExtendedReachAttack, IMessage> {
        // Double checks from the server that the sword reach is valid (to prevent
        // hacking)
        @Override
        public IMessage onMessage(MessageExtendedReachAttack message, MessageContext ctx) {
            final ServerPlayer player = ctx.getServerHandler().player;

            player.getServer().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    Entity entity = player.world.getEntityByID(message.entityId);

                    if (player.getHeldItemMainhand() == null) {
                        return;
                    }

                    if (entity == null) // Miss
                    {
                        // On a miss, reset cooldown anyways
                        player.resetCooldown();
                        net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(player);
                    } else // Hit
                    {
                        Item sword = player.getHeldItemMainhand().getItem();

                        if (sword instanceof IExtendedReach) {
                            // Factor in the size of the entity's bounding box to handle issues with large
                            // mobs
                            if (entity.getDistance(player) < ((IExtendedReach) sword).getReach() + (entity.getBoundingBox().getAverageEdgeLength() * 0.5f)) {
                                player.attackTargetEntityWithCurrentItem(entity);
                            }
                        }
                    }

                    player.swingArm(InteractionHand.MAIN_HAND);
                }
            });

            // No response message
            return null;
        }

    }
}