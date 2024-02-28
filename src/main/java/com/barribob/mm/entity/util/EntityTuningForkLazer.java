package com.barribob.mm.entity.util;

import com.barribob.mm.Main;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.packets.MessageDirectionForRender;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public class EntityTuningForkLazer extends Entity implements DirectionalRender {
    private Vec3 renderLazerPos;
    public static final int TICK_LIFE = 20;

    public EntityTuningForkLazer(Level worldIn) {
        super(ModEntities.TUNIG_FORK_LAZER.get(), worldIn);
    }

    public EntityTuningForkLazer(Level worldIn, Vec3 renderLazerPos) {
        super(ModEntities.TUNIG_FORK_LAZER.get(), worldIn);
        this.renderLazerPos = renderLazerPos;
    }

    @Override
    public void tick() {
        if (this.tickCount > 1 && !this.level.isClientSide) {
            Main.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new MessageDirectionForRender(this, renderLazerPos));
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
        if (this.tickCount > TICK_LIFE) {
            this.discard();
        }
        super.tick();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE && this.getRenderDirection() != null) {
            ModUtils.lineCallback(this.position(), this.getRenderDirection(), 10, (pos, i) -> {
                ParticleManager.spawnSwirl2(level, pos, ModColors.RED, Vec3.ZERO);
            });
        }
        super.handleEntityEvent(id);
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return 0;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    public void setRenderDirection(Vec3 dir) {
        this.renderLazerPos = dir;
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
    	return NetworkHooks.getEntitySpawningPacket(this);
    }

    public Vec3 getRenderDirection() {
        return this.renderLazerPos;
    }
}
