package me.donut.module.modules.render;

import me.donut.DonutClient;
import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.module.ModuleManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {
    private static boolean registered = false;

    public HUD() {
        super("HUD", "Draw watermark and active module list", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        if (!registered) {
            registered = true;
            HudRenderCallback.EVENT.register((ctx, delta) -> {
                if (!this.isEnabled()) return;
                renderHud(ctx);
            });
        }
    }

    private void renderHud(DrawContext ctx) {
        var tr = mc().textRenderer;
        // Watermark like "cracked by starry muah"
        ctx.drawTextWithShadow(tr, "\u00A7dDonut Client \u00A77v1.0.0", 4, 4, 0xFFFFFF);
        int y = 16;
        for (Module m : enabledList()) {
            ctx.drawTextWithShadow(tr, m.getName().toUpperCase(), 4, y, 0xC8C8C8);
            y += 12;
        }
    }

    private List<Module> enabledList() {
        List<Module> list = new ArrayList<>();
        for (Module m : ModuleManager.getAll()) {
            if (m.isEnabled() && !(m instanceof HUD)) list.add(m);
        }
        return list;
    }
}
