package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.entity.entities.EntityLeveledMob;
import com.barribob.MaelstromMod.entity.entities.EntityMaelstromBeast;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ProjectileBoneQuake extends ProjectileBeastQuake {

    public ProjectileBoneQuake(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage);
    }

    public ProjectileBoneQuake(Level worldIn) {
        super(worldIn);
    }

    public ProjectileBoneQuake(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public void onUpdate() {
        if (this.shootingEntity instanceof EntityLeveledMob) {
            EntityMaelstromBeast.spawnBone(world, this.getPositionVector(), (EntityLeveledMob) this.shootingEntity);
        }
        super.onUpdate();
    }
}
