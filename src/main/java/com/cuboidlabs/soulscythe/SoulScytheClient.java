package com.cuboidlabs.soulscythe;

import com.cuboidlabs.soulscythe.util.ClientSoulStormState;
import com.cuboidlabs.soulscythe.util.SoulStorm;
import com.cuboidlabs.soulscythe.util.SoulStormPayload;
import com.cuboidlabs.soulscythe.util.SoulStormState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.ladysnake.satin.api.managed.uniform.Uniform3f;
import org.lwjgl.glfw.GLFW;

public class SoulScytheClient implements ClientModInitializer {
    public static final String MOD_ID = "soulscythe";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final ManagedShaderEffect CUSTOM_SHADER = ShaderEffectManager.getInstance().manage(Identifier.of(MOD_ID, "shaders/post/soul_storm_shader.json"), effect -> {
        MinecraftClient mc = MinecraftClient.getInstance();
        effect.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)mc.getFramebuffer()).getStillDepthMap());
        LOGGER.info("Test shader got updated");
    });

    // render storm
    public static final Uniform1f minMagenta = CUSTOM_SHADER.findUniform1f("minMagenta");
    public static final Uniform1f maxMagenta = CUSTOM_SHADER.findUniform1f("maxMagenta");
    public static final Uniform3f grayTint = CUSTOM_SHADER.findUniform3f("GrayTint");
    public static final Uniform3f circleColor = CUSTOM_SHADER.findUniform3f("CircleColor");
    public static final Uniform3f circleCenter = CUSTOM_SHADER.findUniform3f("CircleCenter");
    public static final Uniform1f circleRadius = CUSTOM_SHADER.findUniform1f("CircleRadius");
    public static final Uniform1f nearRadius = CUSTOM_SHADER.findUniform1f("NearRadius");
    public static final Uniform1f farRadius = CUSTOM_SHADER.findUniform1f("FarRadius");
    public static final Uniform3f magentaColor = CUSTOM_SHADER.findUniform3f("MagentaColor");
    public static final Uniform3f outerColor = CUSTOM_SHADER.findUniform3f("OuterColor");
    public static final Uniform1f outlineStrength = CUSTOM_SHADER.findUniform1f("OutlineStrength");
    public static final Uniform1f outlineThick = CUSTOM_SHADER.findUniform1f("OutlineThickness");
    public static boolean renderStorm = false;
    public static float radius = 0f;
    private boolean wasRendered = false;

    //public static KeyBinding START_STORM_KEY;

    @Override
    public void onInitializeClient() {
        //START_STORM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.soulscythe.start_storm", GLFW.GLFW_KEY_X, "controls.keybinds"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (wasRendered != renderStorm) {
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 4.0f, 0.5f));
                wasRendered = renderStorm;
            }
            if (ClientSoulStormState.stormActive) {
                renderStorm = true;

                if (client.player == null) return;
                if (ClientSoulStormState.activatorPlayer == client.player.getUuid()) {
                    SoulScytheClient.maxMagenta.set(0.06f);
                    SoulScytheClient.minMagenta.set(0.002f);
                    SoulScytheClient.grayTint.set(0.8f, 0.8f, 1.0f);
                    SoulScytheClient.circleCenter.set((float) client.player.getX(), (float) client.player.getY(), (float) client.player.getZ());
                    SoulScytheClient.radius = 0f;
                    SoulScytheClient.circleColor.set(1.3f, 0.0f, 1.4f);
                    SoulScytheClient.nearRadius.set(32f);
                    SoulScytheClient.farRadius.set(64f);
                    SoulScytheClient.magentaColor.set(0.03f, 0.0f, 0.06f);
                    SoulScytheClient.outerColor.set(0.005f, 0.0f, 0.015f);
                    SoulScytheClient.outlineStrength.set(1.0f);
                    SoulScytheClient.outlineThick.set(1.0f);
                } else {
                    SoulScytheClient.maxMagenta.set(0.06f);
                    SoulScytheClient.minMagenta.set(0.002f);
                    SoulScytheClient.grayTint.set(0.4f, 0.4f, 0.6f);
                    SoulScytheClient.circleCenter.set((float) client.player.getX(), (float) client.player.getY(), (float) client.player.getZ());
                    SoulScytheClient.radius = 0f;
                    SoulScytheClient.circleColor.set(1.3f, 0.0f, 1.4f);
                    SoulScytheClient.nearRadius.set(18f);
                    SoulScytheClient.farRadius.set(32f);
                    SoulScytheClient.magentaColor.set(0.03f, 0.0f, 0.06f);
                    SoulScytheClient.outerColor.set(0.005f, 0.0f, 0.015f);
                    SoulScytheClient.outlineStrength.set(1.0f);
                    SoulScytheClient.outlineThick.set(1.0f);
                }
            } else {
                renderStorm = false;
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(
                SoulStormPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        ClientSoulStormState.stormActive = payload.active();
                        ClientSoulStormState.stormIntensity = payload.intensity();
                        ClientSoulStormState.activatorPlayer = payload.activatorPlayer();

                        System.out.println("Storm sync received: active=" + payload.active() +
                                ", intensity=" + payload.intensity() +
                                ", activator=" + payload.activatorPlayer());
                    });
                }
        );
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            radius += 0.1f;
            if (radius > 10) radius = 10;
            circleRadius.set(radius);
            //System.out.println("Rendering blit val: "+renderStorm);
            MinecraftClient mc = MinecraftClient.getInstance();
            CUSTOM_SHADER.findUniformMat4("InverseProjectionMatrix").set(mc.gameRenderer.getBasicProjectionMatrix(mc.options.getFov().getValue()).invert());
            if (renderStorm) {
                //System.out.println("RENDERING SHADERS");
                //System.out.println("Custom shader loaded (1): " + CUSTOM_SHADER.isInitialized());
                CUSTOM_SHADER.render(tickDelta);
                //System.out.println("RENDERED SHADERS");
                //System.out.println("Custom shader loaded (2): " + CUSTOM_SHADER.isInitialized());
                //System.out.println("Custom shader errored (1): " + CUSTOM_SHADER.isErrored());
                //testShader.render(tickDelta);
            }
        });
    }
}
