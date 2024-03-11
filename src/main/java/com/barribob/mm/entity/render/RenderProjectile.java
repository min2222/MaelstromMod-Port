package com.barribob.mm.entity.render;

import com.barribob.mm.entity.projectile.ModProjectile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Makes a projectile registered with this class render with the given item
 **/
@OnlyIn(Dist.CLIENT)
public class RenderProjectile<T extends Entity> extends Render<T> {
    private final RenderItem itemRenderer;
    private final Item itemToRender;

    public RenderProjectile(RenderManager renderManagerIn, RenderItem itemRendererIn, Item item) {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
        this.itemToRender = item;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (this.itemToRender != null) {
            this.itemRenderer.renderItem(new ItemStack(this.itemToRender), ItemCameraTransforms.TransformType.GROUND);
        } else if (entity instanceof ModProjectile) {
            this.itemRenderer.renderItem(new ItemStack(((ModProjectile) entity).getItemToRender()), ItemCameraTransforms.TransformType.GROUND);
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}