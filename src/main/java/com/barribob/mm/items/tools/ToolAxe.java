package com.barribob.mm.items.tools;

import net.minecraft.world.item.CreativeModeTab;

import com.barribob.mm.Main;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.IHasModel;

import net.minecraft.item.ItemAxe;

public class ToolAxe extends ItemAxe implements IHasModel {
    public ToolAxe(String name, ToolMaterial material) {
        super(material, 5.0f, -3.0f);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(CreativeModeTab.COMBAT);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
