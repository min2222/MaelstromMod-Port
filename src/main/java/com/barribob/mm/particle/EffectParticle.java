package com.barribob.mm.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EffectParticle extends TextureSheetParticle {
    /**
     * the scale of the flame FX
     */
    private final float flameScale;

    protected EffectParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.xd = this.xd * 0.009999999776482582D + xSpeedIn;
        this.yd = this.yd * 0.009999999776482582D + ySpeedIn;
        this.zd = this.zd * 0.009999999776482582D + zSpeedIn;
        this.x += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.y += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.z += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
        this.flameScale = this.quadSize;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
        this.setParticleTextureIndex(146);
    }

    public void move(double x, double y, double z) {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    /**
     * Renders the particle
     */
    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        float f = ((float) this.age + pPartialTicks) / (float) this.lifetime;
        this.quadSize = this.flameScale * (1.0F - f * f * 0.5F);
    	super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    public int getLightColor(float p_189214_1_) {
        float f = ((float) this.age + p_189214_1_) / (float) this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(p_189214_1_);
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f * 15.0F * 16.0F);

        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.move(this.xd, this.yd, this.zd);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory {
        public Particle createParticle(int particleID, Level worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new EffectParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}