package com.barribob.mm.items.gun;

import java.util.List;
import java.util.function.Consumer;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.init.ModEnchantments;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.items.ILeveledItem;
import com.barribob.mm.items.ItemBase;
import com.barribob.mm.items.gun.bullet.BulletFactory;
import com.barribob.mm.items.gun.bullet.StandardBullet;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The base class for the mod's cooldown weapons. keeps track of cooldown and
 * calls shoot() when the gun sucessfully shoots
 */
public abstract class ItemGun extends ItemBase implements ILeveledItem, Reloadable, IElement {
    private final int maxCooldown;
    private static final int SMOKE_PARTICLES = 4;
    private float level;
    private final float damage;
    protected BulletFactory factory;
    private Consumer<List<Component>> information = (info) -> {
    };
    private Element element = Element.NONE;

    public ItemGun(String name, int cooldown, float damage, float level) {
        super(new Item.Properties().durability(ModItems.GUN_USE_TIME / cooldown));
        this.maxCooldown = cooldown;
        this.level = level;
        this.damage = damage;
        this.factory = new StandardBullet();
    }

    public ItemGun(String name, int cooldown, float damage, float level, Element element) {
        this(name, cooldown, damage, level);
        this.element = element;
    }

    public ItemGun setBullet(BulletFactory factory) {
        this.factory = factory;
        return this;
    }

    /**
     * Returns the correct multiplier based on the level of the item stack
     */
    private float getMultiplier() {
        return LevelHandler.getMultiplierFromLevel(this.level);
    }

    private float getEnchantedCooldown(ItemStack stack) {
        int reload = stack.getEnchantmentLevel(ModEnchantments.RELOAD.get());
        return this.maxCooldown * (1 - reload * 0.1f);
    }

    public float getEnchantedDamage(ItemStack stack) {
        float maxPower = ModEnchantments.GUN_POWER.get().getMaxLevel();
        float power = stack.getEnchantmentLevel(ModEnchantments.GUN_POWER.get());
        float maxDamageBonus = (float) Math.pow(ModConfig.balance.progression_scale, 2); // Maximum damage is two levels above
        float enchantmentBonus = 1 + ((power / maxPower) * (maxDamageBonus - 1));
        return this.damage * ModConfig.balance.weapon_damage * enchantmentBonus * this.getMultiplier();
    }

    /**
     * Returns a float between 0 and 1 to represent the cooldown of the gun
     */
    @Override
    public float getCooldownForDisplay(ItemStack stack) {
        if (stack.hasTag() && stack.getOrCreateTag().contains("cooldown")) {
            return stack.getOrCreateTag().getInt("cooldown") / this.getEnchantedCooldown(stack);
        }

        return 0;
    }

