package com.barribob.MaelstromMod.proxy;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.blocks.BlockLeavesBase;
import com.barribob.MaelstromMod.mana.IMana;
import com.barribob.MaelstromMod.mana.Mana;
import com.barribob.MaelstromMod.mana.ManaStorage;
import com.barribob.MaelstromMod.packets.*;
import com.barribob.MaelstromMod.util.Reference;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public void registerItemRenderer(Item item, int meta, String id) {
    }

    public void setFancyGraphics(BlockLeavesBase block, boolean isFancy) {
    }

    public void setCustomState(Block block, IStateMapper mapper) {
    }

    public void init() {
        // Register a network to communicate to the server for client stuff (e.g. client
        // raycast rendering for extended melee reach)
        Main.network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.NETWORK_CHANNEL_NAME);

        int packetId = 0;

        Main.network.registerMessage(MessageExtendedReachAttack.Handler.class, MessageExtendedReachAttack.class, packetId++, Dist.SERVER);
        Main.network.registerMessage(MessageMana.MessageHandler.class, MessageMana.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageLeap.MessageHandler.class, MessageLeap.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageManaUnlock.MessageHandler.class, MessageManaUnlock.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageDirectionForRender.Handler.class, MessageDirectionForRender.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageModParticles.MessageHandler.class, MessageModParticles.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageSyncConfig.Handler.class, MessageSyncConfig.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageBBAnimation.Handler.class, MessageBBAnimation.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageLoopAnimationUpdate.Handler.class, MessageLoopAnimationUpdate.class, packetId++, Dist.CLIENT);
        Main.network.registerMessage(MessageStartElytraFlying.Handler.class, MessageStartElytraFlying.class, packetId++, Dist.SERVER);
        Main.network.registerMessage(MessageEmptySwing.Handler.class, MessageEmptySwing.class, packetId++, Dist.SERVER);
        Main.network.registerMessage(MessagePlayDarkNexusWindSound.Handler.class, MessagePlayDarkNexusWindSound.class, packetId++, Dist.CLIENT);

        CapabilityManager.INSTANCE.register(IMana.class, new ManaStorage(), Mana.class);
        // CapabilityManager.INSTANCE.register(IInvasion.class, new InvasionStorage(),
        // Invasion.class);
    }
}
