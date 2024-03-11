package com.barribob.mm.entity.render;

import com.barribob.mm.entity.entities.EntityChaosKnight;
import com.barribob.mm.entity.model.ModelChaosKnight;
import com.barribob.mm.entity.model.ModelChaosShield;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;
import com.barribob.mm.util.RenderUtils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class RenderChaosKnight extends RenderModEntity<EntityChaosKnight> {

    public RenderChaosKnight(RenderManager rendermanagerIn, String... textures) {
        super(rendermanagerIn, new ModelChaosKnight(), textures);
        this.addLayer(new LayerShield(new ModelChaosShield()));
    }

    @Override
    public void doRender(EntityChaosKnight entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        if (entity.getLazerTarget().isPresent()) {
            RenderUtils.drawLazer(renderManager, entity.position().add(ModUtils.yVec(entity.getEyeHeight())), entity.getTarget().get(), new Vec3(x, y, z), ModColors.RED, entity, partialTicks);
        }
    }

    private class LayerShield implements LayerRenderer<EntityChaosKnight> {
        private ModelChaosShield shield;
        private final ResourceLocation SHIELD = new ResourceLocation(Reference.MOD_ID + ":textures/entity/chaos_shield.png");

        public LayerShield(ModelChaosShield shield) {
            this.shield = shield;
        }

        @Override
        public void doRenderLayer(EntityChaosKnight knight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (!knight.isSwingingArms()) {
                bindTexture(SHIELD);
                RenderUtils.renderAura(knight, () -> {
                    float ticks = knight.tickCount + partialTicks;
                    GlStateManager.translate(0.0f, ticks * 0.02f, 0.0F);
                    GlStateManager.color(1, 1, 1, 0.3F);
                }, () -> {
                    GlStateManager.scale(1.1, 1.1, 1.0);
                    GlStateManager.translate(0.15f, -0.5f, -0.1F);
                    shield.render(knight, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                });
            }
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }

    }
}
