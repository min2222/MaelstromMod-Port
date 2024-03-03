package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.event_handlers.ItemToManaSystem;
import com.barribob.mm.items.ILeveledItem;
import com.barribob.mm.items.ItemBase;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;
import com.typesafe.config.Config;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class ItemStaff extends ItemBase implements ILeveledItem, IElement {
    private final float level;
    private final float useTime;
    private Element element = Element.NONE;

    public ItemStaff(String name, float useTime, float level, CreativeModeTab tab) {
        super(name, tab);
        this.level = level;
        this.useTime = useTime;
    }
    
    @Override
    public int getMaxStackSize(ItemStack stack) {
    	return 1;
    }
    
    @Override
    public int getMaxDamage(ItemStack stack) {
        Config config = ItemToManaSystem.getManaConfig(new ItemStack(this));
    	return config == null ? super.getMaxDamage(stack) : (int) (this.useTime / Math.max(1, config.getInt("cooldown_in_ticks")));
    }

    /**
     * If true, this item can be enchanted with damage enchantments like guns
     */
    public boolean doesDamage() {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemStack = playerIn.getItemInHand(handIn);
        if(!worldIn.isClientSide) {
            itemStack.hurtAndBreak(1, playerIn, t -> {
            	
            });
            shoot(worldIn, playerIn, handIn, itemStack);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(!ModConfig.gui.disableMaelstromArmorItemTooltips) {
            tooltip.add(ModUtils.getDisplayLevel(this.level));
        }
        if (!element.equals(Element.NONE) && !ModConfig.gui.disableElementalVisuals) {
            tooltip.add(ModUtils.getElementalTooltip(element));
        }
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

    public ItemStaff setElement(Element element) {
        this.element = element;
        return this;
    }

    protected abstract void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack);
}
