package com.barribob.mm.items.tools;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.ModUtils;

/**
 * A slower melee weapons with a large sweep attack
 */
public class ToolBattleaxe extends ToolSword implements ISweepAttackOverride {
    public ToolBattleaxe(String name, ToolMaterial material, float level) {
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
    public float getAttackDamage() {
        return super.getAttackDamage() * 1.25f;
    }

    @Override
    protected double getAttackSpeed() {
        return -3.1D;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("large_sweep_attack"));
    }
}
