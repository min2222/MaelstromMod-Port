package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.tileentity.TileEntityTeleporter;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.teleporter.NexusToOverworldTeleporter;
import com.barribob.mm.world.gen.WorldGenStructure;
import com.barribob.mm.world.gen.nexus.WorldGenNexusTeleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Rotation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class EntityWhiteMonolith extends EntityLeveledMob {
    public static final int DEATH_TIME = 600;

    public EntityWhiteMonolith(Level worldIn) {
        super(worldIn);
        this.setImmovable(true);
        this.setNoGravity(true);
        this.setSize(2.2f, 4.5f);
        this.setLevel(1);
        this.isImmuneToFire = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(300);
        this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.setRotation(0, 0);
        this.setRotationYawHead(0);

        if (level.isClientSide) {
            return;
        }

        if (rand.nextInt(6) == 0) {
            level.broadcastEntityEvent(this, ModUtils.SECOND_PARTICLE_BYTE);
        }

        if (this.tickCount > DEATH_TIME - 40) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
            world.playSound(this.posX, NexusToOverworldTeleporter.yPortalOffset, this.posZ, SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 5.0f, 1.0f, false);
        }

        if (this.tickCount > DEATH_TIME) {
            // Add the portal with a teleporter that teleports down to (about) the position of this entity
            WorldGenStructure portal = new WorldGenStructure("nexus/nexus_portal") {
                @Override
                protected void handleDataMarker(String function, BlockPos pos, Level worldIn, java.util.Random rand) {
                    if (function.startsWith("teleporter")) {
                        world.setBlockState(pos, ModBlocks.NEXUS_TELEPORTER.getDefaultState());
                        BlockEntity tileentity = world.getTileEntity(pos);
                        if (tileentity instanceof TileEntityTeleporter) {
                            ((TileEntityTeleporter) tileentity).setRelTeleportPos(new Vec3(new BlockPos(EntityWhiteMonolith.this).west(3).down().subtract(pos)));
                        }
                    }
                }

                ;
            };
            BlockPos size = portal.getSize(world);
            BlockPos pos = new BlockPos(this.posX, NexusToOverworldTeleporter.yPortalOffset - 2, this.posZ).subtract(new BlockPos(size.getX() * 0.5f, 0, size.getZ() * 0.5f));
            portal.generateStructure(world, pos, Rotation.NONE);

            // The teleport location into the sky
            BlockPos teleportToPos = pos.add(new BlockPos(3, 2, 3));
            BlockPos relativeTeleport = teleportToPos.subtract(new BlockPos(this).down());
            new WorldGenNexusTeleporter(new Vec3(relativeTeleport)).generate(world, rand, new BlockPos(this).add(new BlockPos(0, -1, -1)), Rotation.NONE);

            this.setDead();
        }

        BlockPos random = new BlockPos(ModRandom.randVec().scale(10).add(position()));
        if (world.getBlockState(random).getBlock().equals(ModBlocks.MAELSTROM_BRICKS) || world.getBlockState(random).getBlock().equals(Blocks.OBSIDIAN)) {
            world.setBlockState(random, Blocks.QUARTZ_BLOCK.getDefaultState());
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            ModUtils.performNTimes(5, (i) -> {
                this.world.spawnParticle(ParticleTypes.EXPLOSION_LARGE, this.posX + ModRandom.getFloat(5),
                        NexusToOverworldTeleporter.yPortalOffset + ModRandom.getFloat(5), this.posZ + ModRandom.getFloat(5), 0, 0, 0);
            });
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            ParticleManager.spawnFirework(world, position().add(ModRandom.randVec().scale(2)).add(ModUtils.yVec(2)), ModColors.WHITE, new Vec3(0, 2.0, 0));
            ParticleManager.spawnFirework(world, position().add(ModRandom.randVec().scale(2)).add(ModUtils.yVec(2)), ModColors.YELLOW, new Vec3(0, 2.0, 0));
            Vec3 pos = ModRandom.randVec().scale(2).add(ModUtils.yVec(2));
            world.spawnParticle(ParticleTypes.ENCHANTMENT_TABLE, pos.x, pos.y, pos.z, 0, 2.0f, 0);
        }
        super.handleStatusUpdate(id);
    }

    // Cannot be attacked
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
