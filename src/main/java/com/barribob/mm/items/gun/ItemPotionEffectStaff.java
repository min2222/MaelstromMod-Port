package com.barribob.mm.items.gun;

import java.util.List;
import java.util.function.Supplier;

import com.barribob.mm.init.ModCreativeTabs;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A staff designed to activate potion effects when used
 */
public class ItemPotionEffectStaff extends ItemStaff {
    Supplier<MobEffectInstance[]> effects;
    String desc_loc;

    public ItemPotionEffectStaff(String name, float level, Supplier<MobEffectInstance[]> effects, String desc_loc) {
        super(name, ModItems.STAFF_USE_TIME, level, ModCreativeTabs.ITEMS);
        this.effects = effects;
        this.desc_loc = desc_loc;
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.0f, 1.0f + ModRandom.getFloat(0.2f));
        for (MobEffectInstance effect : effects.get()) {
            player.addEffect(effect);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc(desc_loc).withStyle(ChatFormatting.GRAY));
    }
}
