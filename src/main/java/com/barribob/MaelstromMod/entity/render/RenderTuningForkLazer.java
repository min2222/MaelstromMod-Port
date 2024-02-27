package com.barribob.MaelstromMod.entity.render;

import com.barribob.MaelstromMod.entity.util.EntityTuningForkLazer;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.RenderUtils;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTuningForkLazer extends Render<EntityTuningForkLazer> {
    public RenderTuningForkLazer(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void doRender(EntityTuningForkLazer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        renderManager.renderEngine.bindTexture(RenderDragon.ENDERCRYSTAL_BEAM_TEXTURES);
        if (entity.getRenderDirection() != null) {
            double scale = (EntityTuningForkLazer.TICK_LIFE / (entity.ticksExisted + partialTicks)) / EntityTuningForkLazer.TICK_LIFE;
            RenderUtils.drawBeam(renderManager, entity.getPositionVector(), entity.getRenderDirection(), new Vec3(x, y, z), ModColors.RED, entity, partialTicks, new Vec3(scale, 1, scale));
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTuningForkLazer entity) {
        return null;
    }
}