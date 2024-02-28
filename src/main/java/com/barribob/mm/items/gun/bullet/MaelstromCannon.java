package com.barribob.mm.items.gun.bullet;

import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileMaelstromCannon;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MaelstromCannon implements BulletFactory {
    @Override
    public ModProjectile get(Level world, Player player, ItemStack stack, float damage) {
        return new ProjectileMaelstromCannon(world, player, damage, stack);
    }
}
