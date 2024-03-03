package com.barribob.mm.sounds;

import com.barribob.mm.init.ModDimensions;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DarkNexusWindSound extends AbstractTickableSoundInstance {
    private final LocalPlayer player;
    private int time;

    public DarkNexusWindSound(LocalPlayer player) {
        super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.1F;
    }

    @Override
    public void tick() {
        ++this.time;

        if (!this.player.isDeadOrDying() && (this.time <= 20 || this.player.level.dimension() == ModDimensions.DARK_NEXUS_KEY)) {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
            float velocity = (float) Math.sqrt(this.player.getDeltaMovement().x * this.player.getDeltaMovement().x + this.player.getDeltaMovement().z * this.player.getDeltaMovement().z + this.player.getDeltaMovement().y * this.player.getDeltaMovement().y);
            float f1 = velocity / 2.0F;

            this.volume = 0.1f + Mth.clamp(f1 * f1, 0.0F, 1.0F);

            if (this.time < 20) {
                this.volume = 0.0F;
            } else if (this.time < 40) {
                this.volume = (float) (this.volume * ((this.time - 20) / 20.0D));
            }

            if (this.volume > 0.8F) {
                this.pitch = 1.0F + (this.volume - 0.8F);
            } else {
                this.pitch = 1.0F;
            }
        } else {
            this.stop();
        }
    }
}