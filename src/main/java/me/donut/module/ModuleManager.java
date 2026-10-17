package me.donut.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    public static void init() {
        // === CRYSTAL (8) ===
        register(new me.donut.module.modules.crystal.SafeAnchor());
        register(new me.donut.module.modules.crystal.AnchorMacro());
        register(new me.donut.module.modules.crystal.AutoCrystal());
        register(new me.donut.module.modules.crystal.AutoHitCrystal());
        register(new me.donut.module.modules.crystal.AutoInvTotem());
        register(new me.donut.module.modules.crystal.AutoTotem());
        register(new me.donut.module.modules.crystal.DoubleAnchor());
        register(new me.donut.module.modules.crystal.HoverTotem());

        // === COMBAT (7) ===
        register(new me.donut.module.modules.combat.ElytraSwap());
        register(new me.donut.module.modules.combat.Hitbox());
        register(new me.donut.module.modules.combat.MaceSwap());
        register(new me.donut.module.modules.combat.StaticHitboxes());
        register(new me.donut.module.modules.combat.AutoJumpReset());
        register(new me.donut.module.modules.combat.AimAssist());
        register(new me.donut.module.modules.combat.Triggerbot());

        // === MISC (15) ===
        register(new me.donut.module.modules.misc.AutoEat());
        register(new me.donut.module.modules.misc.AutoFirework());
        register(new me.donut.module.modules.misc.AutoMine());
        register(new me.donut.module.modules.misc.AutoTPA());
        register(new me.donut.module.modules.misc.AutoTool());
        register(new me.donut.module.modules.misc.CordSnapper());
        register(new me.donut.module.modules.misc.ElytraGlide());
        register(new me.donut.module.modules.misc.FastPlace());
        register(new me.donut.module.modules.misc.Freecam());
        register(new me.donut.module.modules.misc.KeyPearl());
        register(new me.donut.module.modules.misc.NameProtect());
        register(new me.donut.module.modules.misc.WeatherNotifier());
        register(new me.donut.module.modules.misc.TridentBoost());
        register(new me.donut.module.modules.misc.PearlBoost());
        register(new me.donut.module.modules.misc.QuickMacro());

        // === DONUT (16) ===
        register(new me.donut.module.modules.donut.FakeElytra());
        register(new me.donut.module.modules.donut.AntiTrap());
        register(new me.donut.module.modules.donut.AuctionSniper());
        register(new me.donut.module.modules.donut.ChunkFinder());
        register(new me.donut.module.modules.donut.AutoSell());
        register(new me.donut.module.modules.donut.AutoSpawnerSell());
        register(new me.donut.module.modules.donut.BoneDropper());
        register(new me.donut.module.modules.donut.NetheriteFinder());
        register(new me.donut.module.modules.donut.RTPBaseFinder());
        register(new me.donut.module.modules.donut.BoneOrder());
        register(new me.donut.module.modules.donut.SUSChunkFinder());
        register(new me.donut.module.modules.donut.SpawnerFinder());
        register(new me.donut.module.modules.donut.RTPEndBaseFinder());
        register(new me.donut.module.modules.donut.ShulkerDropper());
        register(new me.donut.module.modules.donut.TunnelBaseFinder());
        register(new me.donut.module.modules.donut.SpawnerProtection());

        // === RENDER (15) ===
        register(new me.donut.module.modules.render.Animations());
        register(new me.donut.module.modules.render.Fullbright());
        register(new me.donut.module.modules.render.HUD());
        register(new me.donut.module.modules.render.KelpESP());
        register(new me.donut.module.modules.render.NoRender());
        register(new me.donut.module.modules.render.PlayerESP());
        register(new me.donut.module.modules.render.StorageESP());
        register(new me.donut.module.modules.render.SwingSpeed());
        register(new me.donut.module.modules.render.TargetHUD());
        register(new me.donut.module.modules.render.TridentESP());
        register(new me.donut.module.modules.render.FakeStats());
        register(new me.donut.module.modules.render.BlockESP());
        register(new me.donut.module.modules.render.LightDebug());
        register(new me.donut.module.modules.render.SuspiciousESP());
        register(new me.donut.module.modules.render.PlayerChunkFinder());

        // === CLIENT (8) ===
        register(new me.donut.module.modules.client.ConfigDebug());
        register(new me.donut.module.modules.client.Meilar());
        register(new me.donut.module.modules.client.SelfDestruct());
        register(new me.donut.module.modules.client.Friends());
        register(new me.donut.module.modules.client.Sounds());
        register(new me.donut.module.modules.client.BlockEntityDebug());
        register(new me.donut.module.modules.client.SpotifyHUD());
        register(new me.donut.module.modules.client.ClickGUI());

        // === MOVEMENT (2) ===
        register(new me.donut.module.modules.movement.AutoSprint());
        register(new me.donut.module.modules.movement.FakeLag());
    }

    public static void register(Module module) {
        MODULES.add(module);
    }

    public static List<Module> getAll() {
        return new ArrayList<>(MODULES);
    }

    public static List<Module> getByCategory(Category cat) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) {
            if (m.getCategory() == cat) out.add(m);
        }
        return out;
    }

    public static Module getByName(String name) {
        for (Module m : MODULES) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public static void onClientTick(net.minecraft.client.MinecraftClient client) {
        for (Module m : MODULES) {
            if (m.isEnabled()) m.onTick();
        }
    }
}
