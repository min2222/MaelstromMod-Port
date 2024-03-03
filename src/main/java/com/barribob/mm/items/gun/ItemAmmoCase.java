package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.items.ILeveledItem;
import com.barribob.mm.items.ItemBase;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemAmmoCase extends ItemBase implements ILeveledItem {
    private float level;

    public ItemAmmoCase(String name, float level) {
        super(new Item.Properties().durability(Math.round(LevelHandler.getMultiplierFromLevel(level) * 110)));
        this.level = level;
    }

    @Override
    public float getLevel() {
        return this.level;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(!ModConfig.gui.disableMaelstromArmorItemTooltips) {
            tooltip.add(ModUtils.getDisplayLevel(this.level));
        }
    }
}
