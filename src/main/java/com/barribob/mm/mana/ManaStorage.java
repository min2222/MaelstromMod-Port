package com.barribob.mm.mana;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ManaStorage implements IStorage<IMana> {
    private static final String locked = "locked";
    private static final String mana = "mana";
    private static final String recentlyConsumed = "recentlyConsumed";

    @Override
    public NBTBase writeNBT(Capability<IMana> capability, IMana instance, Direction side) {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat(mana, instance.getMana());
        nbt.putBoolean(locked, instance.isLocked());
        nbt.putBoolean(recentlyConsumed, instance.isRecentlyConsumed());
        return nbt;
    }

    @Override
    public void readNBT(Capability<IMana> capability, IMana instance, Direction side, NBTBase nbt) {
        if (nbt instanceof CompoundTag) {
            CompoundTag compound = (CompoundTag) nbt;
            if (compound.contains(mana) && compound.contains(locked)) {
                instance.setLocked(compound.getBoolean(locked));
                instance.set(compound.getFloat(mana));
                instance.setRecentlyConsumed(compound.getBoolean(recentlyConsumed));
            }
        }
    }
}
