package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.entities.herobrine_state.HerobrineState;
import com.barribob.mm.entity.entities.herobrine_state.StateCliffKey;
import com.barribob.mm.entity.entities.herobrine_state.StateCrimsonDimension;
import com.barribob.mm.entity.entities.herobrine_state.StateCrimsonKey;
import com.barribob.mm.entity.entities.herobrine_state.StateEnderPearls;
import com.barribob.mm.entity.entities.herobrine_state.StateFirstBattle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/*
 * Controls the herobrine fight, with dialogue and ending
 */
public class Herobrine extends EntityLeveledMob {
    public final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.NOTCHED_20));
    private static final String nbtState = "herobrine_state";
    public HerobrineState state;

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 20.0F));
    }

    public Herobrine(Level worldIn) {
        super(worldIn);
        state = new StateEnderPearls(this);
        this.setSize(0.5f, 2.0f);
        this.setImmovable(true);
        this.setNoGravity(true);
    }

    @Override
	public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            state.rightClick(player);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        state.update();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player) {
            state.leftClick(this);
        }
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains(nbtState)) {
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
        super.readAdditionalSaveData(compound);
    }

    public void teleportOutside() {
        this.setImmovablePosition(new Vec3(this.getX(), this.getY(), this.getZ() - 5));
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, (byte) 4);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            teleportOutside();
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void writeEntityToNBT(CompoundTag compound) {
        compound.putString(nbtState, state.getNbtString());
        super.writeEntityToNBT(compound);
    }
}
