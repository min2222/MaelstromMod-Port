package com.barribob.mm.invasion;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.util.Reference;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.storage.WorldSavedData;

public class InvasionWorldSaveData extends SavedData {
    private int timeUntilInvasion = ModConfig.world.invasionTime * 60 * 1000; // The number of milliseconds until invasion
    private boolean invaded = false;
    private int dimensionTime = 1000; // An additional second when coming out of a dimension to let the server get the correct player position
    public static final String DATA_NAME = Reference.MOD_ID + "_InvasionData";

    @SuppressWarnings("unused")
    public InvasionWorldSaveData(String s) {
        super(s);
    }

    public InvasionWorldSaveData() {
        super(DATA_NAME);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        if (nbt.hasKey("invaded") && nbt.hasKey("invasion_time")) {
            this.timeUntilInvasion = nbt.getInteger("invasion_time");
            this.invaded = nbt.getBoolean("invaded");
        }
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag compound) {
        compound.setBoolean("invaded", invaded);
        compound.setInteger("invasion_time", timeUntilInvasion);
        return compound;
    }

    public void update(int milliseconds) {
        this.timeUntilInvasion = Math.max(0, this.timeUntilInvasion - milliseconds);
        this.markDirty();
    }

    // Called when no players are in the overworld to allow for extra time
    // when they do return to the overworld
    public void setDimensionCooldownTime() {
        this.timeUntilInvasion = Math.max(dimensionTime, this.timeUntilInvasion);
    }

    public void setInvasionTime(int time) {
        this.timeUntilInvasion = time;
    }

    public int getInvasionTime() {
        return this.timeUntilInvasion;
    }

    public boolean isInvaded() {
        return invaded;
    }

    public void setInvaded(boolean invaded) {
        this.invaded = invaded;
        this.markDirty();
    }

    public boolean shouldDoInvasion() {
        return !invaded && timeUntilInvasion <= 0;
    }
}
