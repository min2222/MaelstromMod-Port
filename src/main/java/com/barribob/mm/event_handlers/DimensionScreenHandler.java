package com.barribob.mm.event_handlers;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.gui.GuiModDownloadTerrain;
import com.barribob.mm.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class DimensionScreenHandler {
    /**
     * Adds a new loading screen (credit to twilight forest mod for this idea)
     * https://github.com/TeamTwilight/twilightforest/blob/1.12.x/src/main/java/twilightforest/client/LoadingScreenListener.java
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onGuiOpenEvent(GuiOpenEvent event) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        if (event.getGui() instanceof GuiDownloadTerrain && mc.player != null) {
            if (mc.player.dimension == ModConfig.world.fracture_dimension_id) {
                event.setGui(new GuiModDownloadTerrain(new ResourceLocation(Reference.MOD_ID + ":textures/gui/dark_azure_stone.png"), "azure_dimension"));
            } else if (mc.player.dimension == ModConfig.world.nexus_dimension_id) {
                event.setGui(new GuiModDownloadTerrain(new ResourceLocation("minecraft:textures/blocks/quartz_block_chiseled.png"), "nexus_dimension"));
            } else if (mc.player.dimension == ModConfig.world.cliff_dimension_id) {
                event.setGui(new GuiModDownloadTerrain(new ResourceLocation(Reference.MOD_ID + ":textures/gui/chiseled_cliff_stone.png"), "cliff_dimension"));
            } else if (mc.player.dimension == ModConfig.world.crimson_kingdom_dimension_id) {
                event.setGui(new GuiModDownloadTerrain(new ResourceLocation(Reference.MOD_ID + ":textures/blocks/redstone_brick.png"), "crimson_dimension"));
            }
        }
    }
}
