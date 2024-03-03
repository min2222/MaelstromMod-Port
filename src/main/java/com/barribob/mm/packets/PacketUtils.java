package com.barribob.mm.packets;

import com.barribob.mm.sounds.DarkNexusWindSound;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PacketUtils {
    /**
     * Used to work around side errors
     */
    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }

    public static void spawnSweepParticles(MessageModParticles message) {
        Particle particle = new AttackSweepParticle.Provider(new ParticleEngine.MutableSpriteSet()).createParticle(ParticleTypes.SWEEP_ATTACK, Minecraft.getInstance().level, message.xCoord, message.yCoord, message.zCoord,
                message.xOffset, message.yOffset, message.zOffset);
        particle.setColor(message.particleArguments[0], message.particleArguments[1], message.particleArguments[2]);
        Minecraft.getInstance().particleEngine.add(particle);
    }

    public static void spawnEffect(MessageModParticles message) {
        ParticleManager.spawnEffect(Minecraft.getInstance().level, new Vec3(message.xCoord, message.yCoord, message.zCoord), new Vec3(message.particleArguments[0], message.particleArguments[1], message.particleArguments[2]));
    }

    public static void playDarkNexusWindSound() {
        Player player = PacketUtils.getPlayer();
        Minecraft.getInstance().getSoundManager().play(new DarkNexusWindSound((LocalPlayer) player));
    }
}
