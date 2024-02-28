package com.barribob.mm.entity.projectile;

import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

/**
 * The base projectile class for most projectiles in the mod
 * <p>
 * The other two constructors immediately cause the projectile to despawn (e.g. if the projectile is saved in midair, just despawn it)
 * <p>
 * This is intended for offensive projectiles, as there is damage included
 */
public class ModProjectile extends EntityModThrowable implements IElement {
    protected float travelRange;
    private final Vec3 startPos;
    protected static final byte IMPACT_PARTICLE_BYTE = 3;
    private static final byte PARTICLE_BYTE = 4;
    private float damage = 0;
    protected static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.<Integer>createKey(ModProjectile.class, EntityDataSerializers.VARINT);
    protected float maxAge = 20 * 20;
    private Item itemToRender = ModItems.INVISIBLE;

    public ModProjectile(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn);
        this.travelRange = 20.0f;
        this.setDamage(damage);
        this.startPos = new Vec3(this.posX, this.posY, this.posZ);
        if (throwerIn instanceof IElement) {
            this.setElement(((IElement) throwerIn).getElement());
        }
    }

    public ModProjectile(Level worldIn) {
        super(worldIn);
        this.startPos = new Vec3(this.posX, this.posY, this.posZ);
    }

    public ModProjectile(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.startPos = new Vec3(this.posX, this.posY, this.posZ);
    }

    protected double getDistanceTraveled() {
        return this.getDistance(startPos.x, startPos.y, startPos.z);
    }

    /**
     * Set how far the projectile will be from its shooting entity before despawning
     *
     * @param distance
     */
    public void setTravelRange(float distance) {
        this.travelRange = distance;
    }

    /**
     * Sets the damage for use by inherited projectiles
     */
    protected void setDamage(float damage) {
        this.damage = damage;
    }

    /*
     * For use of inhereted projectiles
     */
    protected float getDamage() {
        return this.damage;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.level.broadcastEntityEvent(this, this.PARTICLE_BYTE);

        // Despawn if a certain distance away from its origin
        if (this.shootingEntity != null && getDistanceTraveled() > this.travelRange) {
            this.level.broadcastEntityEvent(this, this.IMPACT_PARTICLE_BYTE);
            this.setDead();
        }

        // Despawn if older than a certain age
        if (this.tickCount > this.maxAge) {
            this.setDead();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level.isClientSide) {
            this.level.broadcastEntityEvent(this, this.IMPACT_PARTICLE_BYTE);
            this.setDead();
        }
    }

    /**
     * Handler for {@link Level#setEntityState} Connected through setEntityState to
     * spawn particles
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == this.IMPACT_PARTICLE_BYTE) {
            spawnImpactParticles();
        }
        if (id == this.PARTICLE_BYTE) {
            spawnParticles();
        }
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    protected void spawnParticles() {
    }

    /**
     * Called on impact to spawn impact particles
     */
    protected void spawnImpactParticles() {
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

    public ModProjectile setElement(Element element) {
        this.dataManager.set(ELEMENT, element.id);
        return this;
    }

    public Item getItemToRender() {
        return itemToRender;
    }

    public ModProjectile setItemToRender(Item itemToRender) {
        this.itemToRender = itemToRender;
        return this;
    }
}
