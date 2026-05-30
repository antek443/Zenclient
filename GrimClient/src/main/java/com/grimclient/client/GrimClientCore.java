package com.grimclient.client;

import com.grimclient.gui.GrimGUI;
import com.grimclient.modules.ModuleManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GrimClientCore {

    public static GrimClientCore INSTANCE;
    public ModuleManager moduleManager;
    public static KeyBinding KEY_GUI;

    public void init() {
        INSTANCE = this;
        moduleManager = new ModuleManager();
        moduleManager.initModules();

        KEY_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.grimclient.gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.categories.grimclient"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KEY_GUI.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new GrimGUI());
                } else if (client.currentScreen instanceof GrimGUI) {
                    client.setScreen(null);
                }
            }
            if (client.player != null) {
                moduleManager.tickModules();
            }
        });

        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen == null && mc.player != null) {
                moduleManager.renderHUD(ctx);
            }
        });
    }
}
