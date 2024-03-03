package com.barribob.mm.entity.animation;

import com.barribob.mm.entity.model.ModelAnimatedBiped;

public class AnimationBackflip extends ArrayAnimation<ModelAnimatedBiped> {
    private static float[] leftArmX = {010, 020, 030, 040, 050, 060, 060, 050, 040, 030, 020, 010, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000};
    private static float[] rightArmX = {010, 020, 030, 040, 050, 060, 060, 050, 040, 030, 020, 010, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000};

    private static float[] bodyX = {010, 020, 030, 040, 050, 060, 060, 050, 040, 030, 020, 010, 000, -15, -15, -15, -15, -15, -15, -15, -15, 000, 015, 000};
    private static float[] centerX = {000, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000, 000, -45, -90, -135, -180, -225, -270, -315, -360, -360, -360, -360, -360};

    public AnimationBackflip() {
        super(leftArmX.length);
    }

    @Override
    public void setModelRotations(ModelAnimatedBiped model, float limbSwing, float limbSwingAmount, float partialTicks) {
        model.leftArm.xRot = (float) Math.toRadians(this.getInterpolatedFrame(leftArmX, partialTicks));
        model.rightArm.xRot = (float) Math.toRadians(this.getInterpolatedFrame(rightArmX, partialTicks));

        model.centerPivot.xRot = (float) Math.toRadians(this.getInterpolatedFrame(centerX, partialTicks));
        model.body.xRot = (float) Math.toRadians(this.getInterpolatedFrame(bodyX, partialTicks));
    }
}
