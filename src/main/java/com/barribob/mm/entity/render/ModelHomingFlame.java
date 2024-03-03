package com.barribob.mm.entity.render;

import com.barribob.mm.entity.projectile.ProjectileHomingFlame;
import com.barribob.mm.util.ModUtils;

import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ModelHomingFlame extends ShulkerBulletModel<ProjectileHomingFlame> {
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        Vec3 lookVec = ModUtils.getEntityVelocity(entityIn).normalize();
        Vec3 direction = ModUtils.rotateVector2(lookVec, lookVec.cross(ModUtils.Y_AXIS), (ageInTicks * 10) % 360);
        Vec2f pitchYaw = ModUtils.getPitchYaw(direction);
        renderer.rotateAngleX = (float) Math.toRadians(pitchYaw.y);
        renderer.rotateAngleY = (float) Math.toRadians(pitchYaw.x);
    }
}
