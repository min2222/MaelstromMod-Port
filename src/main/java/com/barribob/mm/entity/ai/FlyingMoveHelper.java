package com.barribob.mm.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;

/**
 * Taken from the ghast. I believe all this does is check if the area it wants
 * to move towards is valid, and the change course cooldown is not happening too
 * often
 */
public class FlyingMoveHelper extends MoveControl {
    private final Mob parentEntity;
    private int courseChangeCooldown;

    public FlyingMoveHelper(Mob e) {
        super(e);
        this.parentEntity = e;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            double d0 = this.wantedX - this.parentEntity.getX();
            double d1 = this.wantedY - this.parentEntity.getY();
            double d2 = this.wantedZ - this.parentEntity.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.parentEntity.getRandom().nextInt(5) + 2;
                d3 = Math.sqrt(d3);

                if (this.isNotColliding(this.wantedX, this.wantedY, this.wantedZ, d3)) {
                    this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(d0 / d3 * 0.1D, d1 / d3 * 0.1D, d2 / d3 * 0.1D));
                } else {
                    this.operation = MoveControl.Operation.WAIT;
                }
            }
        }
    }

    private boolean isNotColliding(double x, double y, double z, double p_179926_7_) {
        double d0 = (x - this.parentEntity.getX()) / p_179926_7_;
        double d1 = (y - this.parentEntity.getY()) / p_179926_7_;
        double d2 = (z - this.parentEntity.getZ()) / p_179926_7_;
        AABB axisalignedbb = this.parentEntity.getBoundingBox();

        for (int i = 1; i < p_179926_7_; ++i) {
            axisalignedbb = axisalignedbb.move(d0, d1, d2);

            if (!this.parentEntity.level.getEntityCollisions(this.parentEntity, axisalignedbb).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}