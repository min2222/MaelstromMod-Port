package com.barribob.mm.mana;

import net.minecraft.core.Direction;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ManaProvider implements ICapabilitySerializable<Tag> {
    @CapabilityInject(IMana.class)
    public static final Capability<IMana> MANA = null;

    private IMana instance = MANA.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, Direction facing) {
        return MANA == capability;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction facing) {
        return MANA == capability ? MANA.<T>cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return MANA.getStorage().writeNBT(MANA, instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        MANA.getStorage().readNBT(MANA, instance, null, nbt);
    }
}