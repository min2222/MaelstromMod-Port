package com.barribob.mm.items.tools;

import java.util.List;

import com.barribob.mm.entity.particleSpawners.ParticleSpawnerExplosion;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class ToolExplosiveDagger extends ToolDagger {
    public ToolExplosiveDagger(String name, Tier material, float level) {
        super(name, material, level);
    }

    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        if (target != null) {
            float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
            int fireFactor = player.getMainHandItem().getEnchantmentLevel(Enchantments.FIRE_ASPECT) * 5;
            ModUtils.handleAreaImpact(4, (e) -> attackDamage * 0.5f, player, target.position().add(ModUtils.yVec(-0.1f)), DamageSource.explosion(player), 1,
                    fireFactor);
            player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 1.0F, 0.9F);
            Entity particle = new ParticleSpawnerExplosion(player.level);
            particle.copyPosition(target);
            player.level.addFreshEntity(particle);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("explosive_dagger").withStyle(ChatFormatting.GRAY));
    }
}
