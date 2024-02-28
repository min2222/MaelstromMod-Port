package com.barribob.mm.entity.animation;

import net.minecraft.client.model.Model;

public class AnimationNone extends ArrayAnimation {
    public AnimationNone() {
        super(0);
    }

    @Override
    public void setModelRotations(Model model, float limbSwing, float limbSwingAmount, float partialTicks) {
    }
}