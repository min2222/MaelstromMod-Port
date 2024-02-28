package com.barribob.mm.entity.render;

import com.barribob.mm.entity.entities.EntityMonolith;
import com.barribob.mm.entity.model.ModelMonolith;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;
import com.barribob.mm.util.RenderUtils;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class RenderMonolith extends RenderModEntity<EntityMonolith> {
    public ResourceLocation TEXTURES_1 = new ResourceLocation(Reference.MOD_ID + ":textures/entity/monolith.png");
    public ResourceLocation TEXTURES_2 = new ResourceLocation(Reference.MOD_ID + ":textures/entity/monolith_blue.png");
    public ResourceLocation TEXTURES_3 = new ResourceLocation(Reference.MOD_ID + ":textures/entity/monolith_red.png");
    public ResourceLocation TEXTURES_4 = new ResourceLocation(Reference.MOD_ID + ":textures/entity/monolith_yellow.png");

    public RenderMonolith(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelMonolith(), "monolith.png");
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMonolith entity) {
        switch (entity.getAttackColor()) {
            case EntityMonolith.noAttack:
                return TEXTURES_1;
            case EntityMonolith.blueAttack:
                return TEXTURES_2;
            case EntityMonolith.redAttack:
                return TEXTURES_3;
            case EntityMonolith.yellowAttack:
                return TEXTURES_4;
        }
        return TEXTURES_1;
    }

    @Override
    public void doRender(EntityMonolith entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        // Render the monolith lazer. Taken from the guardian lazer thingy
        if (entity.getTarget().isPresent()) {
            RenderUtils.drawLazer(renderManager, entity.position().add(ModUtils.yVec(entity.getEyeHeight())), entity.getTarget().get(), new Vec3(x, y, z), new Vec3(1, 0, 0), entity, partialTicks);
        }
    }
}
