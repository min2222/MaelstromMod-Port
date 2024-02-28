package com.barribob.mm.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.IRenderHandler;

public class AzureSkyRenderHandler extends IRenderHandler {
    /**
     * The class that FML accepts to render the sky
     */
    @Override
    public void render(float partialTicks, ClientLevel world, Minecraft mc) {
        AzureSkyRenderer renderer = new AzureSkyRenderer(mc, world);
        renderer.renderSky(partialTicks, 2);
    }
}
