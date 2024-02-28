package com.barribob.mm.entity.action;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.util.ModRandom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

/**
 * Taken from EntityWitch.java
 * Throws a potion given the potion type
 */
public class ActionThrowPotion implements IAction {
    private Item potionType;

    public ActionThrowPotion(Item potionType) {
        this.potionType = potionType;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        double d0 = target.getY() + target.getEyeHeight() - 1.100000023841858D;
        double d1 = target.getX() + target.getDeltaMovement().x - actor.getX();
        double d2 = d0 - actor.getY();
        double d3 = target.getZ() + target.getDeltaMovement().z - actor.getZ();
        float f = (float) Math.sqrt(d1 * d1 + d3 * d3);
        Potion[] potions = {Potions.POISON, Potions.SLOWNESS, Potions.WEAKNESS, Potions.HARMING};
        Potion potiontype = ModRandom.choice(potions);

        ThrownPotion entitypotion = new ThrownPotion(actor.level, actor);
        entitypotion.setItem(PotionUtils.setPotion(new ItemStack(potionType), potiontype));
        entitypotion.setXRot(entitypotion.getXRot() - -20.0F);
        entitypotion.shoot(d1, d2 + f * 0.2F, d3, 0.75F, 8.0F);
        actor.level.playSound((Player) null, actor.getX(), actor.getY(), actor.getZ(), SoundEvents.WITCH_THROW, actor.getSoundSource(), 1.0F, 0.8F + actor.level.random.nextFloat() * 0.4F);
        actor.level.addFreshEntity(entitypotion);
    }
}
