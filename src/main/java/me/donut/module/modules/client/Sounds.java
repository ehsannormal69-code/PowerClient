package me.donut.module.modules.client;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.module.ModuleManager;
import me.donut.util.Msg;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

/**
 * Sounds — gives audio feedback: a vanilla click plays whenever any module
 * is toggled on or off, so you can hear what's live during fights.
 */
public class Sounds extends Module {
    private static boolean registeredHooks = false;

    public Sounds() {
        super("Sounds", "Play click sounds on module toggles", Category.CLIENT);
    }

    @Override
    protected void onEnable() {
        registeredHooks = true;
    }

    @Override
    protected void onDisable() {
        registeredHooks = false;
    }

    /** Called by ModuleManager whenever any module got toggled. */
    static void onAnyToggle(Module m, boolean nowEnabled) {
        if (!registeredHooks) return;
        var mc = me.donut.util.SSP.mc();
        if (mc == null || mc.player == null || m instanceof Sounds) return;
        SoundEvent snd = nowEnabled
                ? net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_HARP.value()
                : net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_BASS.value();
        mc.player.playSound(snd, 0.4f, nowEnabled ? 1.4f : 0.8f);
    }

    /** convenience accessor used by ModuleManager */
    public static void hook(Module m, boolean nowEnabled) {
        onAnyToggle(m, nowEnabled);
    }
}
