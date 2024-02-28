package com.barribob.mm.items.gun.bullet;

import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileBullet;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StandardBullet implements BulletFactory {
    @Override
    public ModProjectile get(Level world, Player player, ItemStack stack, float damage) {
        return new ProjectileBullet(world, player, damage, stack);
    }
}
