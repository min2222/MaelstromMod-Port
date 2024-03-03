package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectilePiercingBullet;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemPiercer extends ItemGun {
    public ItemPiercer(String name, float level) {
        super(name, 60, 8, level);
    }

    @Override
    protected void getDamageTooltip(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.translateDesc("elk_rifle").withStyle(ChatFormatting.GRAY));
        super.getDamageTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 0.0f;
        float velocity = 6.0f;

        ModProjectile projectile = new ProjectilePiercingBullet(world, player, this.getEnchantedDamage(stack), stack);
        projectile.setElement(getElement());
        projectile.shoot(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(100);

        world.addFreshEntity(projectile);
    }
}
