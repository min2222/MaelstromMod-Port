package com.barribob.MaelstromMod.entity.model;

import com.barribob.MaelstromMod.entity.animation.AnimationManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ModelBBAnimated extends ModelBase {
    private float partialTicks;

    @Override
    public void setLivingAnimations(LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        this.partialTicks = partialTickTime;
        AnimationManager.resetModel(this);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        if (entityIn instanceof LivingEntity) {
            AnimationManager.setModelRotations(this, (LivingEntity) entityIn, limbSwing, limbSwingAmount, this.partialTicks);
        }
    }
}
