package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.init.ModCreativeTabs;
import com.barribob.MaelstromMod.items.ILeveledItem;
import com.barribob.MaelstromMod.items.ItemBase;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.LevelHandler;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

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
