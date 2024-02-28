package com.barribob.mm.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtils {
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");

    /*
     * Draws an element above the entity kind of like a name tag
     */
    public static void drawElement(PoseStack stack, Font fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch,
                                   boolean isThirdPersonFrontal, boolean isSneaking, int ticks, float partialTicks, float scale) {
    	stack.pushPose();
        stack.translate(x, y, z);
        stack.mulPose(Vector3f.YP.rotationDegrees(-viewerYaw));
        stack.mulPose(Vector3f.XP.rotationDegrees((isThirdPersonFrontal ? -1 : 1) * viewerPitch));
        stack.scale(-0.025F * scale, -0.025F * scale, 0.025F * scale);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        // Oscillating color
        float alpha = (float) ((Math.sin((ticks + partialTicks) * 0.1) * 0.15f) + 0.8f);
        fontRendererIn.draw(stack, str, -fontRendererIn.width(str) / 2, verticalShift, ModColors.toIntegerColor(255, 255, 255, (int) (alpha * 255)));
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        stack.popPose();
    }

    public static void drawLazer(PoseStack stack, Vec3 startPos, Vec3 endPos, Vec3 offset, Vec3 color, Mob entity, float partialTicks) {
        drawBeam(stack, startPos, endPos, offset, color, entity, partialTicks);
    }

    /**
     * Draws a beam, requires a bound texture
     *
     * @param renderManager
     * @param pos1
     * @param pos2
     * @param offset
     * @param color
     * @param entity
     * @param partialTicks
     */
    public static void drawBeam(PoseStack stack, Vec3 startPos, Vec3 endPos, Vec3 offset, Vec3 color, Entity entity, float partialTicks) {
        drawBeam(stack, startPos, endPos, offset, color, entity, partialTicks, new Vec3(1, 1, 1));
    }

    public static void drawBeam(PoseStack stack, Vec3 startPos, Vec3 endPos, Vec3 offset, Vec3 color, Entity entity, float partialTicks, Vec3 scale) {
    	Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.texParameter(3553, 10242, 10497);
        RenderSystem.texParameter(3553, 10243, 10497);
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        float time = entity.level.getGameTime() + partialTicks;
        float f3 = time * 0.5F % 1.0F;
        double timeLooped = time * 0.05D * -1.5D;

        stack.pushPose();
        stack.translate((float) offset.x, (float) offset.y + entity.getEyeHeight(), (float) offset.z);

        Vec3 line = endPos.subtract(startPos);
        double lineLength = line.length();
        Vec3 lineDir = line.normalize();

        float angle1 = (float) Math.acos(lineDir.y);
        float angle2 = (float) Math.atan2(lineDir.z, lineDir.x);

        stack.mulPose(Vector3f.YP.rotationDegrees((float) Math.toDegrees((Math.PI / 2F + -angle2))));
        stack.mulPose(Vector3f.XP.rotationDegrees((float) Math.toDegrees(angle1)));
        stack.scale((float)scale.x, (float)scale.y, (float)scale.z);

        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, GUARDIAN_BEAM_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
        bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);

        int red = (int) (color.x * 255);
        int green = (int) (color.y * 255);
        int blue = (int) (color.z * 255);

        double d4 = 0.0D + Math.cos(timeLooped + 2.356194490192345D) * 0.282D;
        double d5 = 0.0D + Math.sin(timeLooped + 2.356194490192345D) * 0.282D;
        double d6 = 0.0D + Math.cos(timeLooped + (Math.PI / 4D)) * 0.282D;
        double d7 = 0.0D + Math.sin(timeLooped + (Math.PI / 4D)) * 0.282D;
        double d8 = 0.0D + Math.cos(timeLooped + 3.9269908169872414D) * 0.282D;
        double d9 = 0.0D + Math.sin(timeLooped + 3.9269908169872414D) * 0.282D;
        double d10 = 0.0D + Math.cos(timeLooped + 5.497787143782138D) * 0.282D;
        double d11 = 0.0D + Math.sin(timeLooped + 5.497787143782138D) * 0.282D;
        double d12 = 0.0D + Math.cos(timeLooped + Math.PI) * 0.2D;
        double d13 = 0.0D + Math.sin(timeLooped + Math.PI) * 0.2D;
        double d14 = 0.0D + Math.cos(timeLooped + 0.0D) * 0.2D;
        double d15 = 0.0D + Math.sin(timeLooped + 0.0D) * 0.2D;
        double d16 = 0.0D + Math.cos(timeLooped + (Math.PI / 2D)) * 0.2D;
        double d17 = 0.0D + Math.sin(timeLooped + (Math.PI / 2D)) * 0.2D;
        double d18 = 0.0D + Math.cos(timeLooped + (Math.PI * 3D / 2D)) * 0.2D;
        double d19 = 0.0D + Math.sin(timeLooped + (Math.PI * 3D / 2D)) * 0.2D;
        double d22 = -1.0F + f3;
        double d23 = lineLength * 2.5D + d22;
        bufferbuilder.vertex(stack.last().pose(), (float)d12, (float)lineLength, (float)d13).uv(0.4999F, (float)d23).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d12, 0.0F, (float)d13).uv(0.4999F, (float)d22).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d14, 0.0F, (float)d15).uv(0.0F, (float)d22).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d14, (float)lineLength, (float)d15).uv(0.0F, (float)d23).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d16, (float)lineLength, (float)d17).uv(0.4999F, (float)d23).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d16, 0.0F, (float)d17).uv(0.4999F, (float)d22).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d18, 0.0F, (float)d19).uv(0.0F, (float)d22).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d18, (float)lineLength, (float)d19).uv(0.0F, (float)d23).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        double d24 = 0.0D;

        if (entity.tickCount % 2 == 0) {
            d24 = 0.5D;
        }

        bufferbuilder.vertex(stack.last().pose(), (float)d4, (float)lineLength, (float)d5).uv(0.5F, (float)d24 + 0.5F).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d6, (float)lineLength, (float)d7).uv(1.0F, (float)d24 + 0.5F).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d10, (float)lineLength, (float)d11).uv(1.0F, (float)d24).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(stack.last().pose(), (float)d8, (float)lineLength, (float)d9).uv(0.5F, (float)d24).color(red, green, blue, 255).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        stack.popPose();
    }

    /**
     * Allows for a common way to generate the creeper charge effect without copying tons of gl code everywhere
     */
    public static void renderAura(LivingEntity entity, Runnable translationCallback, Runnable renderCallback) {
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();

        translationCallback.run();

        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

        renderCallback.run();

        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
    }
}
