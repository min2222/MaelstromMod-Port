package com.barribob.mm.items.tools;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.entity.particleSpawners.ParticleSpawnerExplosion;
import com.barribob.mm.util.ModUtils;

public class ToolExplosiveDagger extends ToolDagger {
    public ToolExplosiveDagger(String name, ToolMaterial material, float level) {
        super(name, material, level);
    }

    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        if (target != null) {
            float attackDamage = (float) player.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue();
            int fireFactor = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, player.getHeldItemMainhand()) * 5;
            ModUtils.handleAreaImpact(4, (e) -> attackDamage * 0.5f, player, target.position().add(ModUtils.yVec(-0.1f)), DamageSource.causeExplosionDamage(player), 1,
                    fireFactor);
            player.world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.GENERIC_EXPLODE, player.getSoundCategory(), 1.0F, 0.9F);
            Entity particle = new ParticleSpawnerExplosion(player.world);
            particle.copyLocationAndAnglesFrom(target);
            player.level.addFreshEntity(particle);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("explosive_dagger"));
    }
}
