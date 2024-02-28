package com.barribob.mm.entity.render;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.renderer.ITarget;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;
import com.barribob.mm.util.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * Renders an entity with a generic type, texture, and model passed in.
 */
public class RenderModEntity<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
    public String[] TEXTURES;
    private ResourceLocation DEATH_TEXTURES;

    public RenderModEntity(EntityRendererProvider.Context rendermanagerIn, String textures, M modelClass) {
        this(rendermanagerIn, modelClass, new String[]{textures});
    }

    public RenderModEntity(EntityRendererProvider.Context rendermanagerIn, M modelClass, String... textures) {
        super(rendermanagerIn, modelClass, 0.5f);
        if (textures.length == 0) {
            throw new IllegalArgumentException("Must provide at least one texture to render an entity.");
        }
        this.TEXTURES = textures;
        this.DEATH_TEXTURES = new ResourceLocation(String.format("%s:textures/entity/disintegration_%d_%d.png", Reference.MOD_ID, modelClass.textureWidth, modelClass.textureHeight));
    }

    @Override
	public ResourceLocation getTextureLocation(T entity) {
        String texture = TEXTURES[0];
        if (entity instanceof IElement) {
            IElement e = (IElement) entity;
            if (e.getElement().equals(Element.AZURE) && TEXTURES.length >= 2 && TEXTURES[1] != null) {
                texture = TEXTURES[1];
            } else if (e.getElement().equals(Element.GOLDEN) && TEXTURES.length >= 3 && TEXTURES[2] != null) {
                texture = TEXTURES[2];
            } else if (e.getElement().equals(Element.CRIMSON) && TEXTURES.length >= 4 && TEXTURES[3] != null) {
                texture = TEXTURES[3];
            }
        }

        return new ResourceLocation(Reference.MOD_ID + ":textures/entity/" + texture);
    }
    @Override
    protected void setupRotations(T entityLiving, PoseStack pMatrixStack, float p_77043_2_, float rotationYaw, float partialTicks) {
        if (entityLiving instanceof EntityMaelstromMob && Minecraft.getInstance().getFramebuffer().isStencilEnabled() && GL11.glGetInteger(GL11.GL_STENCIL_BITS) > 0) {
        	pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        } else {
            super.setupRotations(entityLiving, pMatrixStack, p_77043_2_, rotationYaw, partialTicks);
        }
    }

    @Override
    protected void renderModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        if (entity instanceof EntityMaelstromMob) {
            if (entity.getHealth() <= 0 && Minecraft.getMinecraft().getFramebuffer().isStencilEnabled() && GL11.glGetInteger(GL11.GL_STENCIL_BITS) > 0) {
                float f = entity.deathTime / (15f); // The alpha value required to render a pixel

                // Use the stencil buffer to first record where to draw with the disintegration texture,
                // and then the entity render only renders where the stencil buffer has drawn
                int stencilVal = 42;
                int maskPass = 0xff;
                GL11.glEnable(GL11.GL_STENCIL_TEST);
                GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT);
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
                GL11.glStencilFunc(GL11.GL_ALWAYS, stencilVal, maskPass); // Everything passes the stencil test
                GL11.glStencilMask(0xFF); // Enable writing to stencil buffer

                // Disable color to keep from the disintegration render from affecting the actual visual output
                GlStateManager.colorMask(false, false, false, false);
                GlStateManager.depthMask(false);

                // Use the alpha function to make more and more pixels get cut off as alpha threshold gets larger
                GlStateManager.enableAlpha();
                GlStateManager.alphaFunc(GL11.GL_GREATER, f);

                // Write the disintegrated mob to the stencil buffer
                this.bindTexture(DEATH_TEXTURES);
                this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

                GL11.glEnable(GL11.GL_COLOR_BUFFER_BIT);
                // Disable stencil buffer and enable rendering again
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.depthMask(true);

                // Apply the stencil filter I think
                GL11.glStencilFunc(GL11.GL_NOTEQUAL, 0, maskPass);

                // Return alpha function to what it was before (probably what it was before)
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);

                super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

                GL11.glDisable(GL11.GL_STENCIL_TEST);
            } else {
                super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            }
        } else {
            super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        }
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!entity.isInvisible()) {
            // The blending here allows for rendering of translucent textures
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();

            if (entity instanceof EntityLeveledMob) {
                ((EntityLeveledMob) entity).doRender(this.renderManager, x, y, z, entityYaw, partialTicks);
                if (ModConfig.entities.displayLevel) {
                    this.renderLivingLabel(entity, "Level: " + ((EntityLeveledMob) entity).getLevel(), x, y, z, 10);
                }
            }

            if (entity instanceof IElement && !((IElement) entity).getElement().equals(Element.NONE) && !ModConfig.gui.disableElementalVisuals) {
                double d0 = entity.getDistanceSq(this.renderManager.renderViewEntity);
                double maxDistance = 20;

                if (d0 <= maxDistance * maxDistance) {
                    boolean flag = entity.isSneaking();
                    float f = this.renderManager.playerViewY;
                    float f1 = this.renderManager.playerViewX;
                    boolean flag1 = this.renderManager.options.thirdPersonView == 2;
                    float f2 = entity.height + 0.5F - (flag ? 0.25F : 0.0F);
                    int verticalOffset = this.canRenderName(entity) ? -6 : 0;
                    float scale = (float) entity.getBoundingBox().getAverageEdgeLength();
                    RenderUtils.drawElement(this.getFontRendererFromRenderManager(), ((IElement) entity).getElement().textColor + ((IElement) entity).getElement().symbol, (float) x, (float) y + f2, (float) z,
                            verticalOffset, f, f1, flag1, flag,
                            entity.tickCount, partialTicks, scale);
                }
            }
        } else {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    public boolean shouldRender(@Nonnull T livingEntity, @Nonnull Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ))
        {
            return true;
        }

        if (livingEntity instanceof ITarget) {
            Optional<Vec3> optional = ((ITarget) livingEntity).getLazerTarget();
            if(optional.isPresent()) {
                Vec3 end = optional.get();
                Vec3 start = livingEntity.getEyePosition(1);
                return camera.isVisible(ModUtils.makeBox(start, end));
            }
        }

        return false;
    }
}
