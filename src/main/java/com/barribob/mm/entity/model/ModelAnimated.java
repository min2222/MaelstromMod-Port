package com.barribob.mm.entity.model;

import java.util.function.Function;

import com.barribob.mm.util.IAnimatedMob;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public abstract class ModelAnimated<T extends LivingEntity> extends EntityModel<T> {
    public ModelAnimated(Function<ResourceLocation, RenderType> pRenderType) {
		super(RenderType::entityCutout);
	}
    
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTickTime, float pNetHeadYaw, float pHeadPitch) {
        if (entity instanceof IAnimatedMob) {
            // The partial tick time conditional prevent twitching when the animation is stopped after death
            ((IAnimatedMob) entity).getCurrentAnimation().setModelRotations(this, limbSwing, limbSwingAmount, entity.getHealth() > 0 ? partialTickTime : 0.99f);
        } else {
            throw new IllegalArgumentException("The entity class " + entity.getClass().getName() + " was not an instance of EntityLeveledMob");
        }
    }
}
