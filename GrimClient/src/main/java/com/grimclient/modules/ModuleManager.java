package com.grimclient.modules;

import com.grimclient.modules.combat.*;
import com.grimclient.modules.movement.*;
import com.grimclient.modules.player.*;
import com.grimclient.modules.visual.*;
import com.grimclient.modules.client.*;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public void initModules() {
        // COMBAT
        register(new Aura());
        register(new KillAura());
        register(new Criticals());
        register(new Criticals2());
        register(new AntiKnockback());
        register(new Reach());
        register(new AutoClicker());
        register(new FakeLag());
        register(new AutoWeb());
        register(new AutoTrap());
        register(new Velocity());
        register(new WTap());
        register(new AimAssist());
        register(new Backtrack());
        register(new TriggerBot());
        register(new AutoShield());
        register(new GhostHand());
        register(new AntiBot());

        // MOVEMENT
        register(new Sprint());
        register(new Fly());
        register(new Speed());
        register(new NoFall());
        register(new BunnyHop());
        register(new Step());
        register(new HighJump());
        register(new Scaffold());
        register(new LongJump());
        register(new NoSlowdown());
        register(new Phase());

        // PLAYER
        register(new AutoTotem());
        register(new FastPlace());
        register(new NoRotate());
        register(new AntiAFK());
        register(new AutoEat());
        register(new Namespoof());
        register(new FastBreak());
        register(new PotionRefill());
        register(new AutoArmor());
        register(new ChestStealer());
        register(new InvManager());

        // VISUALS
        register(new FullBright());
        register(new ESP());
        register(new Tracers());
        register(new ChestESP());
        register(new Xray());
        register(new NoWeather());
        register(new Zoom());
        register(new Glow());
        register(new LogoutSpots());
        register(new Ambience());
        register(new TargetHUD());
        register(new Nametags());
        register(new Breadcrumbs());

        // CLIENT
        register(new Disabler());
        register(new Timer());
        register(new PacketFly());
        register(new HUD());
        register(new AttributeSwap());
        register(new AntiCheat());
        register(new Blink());
        register(new AutoConfig());
    }

    public void register(Module m) { modules.add(m); }

    public void tickModules() {
        for (Module m : modules) m.checkKeybind();
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }

    public void renderHUD(DrawContext ctx) {
        int y = 2;
        for (Module m : getEnabled()) { m.onRenderHUD(ctx, 2, y); y += 10; }
    }

    public List<Module> getModules() { return modules; }

    public List<Module> getByCategory(Module.Category cat) {
        return modules.stream().filter(m -> m.getCategory() == cat).collect(Collectors.toList());
    }

    public List<Module> getEnabled() {
        return modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
    }

    public Module getByName(String name) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> search(String query) {
        if (query == null || query.isEmpty()) return new ArrayList<>();
        String q = query.toLowerCase();
        return modules.stream()
            .filter(m -> m.getName().toLowerCase().contains(q)
                      || m.getDescription().toLowerCase().contains(q)
                      || m.getCategory().label.toLowerCase().contains(q))
            .collect(Collectors.toList());
    }
}
