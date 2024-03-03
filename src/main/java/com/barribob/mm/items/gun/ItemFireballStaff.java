package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.items.gun.bullet.BulletFactory;
import com.barribob.mm.items.gun.bullet.Fireball;
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

public class ItemFireballStaff extends ItemStaff {
    private BulletFactory factory = new Fireball();

    public ItemFireballStaff(String name, int useTime, float level, CreativeModeTab tab) {
        super(name, useTime, level, tab);
    }

    private float getBaseDamage() {
        return 10 * ModConfig.balance.weapon_damage;
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL, 1.0F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 2.0f;
        float velocity = 1.3f;

        ModProjectile projectile = factory.get(world, player, stack, ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage()));
        projectile.shoot(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(25);

        world.addFreshEntity(projectile);
    }

    public ItemFireballStaff setFactory(BulletFactory factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ModUtils.translateDesc("fireball_staff").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
