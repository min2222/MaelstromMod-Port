package com.barribob.mm.packets;

import com.barribob.mm.sounds.DarkNexusWindSound;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSweepAttack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class PacketUtils {
    /**
     * Used to work around side errors
     */
    public static Player getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public static Level getWorld() {
        return Minecraft.getMinecraft().world;
    }

    public static void spawnSweepParticles(MessageModParticles message) {
        Particle particle = new ParticleSweepAttack.Factory().createParticle(0, Minecraft.getMinecraft().world, message.xCoord, message.yCoord, message.zCoord,
                message.xOffset, message.yOffset, message.zOffset);
        particle.setRBGColorF(message.particleArguments[0], message.particleArguments[1], message.particleArguments[2]);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public static void spawnEffect(MessageModParticles message) {
        ParticleManager.spawnEffect(Minecraft.getMinecraft().world, new Vec3(message.xCoord, message.yCoord, message.zCoord), new Vec3(message.particleArguments[0], message.particleArguments[1], message.particleArguments[2]));
    }

    public static void playDarkNexusWindSound() {
        Player player = PacketUtils.getPlayer();
        Minecraft.getMinecraft().getSoundHandler().playSound(new DarkNexusWindSound((LocalPlayer) player));
    }
}
