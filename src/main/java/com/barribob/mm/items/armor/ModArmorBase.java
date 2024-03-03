package com.barribob.mm.items.armor;

import java.util.List;
import java.util.UUID;

import com.barribob.mm.Main;
import com.barribob.mm.config.ModConfig;
import com.barribob.mm.init.ModCreativeTabs;
import com.barribob.mm.items.ILeveledItem;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;
import com.barribob.mm.util.handlers.LevelHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The base for armor for the mod
 * <p>
 * Defines the new maelstrom armor type
 * <p>
 * Allows for new armor models to be included
 * <p>
 * Also allows for textures independent of the armor material
 */
public class ModArmorBase extends ArmorItem implements ILeveledItem, IElement {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("a3578781-e4a8-4d70-9d32-cd952aeae1df"),
            UUID.fromString("e2d1f056-f539-48c7-b353-30d7a367ebd0"), UUID.fromString("db13047a-bb47-4621-a025-65ed22ce461a"),
            UUID.fromString("abb5df20-361d-420a-8ec7-4bdba33378eb")};

    private float level;
    private static final int[] armor_fractions = {4, 7, 8, 5};
    private static final int armor_total = 24;
    private String textureName;
    private Element element = Element.NONE;
    private String armorBonusDesc = "";

    public ModArmorBase(String name, ArmorMaterial materialIn, int renderIndex, EquipmentSlot equipmentSlotIn, float level, String textureName) {
        super(materialIn, equipmentSlotIn, new Item.Properties().tab(ModCreativeTabs.ITEMS));
        this.level = level;
        this.textureName = textureName;
    }

    public float getElementalArmor(Element element) {
        if (element.matchesElement(getElement())) {
            float fullArmorReduction = 1 - (1 / ModConfig.balance.elemental_factor);
            float armorFraction = ModArmorBase.armor_fractions[this.slot.getIndex()] / (float) armor_total;
            return fullArmorReduction * armorFraction;
        }
        return 0; // Does not add any reduction at all
    }

    /**
     * Get the calculated reduction in armor based on the armor factor
     */
    public float getMaelstromArmor(ItemStack stack) {
        float total_armor_reduction = 1 - LevelHandler.getArmorFromLevel(this.level);
        float armor_type_fraction = ModArmorBase.armor_fractions[this.slot.getIndex()] / (float) armor_total;
        return total_armor_reduction * armor_type_fraction;
    }

    /**
     * Gets the armor bars for display Directly calculates from the fraction of the
     * maelstrom_armor_factor, which represents the total armor bars of any given
     * armor set
     */
    public float getMaelstromArmorBars() {
        if(ModConfig.gui.disableMaelstromArmorItemTooltips) {
            return 0;
        }
        float armor_type_fraction = ModArmorBase.armor_fractions[this.slot.getIndex()] / (float) armor_total;
        return this.getLevel() * armor_type_fraction;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.<Attribute, AttributeModifier>create();

        if (equipmentSlot == this.slot) {
            multimap.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", this.damageReduceAmount, 0));

            // Override armor toughness to make is adjustable in game
            multimap.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", ModConfig.balance.armor_toughness, Operation.ADDITION));
            multimap.put("maelstrom_armor", new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Maelstrom Armor modifier", Math.round(this.getMaelstromArmorBars() * 10) / 10.0f, Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(!ModConfig.gui.disableMaelstromArmorItemTooltips) {
            tooltip.add(ModUtils.getDisplayLevel((this.getLevel())));
        }

        if (!element.equals(Element.NONE) && !ModConfig.gui.disableElementalVisuals) {
            tooltip.add(ModUtils.translateDesc("elemental_armor_desc", element.textColor + element.symbol + ChatFormatting.GRAY,
                    ModUtils.ROUND.format(100 * getElementalArmor(element)) + "%"));
        }
        if (!this.armorBonusDesc.isEmpty() && Main.itemsConfig.getBoolean("full_set_bonuses." + armorBonusDesc.replace("_full_set", ""))) {
            tooltip.add(ModUtils.translateDesc(this.armorBonusDesc));
        }
    }

    public ModArmorBase setArmorBonusDesc(String armorBonusDesc) {
        this.armorBonusDesc = armorBonusDesc;
        return this;
    }

    protected ModelBiped getCustomModel() {
        return null;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.getCustomModel() != null) {
            return Reference.MOD_ID + ":textures/models/armor/" + this.textureName;
        }

        // Basically the normal string that is generated by minecraft's default armor
        int layer = slot == EquipmentSlot.LEGS ? 2 : 1;
        String t = type == null ? "" : String.format("_%s", type);
        return String.format("%s:textures/models/armor/%s_layer_%d%s.png", Reference.MOD_ID, this.textureName, layer, t);
    }

    /*
     * Sets up a custom armor model
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public ModelBiped getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, ModelBiped _default) {
        if (this.getCustomModel() != null && !itemStack.isEmpty() && itemStack.getItem() instanceof ModArmorBase) {
            ModelBiped model = getCustomModel();
            model.bipedHead.showModel = (armorSlot == EquipmentSlot.HEAD);
            model.bipedBody.showModel = (armorSlot == EquipmentSlot.CHEST);
            model.bipedLeftArm.showModel = (armorSlot == EquipmentSlot.CHEST);
            model.bipedRightArm.showModel = (armorSlot == EquipmentSlot.CHEST);
            model.bipedLeftLeg.showModel = (armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.FEET);
            model.bipedRightLeg.showModel = (armorSlot == EquipmentSlot.LEGS || armorSlot == EquipmentSlot.FEET);

            model.isChild = _default.isChild;
            model.isRiding = _default.isRiding;
            model.isSneak = _default.isSneak;
            model.rightArmPose = _default.rightArmPose;
            model.leftArmPose = _default.leftArmPose;

            return model;
        }
        return null;
    }

    @Override
    public float getLevel() {
        return this.level;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public ModArmorBase setElement(Element element) {
        this.element = element;
        return this;
    }
}
