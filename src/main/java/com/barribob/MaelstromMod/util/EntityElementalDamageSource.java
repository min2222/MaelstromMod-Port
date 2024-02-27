package com.barribob.MaelstromMod.util;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class EntityElementalDamageSource extends EntityDamageSource implements IElement {
    Element element;

    public EntityElementalDamageSource(String damageTypeIn, Entity damageSourceEntityIn, Element element) {
        super(damageTypeIn, damageSourceEntityIn);
        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }
}
