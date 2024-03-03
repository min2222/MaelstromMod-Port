package com.barribob.mm.entity.projectile;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.init.ModEnchantments;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;

import net.minecraft.core.particles.DustParticleOptions;
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
            this.knockbackStrength = stack.getEnchantmentLevel(ModEnchantments.IMPACT.get());
            this.maelstromDestroyer = stack.getEnchantmentLevel(ModEnchantments.MAELSTROM_DESTROYER.get());
            this.criticalHit = stack.getEnchantmentLevel(ModEnchantments.CRITICAL_HIT.get());
            if (random.nextInt(8) == 0 && this.criticalHit > 0 && !level.isClientSide) {
                this.isCritical = true;
                this.setDamage(this.getDamage() * this.criticalHit * 2.5f);
            }
            if (stack.getEnchantmentLevel(ModEnchantments.GUN_FLAME.get()) > 0) {
                this.setSecondsOnFire(100);
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
        if (entity instanceof LivingEntity living && !EntityMaelstromMob.CAN_TARGET.apply(living)) {
            float maxDamageBonus = (float) (Math.pow(ModConfig.balance.progression_scale, 2.5) - 1); // Max damage is slightly more than the damage enchantment
            float damageBonus = super.getDamage() * maxDamageBonus * (this.maelstromDestroyer / (float) ModEnchantments.MAELSTROM_DESTROYER.get().getMaxLevel());
            return super.getDamage() + damageBonus;
        }

        return super.getDamage();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isCritical) {
            level.broadcastEntityEvent(this, ProjectileGun.CRITICAL_BYTE);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == ProjectileGun.CRITICAL_BYTE) {
            level.addParticle(DustParticleOptions.REDSTONE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public ProjectileGun(Level worldIn) {
        super(worldIn);
    }

    public ProjectileGun(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }
}
