package com.barribob.MaelstromMod.items.armor;

import com.barribob.MaelstromMod.items.armor.model.ModelNyanHelmet;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

public class ArmorNyanHelmet extends ModArmorBase {
    public ArmorNyanHelmet(String name, ArmorMaterial materialIn, int renderIndex, EquipmentSlot equipmentSlotIn, float maelstrom_armor, String textureName) {
        super(name, materialIn, renderIndex, equipmentSlotIn, maelstrom_armor, textureName);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected ModelBiped getCustomModel() {
        return new ModelNyanHelmet();
    }
}
