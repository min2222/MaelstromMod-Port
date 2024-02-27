package com.barribob.MaelstromMod.blocks.portal;

import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.teleporter.ToNexusTeleporter;
import com.barribob.MaelstromMod.util.teleporter.ToStructuralDimensionTeleporter;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Teleporter;
import net.minecraft.world.level.Level;

import java.util.List;

public class BlockCrimsonPortal extends BlockPortal {
    public BlockCrimsonPortal(String name) {
        super(name, ModConfig.world.crimson_kingdom_dimension_id, ModConfig.world.nexus_dimension_id);
        this.setBlockUnbreakable();
        this.setLightLevel(0.5f);
        this.setLightOpacity(0);
    }

    @Override
    protected Teleporter getEntranceTeleporter(Level world) {
        return new ToStructuralDimensionTeleporter(world.getMinecraftServer().getWorld(ModConfig.world.crimson_kingdom_dimension_id), new BlockPos(135, 151, 155), null);
    }

    @Override
    protected Teleporter getExitTeleporter(Level world) {
        return new ToNexusTeleporter(world.getMinecraftServer().getWorld(ModConfig.world.nexus_dimension_id), new BlockPos(69, 212, 163));
    }

    @Override
    public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced) {
        tooltip.add(ModUtils.translateDesc("nexus_only_portal"));
        super.addInformation(stack, player, tooltip, advanced);
    }
}