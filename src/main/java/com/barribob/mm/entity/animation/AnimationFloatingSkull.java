package com.barribob.mm.entity.animation;

import com.barribob.mm.entity.model.ModelFloatingSkull;

/*
 * Jaw opening animation for the floating skull
 */
public class AnimationFloatingSkull extends ArrayAnimation<ModelFloatingSkull> {
    private static float[] jawAnimation = {0, 15, 25, 35, 35, 35, 35, 35, 35, 35, 35, 25, 15, 0};

    public AnimationFloatingSkull() {
        super(jawAnimation.length);
    }

    @Override
    public void setModelRotations(ModelFloatingSkull model, float limbSwing, float limbSwingAmount, float partialTicks) {
        model.jaw.xRot = (float) Math.toRadians(this.getInterpolatedFrame(jawAnimation, partialTicks));
    }
}
