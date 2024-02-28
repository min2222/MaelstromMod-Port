package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.util.ModUtils;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class AIRandomFly extends Goal {
    private final Mob parentEntity;

    public AIRandomFly(Mob e) {
        this.parentEntity = e;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        MoveControl entitymovehelper = this.parentEntity.getMoveControl();

        if (!entitymovehelper.hasWanted()) {
            return true;
        } else {
            double d0 = entitymovehelper.getWantedX() - this.parentEntity.getX();
            double d1 = entitymovehelper.getWantedY() - this.parentEntity.getY();
            double d2 = entitymovehelper.getWantedZ() - this.parentEntity.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        RandomSource random = this.parentEntity.getRandom();
        double d0 = this.parentEntity.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
        double d1 = this.parentEntity.getY() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
        double d2 = this.parentEntity.getX() + (random.nextFloat() * 2.0F - 1.0F) * 16.0F;
        this.parentEntity.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
    }

    @Override
    public void tick() {
        Vec3 pos = ModUtils.getEntityVelocity(parentEntity).normalize().scale(5).add(parentEntity.position());
        ModUtils.facePosition(pos, parentEntity, 10, 10);
        parentEntity.getLookControl().setLookAt(pos.x, pos.y, pos.z, 3, 3);
        super.tick();
    }
}
