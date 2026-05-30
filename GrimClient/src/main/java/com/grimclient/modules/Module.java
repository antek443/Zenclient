package com.grimclient.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public abstract class Module {

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false;
    private int keybind = -1;
    private boolean keyWasDown = false;

    public enum Category {
        COMBAT("Combat",   "⚔"),
        MOVEMENT("Movement","⚡"),
        PLAYER("Player",   "👤"),
        VISUALS("Visuals", "✨"),
        CLIENT("Client",   "🖥");

        public final String label, icon;
        Category(String l, String i) { label=l; icon=i; }
    }

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onEnable()  {}
    public void onDisable() {}
    public void onTick()    {}
    public void onRenderHUD(DrawContext ctx, int x, int y) {}

    // Sprawdza keybind i toggleuje jeśli klawisz wciśnięty
    public void checkKeybind() {
        if (keybind == -1 || mc.currentScreen != null) return;
        long win = MinecraftClient.getInstance().getWindow().getHandle();
        boolean isDown = InputUtil.isKeyPressed(win, keybind);
        if (isDown && !keyWasDown) toggle();
        keyWasDown = isDown;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public String   getName()        { return name; }
    public String   getDescription() { return description; }
    public Category getCategory()    { return category; }
    public boolean  isEnabled()      { return enabled; }
    public int      getKeybind()     { return keybind; }
    public void     setKeybind(int k){ this.keybind = k; }

    public void setEnabled(boolean e) {
        boolean was = enabled; enabled = e;
        if (!was && e) onEnable();
        else if (was && !e) onDisable();
    }
}
