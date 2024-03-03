package com.barribob.mm.items.armor;

import java.util.List;

import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemSpeedBoots extends ModArmorBase {
    private MobEffectInstance wornEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0);

    public ItemSpeedBoots(String name, ArmorMaterial materialIn, int renderIndex, EquipmentSlot equipmentSlotIn, float maelstrom_armor, String textureName) {
        super(name, materialIn, renderIndex, equipmentSlotIn, maelstrom_armor, textureName);
    }

    @Override
    public void onArmorTick(ItemStack itemStack, Level world, Player player) {
        super.onArmorTick(itemStack, world, player);
        if (itemStack != null && itemStack.getItem() == this) {
            wornEffect = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 0);
            player.addEffect(wornEffect);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        String potion = ChatFormatting.BLUE + I18n.translateToLocal(wornEffect.getEffectName()).trim();
        if (wornEffect.getAmplifier() > 0) {
            potion = potion + " " + I18n.translateToLocal("potion.potency." + wornEffect.getAmplifier()).trim();
        }
        tooltip.add(ModUtils.translateDesc("potion_add", potion));
    }
}
