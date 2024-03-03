package com.barribob.mm.items.tools;

import java.util.List;

import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileSwordSlash;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemMagisteelSword extends ToolSword {
    public ItemMagisteelSword(String name, Tier material, float level, Element element) {
        super(name, material, level, element);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entityLiving) {
        if (!entityLiving.level.isClientSide && entityLiving instanceof Player) {
            Player player = (Player) entityLiving;
            float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
            float atkCooldown = player.getAttackStrengthScale(0.5F);

            if (atkCooldown > 0.9F) {
                ModProjectile proj = new ProjectileSwordSlash(player.level, player, attackDamage);
                proj.setElement(this.getElement());
                proj.setTravelRange(4.5f);
                proj.shoot(player, player.getXRot(), player.getYRot(), 0.0F, 1.5f, 0);
                player.level.addFreshEntity(proj);
                player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F,
                        0.9F);
                if (!player.getAbilities().instabuild) {
                	stack.hurtAndBreak(1, player, t -> {
                		t.broadcastBreakEvent(t.getUsedItemHand());
                	});
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("magisteel_sword").withStyle(ChatFormatting.GRAY));
    }
}
