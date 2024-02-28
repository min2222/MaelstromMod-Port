package com.barribob.mm.entity.render;

import com.barribob.mm.entity.model.ModelAnimatedBiped;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class RenderAnimatedBiped<T extends Mob, M extends ModelAnimatedBiped<T>> extends HumanoidMobRenderer<T, M> {
    private ResourceLocation textures;

    public RenderAnimatedBiped(EntityRendererProvider.Context renderManagerIn, M modelBipedIn, float shadowSize, ResourceLocation textures) {
        super(renderManagerIn, modelBipedIn, shadowSize);
        this.textures = textures;
    }

    @Override
	public ResourceLocation getTextureLocation(T entity) {
        return this.textures;
    }
}
