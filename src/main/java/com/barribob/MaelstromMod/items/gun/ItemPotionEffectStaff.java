package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.init.ModCreativeTabs;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

/**
 * A staff designed to activate potion effects when used
 */
public class ItemPotionEffectStaff extends ItemStaff {
    Supplier<PotionEffect[]> effects;
    String desc_loc;

    public ItemPotionEffectStaff(String name, float level, Supplier<PotionEffect[]> effects, String desc_loc) {
        super(name, ModItems.STAFF_USE_TIME, level, ModCreativeTabs.ITEMS);
        this.effects = effects;
        this.desc_loc = desc_loc;
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 1.0f, 1.0f + ModRandom.getFloat(0.2f));
        for (PotionEffect effect : effects.get()) {
            player.addPotionEffect(effect);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc(desc_loc));
    }
}
