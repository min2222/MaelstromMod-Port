package com.barribob.mm.proxy;

import net.minecraft.world.level.block.Block;

import com.barribob.mm.blocks.BlockLeavesBase;
import com.barribob.mm.util.handlers.RenderHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.ModelLoader;

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
        if (!Minecraft.getInstance().getFramebuffer().isStencilEnabled()) {
            Minecraft.getInstance().getFramebuffer().enableStencil();
        }

        RenderHandler.registerEntityRenderers();
        super.init();
    }
}
