package com.barribob.mm.items.gun;

import com.barribob.mm.entity.projectile.ModProjectile;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemRifle extends ItemGun {
    public ItemRifle(String name, float level) {
        super(name, 60, 9, level);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 0.0f;
        float velocity = 6.0f;

        ModProjectile projectile = factory.get(world, player, stack, this.getEnchantedDamage(stack));
        projectile.setElement(getElement());
        projectile.shoot(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(100);

        world.addFreshEntity(projectile);
    }
}
