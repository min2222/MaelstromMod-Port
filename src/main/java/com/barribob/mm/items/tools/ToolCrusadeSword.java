package com.barribob.mm.items.tools;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.ModUtils;

public class ToolCrusadeSword extends ToolSword implements ISweepAttackOverride {
    public ToolCrusadeSword(String name, ToolMaterial material, float level) {
        super(name, material, level);
    }

    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        ModUtils.doSweepAttack(player, target, getElement(), (e) -> {
        });
        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 0));
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("crusade_sword"));
    }
}
