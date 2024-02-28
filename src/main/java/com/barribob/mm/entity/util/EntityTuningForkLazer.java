package com.barribob.mm.entity.util;

import com.barribob.mm.Main;
import com.barribob.mm.packets.MessageDirectionForRender;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class EntityTuningForkLazer extends Entity implements DirectionalRender {
    private Vec3 renderLazerPos;
    public static final int TICK_LIFE = 20;

    public EntityTuningForkLazer(Level worldIn) {
        super(worldIn);
    }

    public EntityTuningForkLazer(Level worldIn, Vec3 renderLazerPos) {
        super(worldIn);
        this.renderLazerPos = renderLazerPos;
    }

    @Override
    public void onUpdate() {
        if (this.tickCount > 1 && !this.level.isClientSide) {
            Main.network.sendToAllTracking(new MessageDirectionForRender(this, renderLazerPos), this);
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
        if (this.tickCount > TICK_LIFE) {
            this.setDead();
        }
        super.onUpdate();
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE && this.getRenderDirection() != null) {
            ModUtils.lineCallback(this.position(), this.getRenderDirection(), 10, (pos, i) -> {
                ParticleManager.spawnSwirl2(world, pos, ModColors.RED, Vec3.ZERO);
            });
        }
        super.handleStatusUpdate(id);
    }

    @Override
    public float getEyeHeight() {
        return 0;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(CompoundTag compound) {
    }

    @Override
    protected void writeEntityToNBT(CompoundTag compound) {
    }

    @Override
    public void setRenderDirection(Vec3 dir) {
        this.renderLazerPos = dir;
    }

    public Vec3 getRenderDirection() {
        return this.renderLazerPos;
    }
}
