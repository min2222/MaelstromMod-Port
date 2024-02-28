package com.barribob.mm.entity.animation;

import net.minecraft.client.model.Model;

public interface Animation<T extends Model> {
    /*
     * Called by the entity's model to get the rotations it needs
     */
    public void setModelRotations(T model, float limbSwing, float limbSwingAmount, float partialTicks);

    /*
     * Called by the entity to move the animation forward
     */
    public void update();

    /*
     * Reset the animation back to the beginning of its frames
     */
    public void startAnimation();
}
