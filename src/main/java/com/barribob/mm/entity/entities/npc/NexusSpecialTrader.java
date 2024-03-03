package com.barribob.mm.entity.entities.npc;

import java.util.List;

import com.barribob.mm.entity.entities.EntityTrader;
import com.barribob.mm.init.ModProfessions;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NexusSpecialTrader extends EntityTrader {
    private byte particles = 4;

    public NexusSpecialTrader(Level worldIn) {
        super(worldIn);
        this.setImmovable(true);
        this.setSize(0.8f, 3.0f);
        this.setNoGravity(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(9, new EntityAIWatchClosest2(this, Player.class, 5.0F, 1.0F));
    }

    @Override
    public void tick() {
        super.tick();
        level.broadcastEntityEvent(this, particles);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == particles) {
            // Yellow particles
            ParticleManager.spawnEffect(level, ModRandom.randVec().add(ModUtils.entityPos(this).add(new Vec3(0, 2, 0))), new Vec3(0.9, 0.9, 0.5));
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected List<ITradeList> getTrades() {
        return ModProfessions.NEXUS_SPECIAL_TRADER.getTrades(0);
    }

    @Override
    protected String getVillagerName() {
        return "Nexus Saiyan";
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
    }
}
