package com.barribob.MaelstromMod.items.armor;

import java.util.List;
import java.util.UUID;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.init.ModCreativeTabs;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.IHasModel;
import com.barribob.MaelstromMod.util.Reference;
import com.barribob.MaelstromMod.util.handlers.LevelHandler;
import com.google.common.collect.Multimap;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * 
 * The base for armor for the mod
 * 
 * Defines the new maelstrom armor type
 * 
 * Allows for new armor models to be included
 * 
 * Also allows for textures independent of the armor material
 *
 */
public class ModArmorBase extends ItemArmor implements IHasModel
{
    private static final UUID[] ARMOR_MODIFIERS = new UUID[] { UUID.fromString("a3578781-e4a8-4d70-9d32-cd952aeae1df"),
	    UUID.fromString("e2d1f056-f539-48c7-b353-30d7a367ebd0"), UUID.fromString("db13047a-bb47-4621-a025-65ed22ce461a"),
	    UUID.fromString("abb5df20-361d-420a-8ec7-4bdba33378eb") };

    private float maelstrom_armor_factor;
    private static final int[] armor_fractions = {4, 7, 8, 5};
    private static final int armor_total = 24;
    private String textureName;
    private ModelBiped model;

    public ModArmorBase(String name, ArmorMaterial materialIn, int renderIndex, EntityEquipmentSlot equipmentSlotIn, float maelstrom_armor, String textureName)
    {
	super(materialIn, renderIndex, equipmentSlotIn);
	setUnlocalizedName(name);
	setRegistryName(name);
	setCreativeTab(ModCreativeTabs.ALL);
	this.maelstrom_armor_factor = maelstrom_armor - 1;
	this.textureName = textureName;
	this.model = null;

	ModItems.ITEMS.add(this);
    }
    
    public ModArmorBase(String name, ArmorMaterial materialIn, EntityEquipmentSlot equipmentSlotIn, float maelstrom_armor, String textureName, ModelBiped model)
    {
	this(name, materialIn, 1, equipmentSlotIn, maelstrom_armor, textureName);
	this.model = model;
    }

    /**
     * Get the calculated reduction in armor based on the armor factor
     */
    public float getMaelstromArmor(ItemStack stack)
    {
	float total_armor_reduction = 1 - LevelHandler.getArmorFromLevel(this.maelstrom_armor_factor);
	float armor_type_fraction = this.armor_fractions[this.armorType.getIndex()] / (float)armor_total;
	return total_armor_reduction * armor_type_fraction;
    }
    
    /**
     * Gets the armor bars for display
     * Directly calculates from the fraction of the maelstrom_armor_factor, which
     * represents the total armor bars of any given armor set
     */
    public float getMaelstromArmorBars()
    {
	float armor_type_fraction = this.armor_fractions[this.armorType.getIndex()] / (float)armor_total;
	return this.maelstrom_armor_factor * armor_type_fraction * 2;
    }

    @Override
    public void registerModels()
    {
	Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit
     * damage.
     */
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
	Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

	if (equipmentSlot == this.armorType)
	{
	    multimap.put("maelstrom_armor", new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Maelstrom Armor modifier", Math.round(this.getMaelstromArmorBars() * 100) / 100.0f, 0));
	}

	return multimap;
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
	tooltip.add(TextFormatting.GRAY + "Level " + TextFormatting.DARK_GREEN + (this.maelstrom_armor_factor + 1));
    }
    
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
	if(this.model != null)
	{
	    return Reference.MOD_ID + ":textures/models/armor/" + this.textureName;
	}
	
	// Basically the normal string that is generated by minecraft's default armor
	int layer = slot == EntityEquipmentSlot.LEGS ? 2 : 1;
	String t = type == null ? "" : String.format("_%s", type);
	return String.format("%s:textures/models/armor/%s_layer_%d%s.png", Reference.MOD_ID, this.textureName, layer, t);
    }
    
    /*
     * Sets up a custom armor model
     */
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default)
    {
        if(this.model != null && !itemStack.isEmpty() && itemStack.getItem() instanceof ModArmorBase)
        {
            model.bipedHead.showModel = (armorSlot == EntityEquipmentSlot.HEAD);
            model.bipedBody.showModel = (armorSlot == EntityEquipmentSlot.CHEST);
            model.bipedLeftArm.showModel = (armorSlot == EntityEquipmentSlot.CHEST);
            model.bipedRightArm.showModel = (armorSlot == EntityEquipmentSlot.CHEST);
            model.bipedLeftLeg.showModel = (armorSlot == EntityEquipmentSlot.LEGS || armorSlot == EntityEquipmentSlot.FEET);
            model.bipedRightLeg.showModel = (armorSlot == EntityEquipmentSlot.LEGS || armorSlot == EntityEquipmentSlot.FEET);
            
            model.isChild = _default.isChild;
            model.isRiding = _default.isRiding;
            model.isSneak = _default.isSneak;
            model.rightArmPose = _default.rightArmPose;
            model.leftArmPose = _default.leftArmPose;
            
            return model;
        }
        return null;
    }
}
