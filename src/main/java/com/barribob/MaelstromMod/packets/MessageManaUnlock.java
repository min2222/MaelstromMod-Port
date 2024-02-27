package com.barribob.MaelstromMod.packets;

import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Does not actually unlock mana (that is done automatically in MessageMana)
 * This just spawn the particles when the catalyst is activated
 */
public class MessageManaUnlock implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class MessageHandler implements IMessageHandler<MessageManaUnlock, IMessage> {
        @Override
        public IMessage onMessage(MessageManaUnlock message, MessageContext ctx) {
            if (PacketUtils.getPlayer() != null) {
                Player player = PacketUtils.getPlayer();
                int numCircles = 7;
                float dy = (player.height * 1.5f) / numCircles;
                ModUtils.performNTimes(numCircles, (i) -> {
                    ModUtils.circleCallback(1, 30, (pos) -> {
                        pos = new Vec3(pos.x, 0, pos.y);
                        Vec3 worldPos = pos.add(player.getPositionVector()).add(ModUtils.yVec(i * dy));
                        player.world.spawnParticle(ParticleTypes.ENCHANTMENT_TABLE, worldPos.x, worldPos.y, worldPos.z, 0, 0.1, 0);
                        ParticleManager.spawnMaelstromParticle(player.world, player.world.rand, worldPos);
                    });
                });
            }
            return null;
        }
    }
}
