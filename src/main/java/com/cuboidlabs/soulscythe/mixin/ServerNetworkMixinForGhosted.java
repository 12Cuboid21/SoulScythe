package com.cuboidlabs.soulscythe.mixin;

import com.cuboidlabs.soulscythe.effect.ModEffects;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerNetworkMixinForGhosted {
    @Shadow public ServerPlayerEntity player;

    @Inject(
            method = "onChatMessage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        ServerPlayerEntity player = this.player;
        if (player.hasStatusEffect(ModEffects.GHOST_EFFECT)) {
            player.sendMessage(Text.empty().append("Ghosts can't speak").formatted(Formatting.RED));
            ci.cancel();
        }
    }
}
