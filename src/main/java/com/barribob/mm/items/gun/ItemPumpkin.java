package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.entity.projectile.ProjectilePumpkin;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemPumpkin extends ItemGun {
    public ItemPumpkin(String name, int cooldown, Item ammo, float level) {
        super(name, cooldown, 0.5f, level);
    }

    /**
     * Shoot a single bullet
     */
    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 0.0f;
        float velocity = 7.0f;

        ProjectilePumpkin projectile = new ProjectilePumpkin(world, player, this.getEnchantedDamage(stack), stack);
        projectile.shoot(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(1000f);

        world.addFreshEntity(projectile);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("pumpkin").withStyle(ChatFormatting.GRAY));
        tooltip.add(ModUtils.translateDesc("pumpkin_damage").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void getDamageTooltip(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.translateDesc("damage_per_meter_tooltip", ChatFormatting.BLUE + ModUtils.DF_0.format(this.getEnchantedDamage(stack)) + ChatFormatting.GRAY));
    }
}
