package com.barribob.mm.entity.action;

import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.entity.entities.EntityHerobrineOne;
import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;

/*
 * Deals damage in an area around the entity
 */
public class ActionSpinSlash implements IAction {
    private float size = 2.2f;

    public ActionSpinSlash() {
    }

    public ActionSpinSlash(float size) {
        this.size = size;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.MOB)
                .directEntity(actor)
                .element(actor.getElement())
                .disablesShields()
                .stoppedByArmorNotShields().build();
        
        ModUtils.handleAreaImpact(size, (e) -> actor.getAttack() * actor.getConfigFloat("spin_damage"), actor, actor.position(), source, 0.3f, actor.getElement().matchesElement(Element.CRIMSON) ? 3 : 0, false);

        actor.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.0F / (actor.getRandom().nextFloat() * 0.4F + 0.8F));

        actor.level.broadcastEntityEvent(actor, EntityHerobrineOne.slashParticleByte);
    }
}
