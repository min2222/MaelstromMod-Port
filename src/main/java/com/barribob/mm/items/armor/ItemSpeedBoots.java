package com.barribob.mm.items.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.util.ModUtils;

public class ItemSpeedBoots extends ModArmorBase {
    private PotionEffect wornEffect = new PotionEffect(MobEffects.SPEED, 20, 0);

    public ItemSpeedBoots(String name, ArmorMaterial materialIn, int renderIndex, EquipmentSlot equipmentSlotIn, float maelstrom_armor, String textureName) {
        super(name, materialIn, renderIndex, equipmentSlotIn, maelstrom_armor, textureName);
    }

    @Override
    public void onArmorTick(Level world, Player player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
        if (itemStack != null && itemStack.getItem() == this) {
            wornEffect = new PotionEffect(MobEffects.SPEED, 20, 0);
            player.addPotionEffect(wornEffect);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        String potion = ChatFormatting.BLUE + I18n.translateToLocal(wornEffect.getEffectName()).trim();
        if (wornEffect.getAmplifier() > 0) {
            potion = potion + " " + I18n.translateToLocal("potion.potency." + wornEffect.getAmplifier()).trim();
        }
        tooltip.add(ModUtils.translateDesc("potion_add", potion));
    }
}
