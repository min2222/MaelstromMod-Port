package com.barribob.mm.entity.animation;

import com.barribob.mm.entity.model.ModelAnimatedBiped;

public class AnimationSpinSlash extends ArrayAnimation<ModelAnimatedBiped> {
    private static float[] leftArmX = {-15, -30, -60, -75, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -60, -30, 000, 000, 000, 000, 000};
    private static float[] leftArmZ = {010, 020, 030, 040, 050, 060, 070, 070, 070, 070, 070, 070, 070, 070, 070, 070, 070, 070, 060, 050, 040, 030, 020, 010, 000};

    private static float[] rightArmX = {-15, -30, -60, -75, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -90, -60, -30, 000, 000, 000, 000, 000};
    private static float[] rightArmZ = {010, 020, 030, 040, 050, 060, 070, 070, 070, 070, 070, 070, 070, 070, 070, 070, 070, 070, 060, 050, 040, 030, 020, 010, 000};

    private static float[] bodyX = {005, 010, 015, 020, 025, 030, 030, 030, 030, 030, 030, 025, 020, 015, 010, 005, 000, 000, 000, 000, 000, 000, 000, 000, 000};
    private static float[] pivotY = {-10, -20, -30, -40, -50, -60, -70, -75, -75, -75, -75, 000, 075, 150, 215, 290, 360, 360, 360, 360, 360, 360, 360, 360, 360};

    private static float[] leftLegZ = {000, 000, 000, 000, 000, 000, 000, 000, 000, 000, -05, -10, -15, -15, -15, -15, -10, -05, 000, 000, 000, 000, 000, 000, 000};
    private static float[] rightLegZ = {000, 000, 000, 000, 000, 000, 000, 000, 000, 000, 005, 010, 015, 015, 015, 015, 010, 005, 000, 000, 000, 000, 000, 000, 000};

    public AnimationSpinSlash() {
        super(leftArmX.length);
    }

    @Override
    public void setModelRotations(ModelAnimatedBiped model, float limbSwing, float limbSwingAmount, float partialTicks) {
        model.centerPivot.yRot = (float) Math.toRadians(this.getInterpolatedFrame(pivotY, partialTicks));
        model.body.xRot = (float) Math.toRadians(this.getInterpolatedFrame(bodyX, partialTicks));
        model.leftArm.xRot = (float) Math.toRadians(this.getInterpolatedFrame(leftArmX, partialTicks));
        model.leftArm.zRot = (float) Math.toRadians(this.getInterpolatedFrame(leftArmZ, partialTicks));

        model.rightArm.xRot = (float) Math.toRadians(this.getInterpolatedFrame(rightArmX, partialTicks));
        model.rightArm.zRot = (float) Math.toRadians(this.getInterpolatedFrame(rightArmZ, partialTicks));

        model.leftLeg.zRot = (float) Math.toRadians(this.getInterpolatedFrame(leftLegZ, partialTicks));
        model.rightLeg.zRot = (float) Math.toRadians(this.getInterpolatedFrame(rightLegZ, partialTicks));
    }

}
