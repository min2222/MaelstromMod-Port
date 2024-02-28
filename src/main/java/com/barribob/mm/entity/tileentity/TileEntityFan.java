package com.barribob.mm.entity.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import com.barribob.mm.blocks.BlockFan;

public class TileEntityFan extends BlockEntity implements ITickable {
    @Override
    public void update() {
        Direction facing = BlockFan.getFacing(this.getBlockMetadata());
        boolean triggered = (this.getBlockMetadata() & 8) > 0;
        float maxDistance = 16;
        float distance = 0;

        // Take into consideration any blocks in front of the fan
        for (distance = 1; distance <= maxDistance; distance++) {
            BlockPos pos = this.getPos().add(new BlockPos(facing.getFrontOffsetX() * distance, facing.getFrontOffsetY() * distance, facing.getFrontOffsetZ() * distance));
            BlockState block = world.getBlockState(pos);
            if (block.isFullBlock() || block.isFullCube() || block.isBlockNormalCube() || block.isSideSolid(world, pos, facing.getOpposite())
                    || block.isSideSolid(world, pos, facing)) {
                break;
            }
        }

        double strength = facing.getFrontOffsetY() != 0 ? 0.5 : 0.3;
        if (triggered) {
            AABB box = new AABB(pos, pos.add(1, 1, 1)).expand(facing.getFrontOffsetX() * distance, facing.getFrontOffsetY() * distance,
                    facing.getFrontOffsetZ() * distance);
            List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, box);

            if (list != null) {
                for (Entity entity : list) {
                    Vec3 vel = new Vec3(facing.getDirectionVec()).scale(strength / Math.sqrt(entity.getDistanceSq(this.pos.add(0.5, 0.5, 0.5))));
                    vel.scale(1 / entity.getBoundingBox().getAverageEdgeLength()); // Take into consideration the entity's size
                    entity.addVelocity(vel.x, vel.y, vel.z);
                    entity.fallDistance = 0;
                }
            }
        }
    }
}
