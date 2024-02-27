package com.barribob.MaelstromMod.items.gun.bullet;

import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.entity.projectile.ProjectileGoldenFireball;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GoldenFireball implements BulletFactory {
    @Override
    public Projectile get(Level world, Player player, ItemStack stack, float damage) {
        return new ProjectileGoldenFireball(world, player, damage, stack);
    }
}
