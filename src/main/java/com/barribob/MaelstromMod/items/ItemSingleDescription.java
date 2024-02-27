package com.barribob.MaelstromMod.items;

import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemSingleDescription extends ItemBase {
    private String desc;

    public ItemSingleDescription(String name, String desc, CreativeModeTab tab) {
        super(name, tab);
        this.desc = desc;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc(desc));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
