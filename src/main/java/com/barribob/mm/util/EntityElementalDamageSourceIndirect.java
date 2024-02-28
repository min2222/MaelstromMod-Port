package com.barribob.mm.util;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityElementalDamageSourceIndirect extends IndirectEntityDamageSource implements IElement, IShieldArmorDamageSource {
    Element element;
    boolean stoppedByArmor;
    boolean disablesShields;

    public EntityElementalDamageSourceIndirect(String damageTypeIn, Entity source, Entity indirectEntityIn, Element element) {
        super(damageTypeIn, source, indirectEntityIn);
        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public EntityElementalDamageSourceIndirect setStoppedByArmor(boolean stoppedByArmor) {
        this.stoppedByArmor = stoppedByArmor;
        return this;
    }

    @Override
    public boolean getStoppedByArmor() {
        return stoppedByArmor;
    }

    public void setDisablesShields(boolean disablesShields) {
        this.disablesShields = disablesShields;
    }

    public boolean getDisablesShields() {
        return disablesShields;
    }

    public Entity getTrueSource()
    {
        if(super.getTrueSource() instanceof LivingEntity) {
            return super.getTrueSource();
        } else if (super.getImmediateSource() instanceof LivingEntity) {
            return super.getImmediateSource();
        }
        return null;
    }

    @Override
    public ITextComponent getDeathMessage(LivingEntity entity)
    {
        ITextComponent itextcomponent = this.getTrueSource() == null ? this.damageSourceEntity.getDisplayName() : this.getTrueSource().getDisplayName();

        String s = "death.attack." + this.damageType;
        return new TextComponentTranslation(s, entity.getDisplayName(), itextcomponent);
    }
}
