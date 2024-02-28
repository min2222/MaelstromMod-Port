package com.barribob.mm.entity.projectile;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.init.ModEnchantments;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A helper class to record enchantments from the gun stack
 */
public class ProjectileGun extends ModProjectile {
    private int knockbackStrength;
    private int maelstromDestroyer;
    private int criticalHit;
    private boolean isCritical;
    private static final byte CRITICAL_BYTE = 5;

    public ProjectileGun(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack, Element element) {
        this(worldIn, throwerIn, baseDamage, stack);
        this.setElement(element);
    }

    public ProjectileGun(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage);

        if (stack != null) {
            this.knockbackStrength = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.impact, stack);
            this.maelstromDestroyer = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.maelstrom_destroyer, stack);
            this.criticalHit = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.critical_hit, stack);
            if (rand.nextInt(8) == 0 && this.criticalHit > 0 && !level.isClientSide) {
                this.isCritical = true;
                this.setDamage(this.getDamage() * this.criticalHit * 2.5f);
            }
            if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.gun_flame, stack) > 0) {
                this.setFire(100);
            }
            if (stack.getItem() instanceof IElement) {
                this.setElement(((IElement) stack.getItem()).getElement());
            }
        }
    }

    protected int getKnockback() {
        return this.knockbackStrength;
    }

    protected float getGunDamage(Entity entity) {
        if (!EntityMaelstromMob.CAN_TARGET.apply(entity)) {
            float maxDamageBonus = (float) (Math.pow(ModConfig.balance.progression_scale, 2.5) - 1); // Max damage is slightly more than the damage enchantment
            float damageBonus = super.getDamage() * maxDamageBonus * (this.maelstromDestroyer / (float) ModEnchantments.maelstrom_destroyer.getMaxLevel());
            return super.getDamage() + damageBonus;
        }

        return super.getDamage();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isCritical) {
            level.broadcastEntityEvent(this, this.CRITICAL_BYTE);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == this.CRITICAL_BYTE) {
            world.spawnParticle(ParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0, 0, 0);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public ProjectileGun(Level worldIn) {
        super(worldIn);
    }

    public ProjectileGun(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }
}
