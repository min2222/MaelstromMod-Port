package com.barribob.mm.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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

    @Override
    public Entity getEntity()
    {
        if(super.getEntity() instanceof LivingEntity) {
            return super.getEntity();
        } else if (super.getDirectEntity() instanceof LivingEntity) {
            return super.getDirectEntity();
        }
        return null;
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity entity)
    {
        Component itextcomponent = this.getEntity() == null ? this.getDirectEntity().getDisplayName() : this.getEntity().getDisplayName();

        String s = "death.attack." + this.msgId;
        return Component.translatable(s, entity.getDisplayName(), itextcomponent);
    }
}
