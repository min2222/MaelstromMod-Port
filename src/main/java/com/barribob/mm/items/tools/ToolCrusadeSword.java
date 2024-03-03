package com.barribob.mm.items.tools;

import java.util.List;

import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ToolCrusadeSword extends ToolSword implements ISweepAttackOverride {
    public ToolCrusadeSword(String name, Tier material, float level) {
        super(name, material, level);
    }

    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        ModUtils.doSweepAttack(player, target, getElement(), (e) -> {
        });
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("crusade_sword").withStyle(ChatFormatting.GRAY));
    }
}
