package com.barribob.mm.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * A custom elytra. The actual functionality is spread across multiple event handlers, as Minecraft doesn't make a custom elytra an easy process.
 */
public class ItemModElytra extends ItemBase {
    private static final UUID ARMOR_MODIFIER = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");
    private final ArmorMaterial material;

    public ItemModElytra(String name, ArmorMaterial material) {
        super(new Item.Properties().stacksTo(1));
        this.material = material;
    }

    // Taken from {@code ItemElytra}
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        EquipmentSlot entityequipmentslot = Mob.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = playerIn.getItemBySlot(entityequipmentslot);

        if (itemstack1.isEmpty()) {
            playerIn.setItemSlot(entityequipmentslot, itemstack.copy());
            itemstack.setCount(0);
            return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemstack);
        } else {
            return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, itemstack);
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return armorType == EquipmentSlot.CHEST;
    }

    @Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.CHEST) {
            return Reference.MOD_ID + ":textures/models/armor/" + this.getDescriptionId().replace("item.", "") + ".png";
        }
        return null;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EquipmentSlot.CHEST) {
            multimap.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER, "Armor modifier", this.material.getDefenseForSlot(EquipmentSlot.CHEST), Operation.ADDITION));
            multimap.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIER, "Armor toughness", this.material.getToughness(), Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc(this.getDescriptionId().replace("item.", "")).withStyle(ChatFormatting.GRAY));
    }
}
