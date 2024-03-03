package com.barribob.mm.items.tools;

import java.util.List;

import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A slower melee weapons with a large sweep attack
 */
public class ToolBattleaxe extends ToolSword implements ISweepAttackOverride {
    public ToolBattleaxe(String name, Tier material, float level) {
        super(name, material, level);
    }

    /**
     * Increased sweep attack
     */
    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        ModUtils.doSweepAttack(player, target, getElement(), (e) -> {
        }, 10, 2);
    }

    @Override
    public float getDamage() {
        return super.getDamage() * 1.25f;
    }

    @Override
    protected double getAttackSpeed() {
        return -3.1D;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("large_sweep_attack").withStyle(ChatFormatting.GRAY));
    }
}
