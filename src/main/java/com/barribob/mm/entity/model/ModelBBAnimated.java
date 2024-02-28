package com.barribob.mm.entity.model;

import com.barribob.mm.entity.animation.AnimationManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ModelBBAnimated<T extends Entity> extends EntityModel<T> {
    private float partialTicks;

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.partialTicks = ageInTicks;
        AnimationManager.resetModel(this);
        if (entityIn instanceof LivingEntity) {
            AnimationManager.setModelRotations(this, (LivingEntity) entityIn, limbSwing, limbSwingAmount, this.partialTicks);
        }
    }

	@Override
	public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
		
	}
}
