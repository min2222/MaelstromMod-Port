package com.barribob.mm.items.gun;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.init.ModCreativeTabs;
import com.barribob.mm.items.ILeveledItem;
import com.barribob.mm.items.ItemBase;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;

public class ItemAmmoCase extends ItemBase implements ILeveledItem {
    private float level;

    public ItemAmmoCase(String name, float level) {
        super(name, ModCreativeTabs.ITEMS);
        this.level = level;
        this.setMaxDamage(Math.round(LevelHandler.getMultiplierFromLevel(level) * 110));
        this.setMaxStackSize(1);
    }

    @Override
    public float getLevel() {
        return this.level;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        if(!ModConfig.gui.disableMaelstromArmorItemTooltips) {
            tooltip.add(ModUtils.getDisplayLevel(this.level));
        }
    }
}
