package com.barribob.mm.entity.render;

import com.barribob.mm.entity.entities.EntityGoldenBoss;
import com.barribob.mm.entity.model.ModelStatueOfNirvana;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.Reference;
import com.barribob.mm.util.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerStatueOfNirvanaArmor extends RenderLayer<EntityGoldenBoss, ModelStatueOfNirvana> {
    private static final ResourceLocation ARMOR_TEXTURE = new ResourceLocation(Reference.MOD_ID + ":textures/entity/statue_armor.png");
    private final RenderStatueOfNirvana render;

    public LayerStatueOfNirvanaArmor(RenderStatueOfNirvana render) {
        this.render = render;
    }

    @Override
    public void doRenderLayer(EntityGoldenBoss entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.isInvulnerableTo(ModDamageSource.MAELSTROM_DAMAGE)) {
            float f1 = 0.8F;
            this.render.bindTexture(ARMOR_TEXTURE);
            RenderUtils.renderAura(entitylivingbaseIn, () -> {
                float ticks = entitylivingbaseIn.tickCount + partialTicks;
                GlStateManager.translate(ticks * 0.01F, ticks * 0.01F, 0.0F);
                GlStateManager.color(f1, f1, f1, 1.0F);
            }, () -> {
                Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
                GlStateManager.scale(1.02, 1.02, 1.02);
                this.render.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            });
        }
    }
}