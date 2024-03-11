package com.barribob.mm.entity.animation;

import com.barribob.mm.entity.model.ModelAnimatedBiped;

public class AnimationHerobrineGroundSlash extends ArrayAnimation<ModelAnimatedBiped> {
    private static float[] leftArmX = {-10, -20, -30, -40, -50, -60, -70, -80, -90, -90, -90, -90, -90, -90, -60, -30, 000, 030, 045, 045, 030, 015, 000, 000};
    private static float[] leftArmZ = {-05, -10, -15, -20, -25, -30, -35, -40, -45, -45, -45, -45, -45, -45, -30, -15, 000, 015, 015, 015, 015, 005, 000, 000};

    private static float[] rightArmX = {-10, -20, -30, -40, -50, -60, -70, -80, -90, -90, -90, -90, -90, -90, -60, -30, 000, 030, 045, 045, 030, 015, 000, 000};
    private static float[] rightArmZ = {005, 010, 015, 020, 025, 030, 035, 040, 045, 045, 045, 045, 045, 045, 030, 015, 000, -15, -15, -15, -15, -05, 000, 000};

    private static float[] bodyX = {000, 000, 000, -05, -10, -15, -15, -15, -15, -15, -15, -15, -15, -15, -15, -07, 000, 007, 015, 022, 022, 015, 007, 000};

    public AnimationHerobrineGroundSlash() {
        super(leftArmX.length);
    }

    @Override
    public void setModelRotations(ModelAnimatedBiped model, float limbSwing, float limbSwingAmount, float partialTicks) {
        model.leftArm.xRot = (float) Math.toRadians(this.getInterpolatedFrame(leftArmX, partialTicks));
        model.leftArm.zRot = (float) Math.toRadians(this.getInterpolatedFrame(leftArmZ, partialTicks));

        model.rightArm.xRot = (float) Math.toRadians(this.getInterpolatedFrame(rightArmX, partialTicks));
        model.rightArm.zRot = (float) Math.toRadians(this.getInterpolatedFrame(rightArmZ, partialTicks));

        model.body.xRot = (float) Math.toRadians(this.getInterpolatedFrame(bodyX, partialTicks));
    }
}