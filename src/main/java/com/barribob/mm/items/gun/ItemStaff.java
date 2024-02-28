package com.barribob.mm.items.gun;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.event_handlers.ItemToManaSystem;
import com.barribob.mm.items.ILeveledItem;
import com.barribob.mm.items.ItemBase;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;
import com.typesafe.config.Config;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import java.util.List;

public abstract class ItemStaff extends ItemBase implements ILeveledItem, IElement {
    private final float level;
    private Element element = Element.NONE;

    public ItemStaff(String name, float useTime, float level, CreativeModeTab tab) {
        super(name, tab);
        this.maxStackSize = 1;
        this.level = level;
        Config config = ItemToManaSystem.getManaConfig(new ItemStack(this));
        if(config != null) {
            this.setMaxDamage((int) (useTime / Math.max(1, config.getInt("cooldown_in_ticks"))));
        }
    }

    /**
     * If true, this item can be enchanted with damage enchantments like guns
     */
    public boolean doesDamage() {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> onItemRightClick(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if(!worldIn.isRemote) {
            itemStack.damageItem(1, playerIn);
            shoot(worldIn, playerIn, handIn, itemStack);
        }
        return new InteractionResultHolder<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
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
    public int getItemEnchantability() {
        return 1;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFull3D() {
        return true;
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
