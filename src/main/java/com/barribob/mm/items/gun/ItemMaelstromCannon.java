package com.barribob.mm.items.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.init.Enchantments;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.items.gun.bullet.BulletFactory;
import com.barribob.mm.items.gun.bullet.MaelstromCannon;
import com.barribob.mm.util.ModUtils;

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
        world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.6F / (itemRand.nextFloat() * 0.4F + 0.8F));

        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
        float velocity = 1.0F;
        float inaccuracy = 3.0f;
        float degreesUp = 20;

        ModProjectile projectile = factory.get(world, player, stack, ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage()));
        projectile.shoot(player, player.rotationPitch - degreesUp, player.rotationYaw, 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(25f);
        level.addFreshEntity(projectile);
    }

    public Item setFactory(BulletFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("maelstrom_cannon"));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
