package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.animation.Animation;
import com.barribob.mm.entity.animation.AnimationNone;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IAnimatedMob;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;

import net.minecraft.entity.EntityFlying;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/*
 * Base class that serves as the flying version of EntityLeveledMob
 */
public abstract class EntityLeveledFlyingMob extends EntityFlying implements IMob, RangedAttackMob, IAnimatedMob, IElement {
    @OnlyIn(Dist.CLIENT)
    protected Animation currentAnimation;
    private float level;

    protected static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.<Integer>createKey(EntityLeveledMob.class, EntityDataSerializers.VARINT);

    public EntityLeveledFlyingMob(Level worldIn) {
        super(worldIn);
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (level.isClientSide && currentAnimation != null && this.getHealth() > 0) {
            currentAnimation.update();
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.level.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.setDead();
        }

        /**
         * Periodically check if the animations need to be reinitialized
         */
        if (this.tickCount % 20 == 0) {
            level.broadcastEntityEvent(this, animationByte);
        }
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        level.broadcastEntityEvent(this, animationByte);
        if (compound.hasKey("element")) {
            this.setElement(Element.getElementFromId(compound.getInteger("element")));
        }

        super.readFromNBT(compound);
    }

    public float getAttack() {
        return ModUtils.getMobDamage(this.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue(), 0.0, this.getMaxHealth(), this.getHealth(),
                this.level, this.getElement());
    }

    @Override
    public Animation getCurrentAnimation() {
        return this.currentAnimation == null ? new AnimationNone() : this.currentAnimation;
    }

    @Override
    protected float applyArmorCalculations(DamageSource source, float damage) {
        return super.applyArmorCalculations(source, ModUtils.getArmoredDamage(source, damage, getLevel(), getElement()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == animationByte && currentAnimation == null) {
            initAnimation();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ELEMENT, Integer.valueOf(Element.NONE.id));
    }

    @Override
    public Element getElement() {
        return this.dataManager == null ? Element.getElementFromId(Element.NONE.id) : Element.getElementFromId(this.dataManager.get(ELEMENT));
    }

    public void setElement(Element element) {
        this.dataManager.set(ELEMENT, element.id);
    }

    @Override
    public void writeEntityToNBT(CompoundTag compound) {
        compound.setInteger("element", getElement().id);
        super.writeEntityToNBT(compound);
    }
}
