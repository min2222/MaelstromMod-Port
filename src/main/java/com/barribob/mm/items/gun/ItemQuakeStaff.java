package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.projectile.ProjectileQuake;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A short range quake attack
 */
public class ItemQuakeStaff extends ItemStaff {
    public ItemQuakeStaff(String name, int maxDamage, float level, CreativeModeTab tab) {
        super(name, maxDamage, level, tab);
    }

    public float getBaseDamage() {
        return 6 * ModConfig.balance.weapon_damage;
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        float inaccuracy = 0.0f;
        float speed = 0.5f;
        float pitch = 0; // Projectiles aim straight ahead always

        // Shoots projectiles in a small arc
        for (int i = 0; i < 5; i++) {
            ProjectileQuake projectile = new ProjectileQuake(world, player, ModUtils.getEnchantedDamage(stack, getLevel(), getBaseDamage()), stack);
            projectile.setPos(player.getX(), player.getY(), player.getZ());
            projectile.shoot(player, pitch, player.getYRot() - 20 + (i * 10), 0.0F, speed, inaccuracy);
            projectile.setTravelRange(8f);
            world.addFreshEntity(projectile);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ModUtils.translateDesc("quake_staff").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