    /**
     * Taken from the bow class. Finds the appropriate ammo for the gun
     *
     * @param player
     * @return
     */
    private ItemStack findAmmo(Player player) {
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ItemAmmoCase) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        } else if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemAmmoCase) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = player.getInventory().getItem(i);

                if (itemstack.getItem() instanceof ItemAmmoCase) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    /**
     * Updates the gun's cooldown
     */
    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        // Use a compound to record the cooldown data for an item stack
        if (stack.hasTag()) {
            CompoundTag compound = stack.getOrCreateTag();

            // Decrement the cooldown every tick
            if (compound.contains("cooldown")) {
                if (entityIn instanceof Player && ((Player) entityIn).getItemInHand(InteractionHand.MAIN_HAND).equals(stack)
                        || ((Player) entityIn).getItemInHand(InteractionHand.OFF_HAND).equals(stack)) {
                    int cooldown = compound.getInt("cooldown") - 1;
                    compound.putInt("cooldown", cooldown >= 0 ? cooldown : 0);
                }
            } else {
                compound.putInt("cooldown", (int) this.getEnchantedCooldown(stack));
            }

            stack.setTag(compound);
        } else {
            stack.setTag(new CompoundTag());
        }
    }

    /**
     * Called when the equipped item is right clicked. Calls the shoot function
     * after handling ammo and cooldown
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        ItemStack ammoStack = findAmmo(playerIn);

        if (itemstack.hasTag() && itemstack.getOrCreateTag().contains("cooldown")) {
            CompoundTag compound = itemstack.getOrCreateTag();

            if ((playerIn.getAbilities().instabuild || !ammoStack.isEmpty()) && compound.getInt("cooldown") <= 0) {
                boolean dontConsumeAmmo = playerIn.getAbilities().instabuild
                        || itemstack.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;

                if (!dontConsumeAmmo) {
                    ammoStack.hurtAndBreak(ModUtils.getGunAmmoUse(this.level), playerIn, t -> {
                    });
                    itemstack.hurtAndBreak(1, playerIn, t -> {
                    });

                    if (ammoStack.isEmpty()) {
                        playerIn.getInventory().removeItem(ammoStack);
                    }
                }

                // Only the server spawns the projectile, otherwise things get weird
                if (!worldIn.isClientSide) {
                    shoot(worldIn, playerIn, handIn, itemstack);
                } else {
                    spawnShootParticles(worldIn, playerIn, handIn);
                }

                compound.putInt("cooldown", (int) this.getEnchantedCooldown(itemstack));
                itemstack.setTag(compound);

                return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemstack);
            }
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, itemstack);
    }

    /**
     * Called from the client side whenever the gun successfully shoots
     */
    @OnlyIn(Dist.CLIENT)
    protected void spawnShootParticles(Level worldIn, Player playerIn, InteractionHand handIn) {
        // Add the fire and smoke effects when the gun goes off
        Vec3 flameOffset = playerIn.getLookAngle().scale(0.5f);

        if (handIn == InteractionHand.MAIN_HAND) {
            flameOffset = flameOffset.yRot((float) Math.PI * -0.5f);
        } else {
            flameOffset = flameOffset.yRot((float) Math.PI * 0.5f);
        }

        flameOffset = flameOffset.add(playerIn.getLookAngle());

        worldIn.addParticle(ParticleTypes.FLAME, playerIn.getX() + flameOffset.x, playerIn.getY() + playerIn.getEyeHeight() + flameOffset.y, playerIn.getZ() + flameOffset.z,
                0, 0, 0);

        for (int i = 0; i < SMOKE_PARTICLES; i++) {
            float f = 0.1f;
            worldIn.addParticle(ParticleTypes.SMOKE, playerIn.getX() + flameOffset.x + ModRandom.getFloat(f),
                    playerIn.getY() + playerIn.getEyeHeight() + flameOffset.y + ModRandom.getFloat(f), playerIn.getZ() + flameOffset.z + ModRandom.getFloat(f), 0, 0, 0);
        }
    }

    public ItemGun setInformation(Consumer<List<Component>> information) {
        this.information = information;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(!ModConfig.gui.disableMaelstromArmorItemTooltips) {
            tooltip.add(ModUtils.getDisplayLevel(this.level));
        }

        if (this.getEnchantedDamage(stack) > 0) {
            this.getDamageTooltip(stack, worldIn, tooltip, flagIn);
        }

        tooltip.add(ModUtils.translateDesc("gun_ammo", ChatFormatting.DARK_PURPLE + "" + ModUtils.getGunAmmoUse(this.level)));
        tooltip.add(ModUtils.getCooldownTooltip(this.getEnchantedCooldown(stack)));
        if (!element.equals(Element.NONE) && !ModConfig.gui.disableElementalVisuals) {
            tooltip.add(ModUtils.getElementalTooltip(element));
        }
        information.accept(tooltip);
    }

    protected void getDamageTooltip(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.getDamageTooltip(this.getEnchantedDamage(stack)));
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on
     * material.
     */
    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 1;
    }

    @Override
    public float getLevel() {
        return this.level;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public Item setElement(Element element) {
        this.element = element;
        return this;
    }

    protected abstract void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack);
}
