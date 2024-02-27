package com.barribob.MaelstromMod.items;

import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.Reference;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * A custom elytra. The actual functionality is spread across multiple event handlers, as Minecraft doesn't make a custom elytra an easy process.
 */
public class ItemModElytra extends ItemBase {
    private static final UUID ARMOR_MODIFIER = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");
    private final ItemArmor.ArmorMaterial material;

    public ItemModElytra(String name, ItemArmor.ArmorMaterial material) {
        super(name);
        this.maxStackSize = 1;
        this.material = material;
    }

    // Taken from {@code ItemElytra}
    @Override
    public InteractionResultHolder<ItemStack> onItemRightClick(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        EquipmentSlot entityequipmentslot = Mob.getSlotForItemStack(itemstack);
        ItemStack itemstack1 = playerIn.getItemStackFromSlot(entityequipmentslot);

        if (itemstack1.isEmpty()) {
            playerIn.setItemStackToSlot(entityequipmentslot, itemstack.copy());
            itemstack.setCount(0);
            return new InteractionResultHolder<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        } else {
            return new InteractionResultHolder<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }

    @Override
    public boolean isValidArmor(ItemStack stack, EquipmentSlot armorType, Entity entity) {
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
            return Reference.MOD_ID + ":textures/models/armor/" + this.getUnlocalizedName().replace("item.", "") + ".png";
        }
        return null;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EquipmentSlot.CHEST) {
            multimap.put(Attributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER, "Armor modifier", this.material.getDamageReductionAmount(EquipmentSlot.CHEST), 0));
            multimap.put(Attributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIER, "Armor toughness", this.material.getToughness(), 0));
        }

        return multimap;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc(this.getUnlocalizedName().replace("item.", "")));
    }
}
