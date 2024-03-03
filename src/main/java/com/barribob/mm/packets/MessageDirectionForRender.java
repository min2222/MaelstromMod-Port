package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.entity.util.DirectionalRender;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public class MessageDirectionForRender {
    private CompoundTag data;
    
    public MessageDirectionForRender(Entity entity, Vec3 vec) {
        CompoundTag data = new CompoundTag();
        data.putInt("entityId", entity.getId());
        data.putFloat("posX", (float) vec.x);
        data.putFloat("posY", (float) vec.y);
        data.putFloat("posZ", (float) vec.z);
        this.data = data;
    }
    
    public MessageDirectionForRender(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public void fromBytes(FriendlyByteBuf buf) {
        data = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public static class Handler {
        public static boolean onMessage(MessageDirectionForRender message, Supplier<NetworkEvent.Context> ctx) {
            if (PacketUtils.getPlayer() != null) {
                Player player = PacketUtils.getPlayer();
                if (message.data.contains("entityId") && message.data.contains("posX") && message.data.contains("posY") && message.data.contains("posZ")) {
                    Entity entity = player.level.getEntity(message.data.getInt("entityId"));
                    if (entity instanceof DirectionalRender) {
                        ((DirectionalRender) entity).setRenderDirection(new Vec3(message.data.getFloat("posX"), message.data.getFloat("posY"), message.data.getFloat("posZ")));
                    }
                }
            }
            
            ctx.get().setPacketHandled(true);

            // No response message
            return true;
        }

    }
}