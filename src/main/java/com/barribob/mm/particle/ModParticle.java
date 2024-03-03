package com.barribob.mm.particle;

import javax.annotation.Nonnull;

import com.barribob.mm.util.ModUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ModParticle extends TextureSheetParticle {
    public int minIndex;
    public int indexRange;
    public float animationSpeed;
    public boolean isLit;

    public ModParticle(ClientLevel worldIn, Vec3 pos, Vec3 motion, float scale, int age, boolean isLit) {
        super(worldIn, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        this.quadSize = scale;
        this.lifetime = age;
        this.xd = motion.x;
        this.yd = motion.y;
        this.zd = motion.z;
        this.isLit = isLit;
    }

    // Vanilla copy: most code from Particle#renderParticle, adjusted to allow for custom textures
    @Override
    public void renderParticle(@Nonnull BufferBuilder buffer, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ModUtils.PARTICLE);
        Tessellator tessellator = Tessellator.getInstance();
        float f = (float)this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float)this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            f = this.particleTexture.getMinU();
            f1 = this.particleTexture.getMaxU();
            f2 = this.particleTexture.getMinV();
            f3 = this.particleTexture.getMaxV();
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3[] avec3d = new Vec3[] {new Vec3(-rotationX * f4 - rotationXY * f4, -rotationZ * f4, -rotationYZ * f4 - rotationXZ * f4), new Vec3((-rotationX * f4 + rotationXY * f4), (rotationZ * f4), (-rotationYZ * f4 + rotationXZ * f4)), new Vec3((rotationX * f4 + rotationXY * f4), (rotationZ * f4), (rotationYZ * f4 + rotationXZ * f4)), new Vec3((rotationX * f4 - rotationXY * f4), (-rotationZ * f4), (rotationYZ * f4 - rotationXZ * f4))};

        if (this.particleAngle != 0.0F)
        {
            float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = Mth.cos(f8 * 0.5F);
            float f10 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.x;
            float f11 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.y;
            float f12 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.z;
            Vec3 vec3d = new Vec3(f10, f11, f12);

            for (int l = 0; l < 4; ++l)
            {
                avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.cross(avec3d[l]).scale((2.0F * f9)));
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();
        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.pos(f5 + avec3d[0].x, f6 + avec3d[0].y, f7 + avec3d[0].z).tex(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(f5 + avec3d[1].x, f6 + avec3d[1].y, f7 + avec3d[1].z).tex(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(f5 + avec3d[2].x, f6 + avec3d[2].y, f7 + avec3d[2].z).tex(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(f5 + avec3d[3].x, f6 + avec3d[3].y, f7 + avec3d[3].z).tex(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
        tessellator.draw();
        GlStateManager.enableLighting();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return 3;
    }

    @Override
    public void tick() {
        this.setParticleTextureIndex(minIndex + (int) (this.indexRange * (((this.age * this.animationSpeed) % this.lifetime) / this.lifetime)));

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.move(this.xd, this.yd, this.zd);
    }

    public void setParticleTextureRange(int minIndex, int range, float speed) {
        if (minIndex < 0 || range < 0 || minIndex + range > 255 || speed < 0) {
            throw new IllegalArgumentException("Texture ranges are invalid.");
        }

        this.minIndex = minIndex;
        this.indexRange = range;
        animationSpeed = speed;
        this.setParticleTextureIndex(minIndex + (int) (this.indexRange * (((this.age * this.animationSpeed) % this.lifetime) / this.lifetime)));
    }

    @Override
    public int getLightColor(float p_189214_1_) {
        // Light like the firework particle
        if (isLit) {
            float f = (this.age + p_189214_1_) / this.lifetime;
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
        return super.getLightColor(p_189214_1_);
    }
}
