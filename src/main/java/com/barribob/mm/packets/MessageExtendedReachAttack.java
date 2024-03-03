package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.items.IExtendedReach;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

/**
 * Taken from Jabelar's extended reach tutorial
 */
public class MessageExtendedReachAttack {
    private int entityId;

    public MessageExtendedReachAttack(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public MessageExtendedReachAttack(int entityId) {
        this.entityId = entityId;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        entityId = buf.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public static class Handler {
        // Double checks from the server that the sword reach is valid (to prevent
        // hacking)
        public static boolean onMessage(MessageExtendedReachAttack message, Supplier<NetworkEvent.Context> ctx) {
            final ServerPlayer player = ctx.get().getSender();

            player.getServer().addTickable(new Runnable() {
                @Override
                public void run() {
                    Entity entity = player.level.getEntity(message.entityId);

                    if (player.getMainHandItem() == null) {
                        return;
                    }

                    if (entity == null) // Miss
                    {
                        // On a miss, reset cooldown anyways
                        player.resetAttackStrengthTicker();
                        net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(player);
                    } else // Hit
                    {
                        Item sword = player.getMainHandItem().getItem();

                        if (sword instanceof IExtendedReach) {
                            // Factor in the size of the entity's bounding box to handle issues with large
                            // mobs
                            if (entity.distanceTo(player) < ((IExtendedReach) sword).getReach() + (entity.getBoundingBox().getSize() * 0.5f)) {
                                player.attack(entity);
                            }
                        }
                    }

                    player.swing(InteractionHand.MAIN_HAND);
                }
            });
            
            ctx.get().setPacketHandled(true);

            // No response message
            return true;
        }

    }
}