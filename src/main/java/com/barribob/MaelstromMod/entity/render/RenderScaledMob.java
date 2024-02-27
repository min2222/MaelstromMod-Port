package com.barribob.MaelstromMod.entity.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.entity.Mob;

public class RenderScaledMob<T extends Mob> extends RenderModEntity<T> {
    protected float scale;

    public <U extends ModelBase> RenderScaledMob(RenderManager rendermanagerIn, String textures, U modelClass, float scale) {
        super(rendermanagerIn, textures, modelClass);
        this.scale = scale;
    }

    @Override
    protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(scale, scale, scale);
    }
}
