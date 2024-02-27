package com.barribob.MaelstromMod.items.gun;

import net.minecraft.world.item.ItemStack;

public interface Reloadable {
    public float getCooldownForDisplay(ItemStack stack);
}
