package com.barribob.mm.proxy;

import com.barribob.mm.Main;
import com.barribob.mm.blocks.BlockLeavesBase;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.Mana;
import com.barribob.mm.mana.ManaStorage;
import com.barribob.mm.packets.MessageBBAnimation;
import com.barribob.mm.packets.MessageDirectionForRender;
import com.barribob.mm.packets.MessageEmptySwing;
import com.barribob.mm.packets.MessageExtendedReachAttack;
import com.barribob.mm.packets.MessageLeap;
import com.barribob.mm.packets.MessageLoopAnimationUpdate;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.packets.MessageManaUnlock;
import com.barribob.mm.packets.MessageModParticles;
import com.barribob.mm.packets.MessagePlayDarkNexusWindSound;
import com.barribob.mm.packets.MessageStartElytraFlying;
import com.barribob.mm.packets.MessageSyncConfig;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CommonProxy {

    public void setFancyGraphics(BlockLeavesBase block, boolean isFancy) {
    }

    public void init() {
        int packetId = 0;

        Main.NETWORK.registerMessage(MessageExtendedReachAttack.Handler.class, MessageExtendedReachAttack.class, packetId++, Dist.SERVER);
        Main.NETWORK.registerMessage(MessageMana.MessageHandler.class, MessageMana.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageLeap.MessageHandler.class, MessageLeap.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageManaUnlock.MessageHandler.class, MessageManaUnlock.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageDirectionForRender.Handler.class, MessageDirectionForRender.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageModParticles.MessageHandler.class, MessageModParticles.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageSyncConfig.Handler.class, MessageSyncConfig.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageBBAnimation.Handler.class, MessageBBAnimation.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageLoopAnimationUpdate.Handler.class, MessageLoopAnimationUpdate.class, packetId++, Dist.CLIENT);
        Main.NETWORK.registerMessage(MessageStartElytraFlying.Handler.class, MessageStartElytraFlying.class, packetId++, Dist.SERVER);
        Main.NETWORK.registerMessage(MessageEmptySwing.Handler.class, MessageEmptySwing.class, packetId++, Dist.SERVER);
        Main.NETWORK.registerMessage(MessagePlayDarkNexusWindSound.Handler.class, MessagePlayDarkNexusWindSound.class, packetId++, Dist.CLIENT);

        CapabilityManager.INSTANCE.register(IMana.class, new ManaStorage(), Mana.class);
        // CapabilityManager.INSTANCE.register(IInvasion.class, new InvasionStorage(),
        // Invasion.class);
    }
}
