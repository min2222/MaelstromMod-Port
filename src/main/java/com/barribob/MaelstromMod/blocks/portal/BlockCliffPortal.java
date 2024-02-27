package com.barribob.MaelstromMod.blocks.portal;

import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.init.ModBlocks;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.teleporter.DimensionalTeleporter;
import com.barribob.MaelstromMod.util.teleporter.ToNexusTeleporter;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * The portal block for the azure dimension
 */
public class BlockCliffPortal extends BlockPortal {
    public BlockCliffPortal(String name) {
        super(name, ModConfig.world.cliff_dimension_id, ModConfig.world.nexus_dimension_id);
        this.setBlockUnbreakable();
        this.setLightLevel(0.5f);
        this.setLightOpacity(0);
    }

    @Override
    protected Teleporter getEntranceTeleporter(Level world) {
        return new DimensionalTeleporter(world.getMinecraftServer().getWorld(ModConfig.world.cliff_dimension_id), ModBlocks.CHISELED_CLIFF_STONE, ModBlocks.CLIFF_PORTAL);
    }

    @Override
    protected Teleporter getExitTeleporter(Level world) {
        return new ToNexusTeleporter(world.getMinecraftServer().getWorld(ModConfig.world.nexus_dimension_id), new BlockPos(82, 129, 171));
    }

    @Override
    public void addInformation(ItemStack stack, Level player, List<String> tooltip, TooltipFlag advanced) {
        tooltip.add(ModUtils.translateDesc("nexus_only_portal"));
        super.addInformation(stack, player, tooltip, advanced);
    }
}