package com.barribob.mm.entity.tileentity;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * The tile entity spawner logic for the disappearing spawner. Similar to the
 * vanilla spawner, except it sets itself to air
 */
public class DisappearingSpawnerLogic extends MobSpawnerLogic {
    public DisappearingSpawnerLogic(Supplier<Level> world, Supplier<BlockPos> pos, Block block) {
        super(world, pos, block);
    }

    /**
     * Returns true if there's a player close enough to this mob spawner to activate
     * it.
     */
    protected boolean isActivated() {
        BlockPos blockpos = this.pos.get();
        return isAnyPlayerWithinRangeAt(this.world.get(), blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D,
                this.activatingRangeFromPlayer);
    }

    /**
     * Checks to see if any players (in survival) are in range for spawning
     */
    private boolean isAnyPlayerWithinRangeAt(Level world, double x, double y, double z, double range) {
        for (int j2 = 0; j2 < world.players().size(); ++j2) {
            Player entityplayer = world.players().get(j2);

            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer) && !entityplayer.getAbilities().instabuild) {
                double d0 = entityplayer.distanceToSqr(x, y, z);

                if (range < 0.0D || d0 < range * range) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void updateSpawner() {
        // Currently does not deal with any server stuff, although this might be a
        // mistake, so potentially this may have to revert back to the vanilla logic
        if (this.world.get().isClientSide || !this.isActivated()) {
            return;
        }

        if (this.spawnDelay > 0) {
            --this.spawnDelay;
            return;
        }

        while (this.count < this.maxCount) {
            // Try multiple times to spawn the entity in a good spot
            int tries = 50;
            for (int t = 0; t < tries; t++) {
                if (this.tryToSpawnEntity()) {
                    break;
                } else if (t == tries - 1) {
                    this.count++;
                }
            }
        }

        this.onSpawn(world.get(), pos.get());
    }

    protected void onSpawn(Level world, BlockPos blockpos) {
        world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
    }
}