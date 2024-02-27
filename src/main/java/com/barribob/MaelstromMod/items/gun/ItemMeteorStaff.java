package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.entity.projectile.ProjectileMeteorSpawner;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemMeteorStaff extends ItemStaff {
    public ItemMeteorStaff(String name, int useTime, float level, CreativeModeTab tab) {
        super(name, useTime, level, tab);
    }

    public float getBaseDamage() {
        return 10 * ModConfig.balance.weapon_damage;
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        float inaccuracy = 0.0f;
        float velocity = 3f;

        Projectile projectile = new ProjectileMeteorSpawner(world, player, ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage()), stack);
        projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(50);

        world.spawnEntity(projectile);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("meteor_staff"));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
