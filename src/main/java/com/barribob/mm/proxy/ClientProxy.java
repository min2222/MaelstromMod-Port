package com.barribob.mm.proxy;

import com.barribob.mm.blocks.BlockLeavesBase;
import com.barribob.mm.util.handlers.RenderHandler;

import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy {
    @Override
    public void setFancyGraphics(BlockLeavesBase block, boolean isFancy) {
        block.setFancyGraphics(isFancy);
    }

    /**
     * Initializations for client only stuff like rendering
     */
    @Override
    public void init() {
        if (!Minecraft.getInstance().getMainRenderTarget().isStencilEnabled()) {
            Minecraft.getInstance().getMainRenderTarget().enableStencil();
        }

        RenderHandler.registerEntityRenderers();
        super.init();
    }
}
