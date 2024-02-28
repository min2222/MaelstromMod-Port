package com.barribob.mm.entity.render;

import com.barribob.mm.entity.model.ModelAnimatedBiped;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;

public class RenderAnimatedBiped extends RenderBiped {
    private ResourceLocation textures;

    public RenderAnimatedBiped(RenderManager renderManagerIn, ModelAnimatedBiped modelBipedIn, float shadowSize, ResourceLocation textures) {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.textures = textures;
    }

    @Override
    protected ResourceLocation getEntityTexture(Mob entity) {
        return this.textures;
    }
}
