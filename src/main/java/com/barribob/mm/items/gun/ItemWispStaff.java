package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.projectile.ProjectileWillOTheWisp;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A short range staff that burns enemies
 */
public class ItemWispStaff extends ItemStaff {
    public ItemWispStaff(String name, int maxDamage, float level, CreativeModeTab tab) {
        super(name, maxDamage, level, tab);
    }

    public float getBaseDamage() {
        return 5 * ModConfig.balance.weapon_damage;
    }

    /**
     * Shoot a bunch of projectiles
     */
    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_AMBIENT, SoundSource.NEUTRAL, 0.5F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 0.0f;
        float speed = 1f;

        ProjectileWillOTheWisp projectile = new ProjectileWillOTheWisp(world, player, ModUtils.getEnchantedDamage(stack, getLevel(), getBaseDamage()), stack);
        projectile.shoot(player, player.getXRot(), player.getYRot(), 0.0F, speed, inaccuracy);
        projectile.setTravelRange(9f);

        world.addFreshEntity(projectile);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ModUtils.translateDesc("wisp_staff").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
