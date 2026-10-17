package me.donut.module.modules.render;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.HudCollected;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * SuspiciousESP — flags entities with non-standard custom names, invisible
 * armor stands (the classic map-maker camera tricks), and mobs that should
 * not naturally exist in the current dimension, so you can spot admin
 * spy-entities or debug traps.
 */
public class SuspiciousESP extends Module {
    private int cd = 0;

    public SuspiciousESP() {
        super("Suspicious ESP", "Spot admin spies, hidden armor stands, odd mobs", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (cd > 0) { cd--; return; }
        cd = 10;
        var mc = mc();
        if (mc.player == null || mc.world == null) return;

        List<String> found = new ArrayList<>();
        List<Entity> nearby = mc.world.getOtherEntities(mc.player,
                mc.player.getBoundingBox().expand(24));

        for (Entity e : nearby) {
            if (e instanceof ArmorStandEntity as && as.isInvisible()) {
                found.add("invisible armor stand @ " + e.getBlockPos().toShortString());
                continue;
            }
            if (e instanceof LivingEntity le) {
                // suspicious when it has a custom name "hidden" grey
                if (le.isCustomNameVisible() && le.getCustomName() != null) {
                    String n = le.getCustomName().getString();
                    if (n.equalsIgnoreCase("herobrine") || n.contains("spy")
                            || n.contains("watching")) {
                        found.add(n + " @ " + le.getBlockPos().toShortString());
                    }
                }
            }
            // mobs far from their biome: crude heuristic — nether mobs in overworld
            if (e instanceof MobEntity m) {
                String t = m.getType().getName().getString().toLowerCase();
                if (t.contains("blaze") || t.contains("ghast") || t.contains("wither")) {
                    if (mc.world.getRegistryKey() != net.minecraft.world.World.NETHER) {
                        found.add("Nether mob in " + mc.world.getRegistryKey().getValue().getPath()
                                + ": " + t + " @ " + m.getBlockPos().toShortString());
                    }
                }
            }
        }

        HudCollected.put("sus", found.isEmpty()
                ? "Nothing suspicious within 24 blocks"
                : String.join(" | ", found));
    }

    @Override
    protected void onDisable() {
        HudCollected.put("sus", null);
    }
}
