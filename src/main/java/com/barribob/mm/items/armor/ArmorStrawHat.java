package com.barribob.mm.items.armor;

import com.barribob.mm.items.armor.model.ModelStrawHat;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmorStrawHat extends ModArmorBase {
    public ArmorStrawHat(String name, ArmorMaterial materialIn, int renderIndex, EquipmentSlot equipmentSlotIn, float maelstrom_armor, String textureName) {
        super(name, materialIn, renderIndex, equipmentSlotIn, maelstrom_armor, textureName);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected ModelBiped getCustomModel() {
        return new ModelStrawHat();
    }
}
