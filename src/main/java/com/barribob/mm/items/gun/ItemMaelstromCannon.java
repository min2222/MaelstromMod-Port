package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.items.gun.bullet.BulletFactory;
import com.barribob.mm.items.gun.bullet.MaelstromCannon;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

/**
 * A simple medium range weapon
 */
public class ItemMaelstromCannon extends ItemStaff {
    private BulletFactory factory = new MaelstromCannon();

    public ItemMaelstromCannon(String name, int maxDamage, float level, CreativeModeTab tab) {
        super(name, maxDamage, level, tab);
    }

    public float getBaseDamage() {
        return 5 * ModConfig.balance.weapon_damage;
    }

    /**
     * Shoot a single projectile
     */
    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.6F / (world.random.nextFloat() * 0.4F + 0.8F));

        int power = stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        int knockback = stack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
        float velocity = 1.0F;
        float inaccuracy = 3.0f;
        float degreesUp = 20;

        ModProjectile projectile = factory.get(world, player, stack, ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage()));
        projectile.shoot(player, player.getXRot() - degreesUp, player.getYRot(), 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(25f);
        world.addFreshEntity(projectile);
    }

    public Item setFactory(BulletFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ModUtils.translateDesc("maelstrom_cannon").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
