package com.barribob.mm.items.gun;

import net.minecraft.world.item.ItemStack;

public interface Reloadable {
    public float getCooldownForDisplay(ItemStack stack);
}
