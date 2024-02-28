package com.barribob.mm.entity.entities;

import net.minecraft.world.entity.ai.attributes.Attributes;

import com.barribob.mm.entity.entities.herobrine_state.*;

import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.level.Level;

/*
 * Controls the herobrine fight, with dialogue and ending
 */
public class Herobrine extends EntityLeveledMob {
    public final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossEvent.Color.PURPLE, BossEvent.Overlay.NOTCHED_20));
    private static final String nbtState = "herobrine_state";
    public HerobrineState state;

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(7, new EntityAIWatchClosest(this, Player.class, 20.0F));
    }

    public Herobrine(Level worldIn) {
        super(worldIn);
        state = new StateEnderPearls(this);
        this.setSize(0.5f, 2.0f);
        this.setImmovable(true);
        this.setNoGravity(true);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected boolean processInteract(Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            state.rightClick(player);
        }
        return super.processInteract(player, hand);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        state.update();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() instanceof Player) {
            state.leftClick(this);
        }
        return false;
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void addTrackingPlayer(ServerPlayer player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayer player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void readEntityFromNBT(CompoundTag compound) {
        if (compound.hasKey(nbtState)) {
            if (compound.getString(nbtState).equals(new StateCliffKey(this).getNbtString())) {
                state = new StateCliffKey(this);
            } else if (compound.getString(nbtState).equals(new StateCrimsonKey(this).getNbtString())) {
                state = new StateCrimsonKey(this);
            } else if (compound.getString(nbtState).equals(new StateFirstBattle(this).getNbtString())) {
                state = new StateFirstBattle(this);
            } else if (compound.getString(nbtState).equals(new StateCrimsonDimension(this).getNbtString())) {
                state = new StateCrimsonDimension(this);
            }
        }
        super.readEntityFromNBT(compound);
    }

    public void teleportOutside() {
        this.setImmovablePosition(new Vec3(posX, posY, posZ - 5));
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, (byte) 4);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            teleportOutside();
        }
        super.handleStatusUpdate(id);
    }

    @Override
    public void writeEntityToNBT(CompoundTag compound) {
        compound.setString(nbtState, state.getNbtString());
        super.writeEntityToNBT(compound);
    }
}
