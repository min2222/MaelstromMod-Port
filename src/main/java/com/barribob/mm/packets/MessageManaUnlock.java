package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

/**
 * Does not actually unlock mana (that is done automatically in MessageMana)
 * This just spawn the particles when the catalyst is activated
 */
public class MessageManaUnlock {
	
	public MessageManaUnlock() {
	}
	
	public MessageManaUnlock(FriendlyByteBuf buf) {
		this.fromBytes(buf);
	}
	
    public void fromBytes(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public static class MessageHandler {
        public static boolean onMessage(MessageManaUnlock message, Supplier<NetworkEvent.Context> ctx) {
            if (PacketUtils.getPlayer() != null) {
                Player player = PacketUtils.getPlayer();
                int numCircles = 7;
                float dy = (player.getBbHeight() * 1.5f) / numCircles;
                ModUtils.performNTimes(numCircles, (i) -> {
                    ModUtils.circleCallback(1, 30, (pos) -> {
                        pos = new Vec3(pos.x, 0, pos.y);
                        Vec3 worldPos = pos.add(player.position()).add(ModUtils.yVec(i * dy));
                        player.level.addParticle(ParticleTypes.ENCHANT, worldPos.x, worldPos.y, worldPos.z, 0, 0.1, 0);
                        ParticleManager.spawnMaelstromParticle(player.level, player.level.random, worldPos);
                    });
                });
            }
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
