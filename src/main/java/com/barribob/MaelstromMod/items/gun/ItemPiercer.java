package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.entity.projectile.ProjectilePiercingBullet;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemPiercer extends ItemGun {
    public ItemPiercer(String name, float level) {
        super(name, 60, 8, level);
    }

    @Override
    protected void getDamageTooltip(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("elk_rifle"));
        super.getDamageTooltip(stack, worldIn, tooltip, flagIn);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 0.0f;
        float velocity = 6.0f;

        Projectile projectile = new ProjectilePiercingBullet(world, player, this.getEnchantedDamage(stack), stack);
        projectile.setElement(getElement());
        projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(100);

        world.spawnEntity(projectile);
    }
}
