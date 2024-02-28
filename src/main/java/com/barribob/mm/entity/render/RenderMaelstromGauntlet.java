package com.barribob.mm.entity.render;

import com.barribob.mm.entity.model.ModelMaelstromGauntlet;
import com.barribob.mm.util.Reference;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;

public class RenderMaelstromGauntlet extends RenderModEntity<Mob> {
    public RenderMaelstromGauntlet(RenderManager rendermanagerIn, String... textures) {
        super(rendermanagerIn, new ModelMaelstromGauntlet(), textures);
    }

    /**
     * Change to hurt texture whenever the gauntlet takes damage
     */
    @Override
    protected ResourceLocation getEntityTexture(Mob entity) {
        if (entity.hurtTime > 0) {
            return new ResourceLocation(Reference.MOD_ID + ":textures/entity/maelstrom_gauntlet_hurt.png");
        } else if (entity.getHealth() / entity.getMaxHealth() < 0.55) {
            return new ResourceLocation(Reference.MOD_ID + ":textures/entity/maelstrom_gauntlet_low_health.png");
        }
        return super.getEntityTexture(entity);
    }
}
