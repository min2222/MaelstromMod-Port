package com.barribob.mm.entity.render;

import com.barribob.mm.entity.entities.EntityMaelstromBeast;
import com.barribob.mm.entity.model.ModelBeast;
import com.barribob.mm.entity.model.ModelMaelstromBeast;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderMaelstromBeast extends RenderModEntity<EntityMaelstromBeast, ModelBeast> {
    public ResourceLocation TEXTURES_1 = new ResourceLocation(com.barribob.mm.util.Reference.MOD_ID + ":textures/entity/maelstrom_beast.png");
    public ResourceLocation TEXTURES_2 = new ResourceLocation(com.barribob.mm.util.Reference.MOD_ID + ":textures/entity/skeleton_minotaur.png");

    public RenderMaelstromBeast(EntityRendererProvider.Context rendermanagerIn) {
        super(rendermanagerIn, "", new ModelMaelstromBeast());
    }

    @Override
	public ResourceLocation getTextureLocation(EntityMaelstromBeast entity) {
        if (entity.isRaged()) {
            return TEXTURES_2;
        }
        return TEXTURES_1;
    }
}
