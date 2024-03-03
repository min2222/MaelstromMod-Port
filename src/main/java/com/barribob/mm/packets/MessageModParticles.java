package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.particle.EnumModParticles;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

/**
 * Taken from the minecraft particle packet.
 */
public class MessageModParticles {
    private EnumModParticles particleType;
    float xCoord;
    float yCoord;
    float zCoord;
    float xOffset;
    float yOffset;
    float zOffset;
    /**
     * These are the block/item ids and possibly metaData ids that are used to color
     * or texture the particle.
     */
    float[] particleArguments;

    public MessageModParticles(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public MessageModParticles(EnumModParticles particleIn, Vec3 pos, Vec3 vel, Vec3 color) {
        this(particleIn, (float) pos.x, (float) pos.y, (float) pos.z, (float) vel.x, (float) vel.y, (float) vel.z, (float) color.x, (float) color.y, (float) color.z);
    }

    public MessageModParticles(EnumModParticles particleIn, float xIn, float yIn, float zIn, float xOffsetIn, float yOffsetIn, float zOffsetIn,
                               float... argumentsIn) {
        this.particleType = particleIn;
        this.xCoord = xIn;
        this.yCoord = yIn;
        this.zCoord = zIn;
        this.xOffset = xOffsetIn;
        this.yOffset = yOffsetIn;
        this.zOffset = zOffsetIn;
        this.particleArguments = argumentsIn;
    }

    @OnlyIn(Dist.CLIENT)
    public EnumModParticles getParticleType() {
        return this.particleType;
    }

    /**
     * Gets the x coordinate to spawn the particle.
     */
    @OnlyIn(Dist.CLIENT)
    public double getXCoordinate() {
        return this.xCoord;
    }

    /**
     * Gets the y coordinate to spawn the particle.
     */
    @OnlyIn(Dist.CLIENT)
    public double getYCoordinate() {
        return this.yCoord;
    }

    /**
     * Gets the z coordinate to spawn the particle.
     */
    @OnlyIn(Dist.CLIENT)
    public double getZCoordinate() {
        return this.zCoord;
    }

    /**
     * Gets the x coordinate offset for the particle. The particle may use the
     * offset for particle spread.
     */
    @OnlyIn(Dist.CLIENT)
    public float getXOffset() {
        return this.xOffset;
    }

    /**
     * Gets the y coordinate offset for the particle. The particle may use the
     * offset for particle spread.
     */
    @OnlyIn(Dist.CLIENT)
    public float getYOffset() {
        return this.yOffset;
    }

    /**
     * Gets the z coordinate offset for the particle. The particle may use the
     * offset for particle spread.
     */
    @OnlyIn(Dist.CLIENT)
    public float getZOffset() {
        return this.zOffset;
    }

    /**
     * Gets the particle arguments. Some particles rely on block and/or item ids and
     * sometimes metadata ids to color or texture the particle.
     */
    @OnlyIn(Dist.CLIENT)
    public float[] getParticleArgs() {
        return this.particleArguments;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        this.particleType = EnumModParticles.getParticleFromId(buf.readInt());

        if (this.particleType == null) {
            this.particleType = EnumModParticles.SWEEP_ATTACK;
        }

        this.xCoord = buf.readFloat();
        this.yCoord = buf.readFloat();
        this.zCoord = buf.readFloat();
        this.xOffset = buf.readFloat();
        this.yOffset = buf.readFloat();
        this.zOffset = buf.readFloat();
        int i = this.particleType.getArgumentCount();
        this.particleArguments = new float[i];

        for (int j = 0; j < i; ++j) {
            this.particleArguments[j] = buf.readFloat();
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.particleType.getParticleID());
        buf.writeFloat(this.xCoord);
        buf.writeFloat(this.yCoord);
        buf.writeFloat(this.zCoord);
        buf.writeFloat(this.xOffset);
        buf.writeFloat(this.yOffset);
        buf.writeFloat(this.zOffset);
        int i = this.particleType.getArgumentCount();

        for (int j = 0; j < i; ++j) {
            buf.writeFloat(this.particleArguments[j]);
        }
    }

    public static class MessageHandler {
        public static boolean onMessage(MessageModParticles message, Supplier<NetworkEvent.Context> ctx) {
            if (message.particleType.equals(EnumModParticles.SWEEP_ATTACK)) {
                PacketUtils.spawnSweepParticles(message);
            } else if (message.particleType.equals(EnumModParticles.EFFECT)) {
                PacketUtils.spawnEffect(message);
            }
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}