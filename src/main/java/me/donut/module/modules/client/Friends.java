package me.donut.module.modules.client;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.util.Msg;
import me.donut.util.SSP;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Friends — shared list of "friendly" players: AimAssist/Triggerbot skip them,
 * PlayerESP marks them green. Click the module in the GUI to add the player
 * you last hit; right-click logic can be extended later.
 */
public class Friends extends Module {
    public static final Set<String> FRIENDS = ConcurrentHashMap.newKeySet();

    public Friends() {
        super("Friends", "Manage friendly players (civic targeting)", Category.CLIENT);
    }

    /** True when a module should NOT target this player name. */
    public static boolean isFriend(String name) {
        return FRIENDS.contains(name.toLowerCase());
    }

    @Override
    public void onEnable() {
        var mc = mc();
        if (mc.player != null && mc.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult ehr
                && ehr.getEntity() instanceof net.minecraft.entity.player.PlayerEntity p) {
            String name = p.getName().getString().toLowerCase();
            if (FRIENDS.add(name)) Msg.info("Added friend: " + name);
            else Msg.info("Removed friend: " + name);
        } else if (mc.player != null) {
            // no target: toggle last-attacked target's friendship
            LivingTarget:
            {
                var t = TargetHUD_accessor.last();
                if (t instanceof net.minecraft.entity.player.PlayerEntity p) {
                    String name = p.getName().getString().toLowerCase();
                    if (FRIENDS.contains(name)) {
                        FRIENDS.remove(name);
                        Msg.info("Removed friend: " + name);
                    } else {
                        FRIENDS.add(name);
                        Msg.info("Added friend: " + name);
                    }
                } else {
                    Msg.warn("Look at a player to add/remove friend");
                }
            }
        }
        setEnabled(false);
    }

    /** avoids circular init: queries TargetHUD's last target lazily */
    private static final class TargetHUD_accessor {
        static net.minecraft.entity.LivingEntity last() {
            var m = me.donut.module.ModuleManager.getByName("Target HUD");
            return m instanceof me.donut.module.modules.render.TargetHUD th
                    ? me.donut.module.modules.render.TargetHUD.last() : null;
        }
    }

    public int count() {
        return FRIENDS.size();
    }
}
