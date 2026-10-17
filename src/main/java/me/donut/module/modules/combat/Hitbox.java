package me.donut.module.modules.combat;

import me.donut.module.Category;
import me.donut.module.Module;
import me.donut.setting.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Hitbox — makes crosshair-based attacks easier by expanding each entity's
 * box when the crosshair ray is cast: we don't modify entity data (server-safe),
 * instead we pre-select the entity through an expanded raycast and let the
 * attack go through. Visually the vanilla hitbox lines are unchanged.
 */
public class Hitbox extends Module {
    private static Hitbox instance;
    private final DoubleSetting expand = new DoubleSetting("Expand", 0.2, 0.0, 2.0, 0.05);

    public Hitbox() {
        super("Hitbox", "Wider effective attack hitboxes", Category.COMBAT);
        addSettings(expand);
        instance = this;
    }

    /** Called by the mixin that replaces the crosshair target raycast. */
    public static double expansion() {
        if (instance == null || !instance.isEnabled()) return 0.0;
        return instance.expand.getValue();
    }

    /**
     * Finds the closest entity whose AABB expanded by 'expansion' intersects
     * the given ray, within 'maxDist'. Used by the crosshair mixin.
     */
    public static Entity raycastExpanded(Vec3d start, Vec3d end, double expansion) {
        var mc = mc();
        var world = mc.world;
        if (world == null) return null;
        Entity best = null;
        double bestT = Double.MAX_VALUE;
        Box ray = new Box(start, end);
        List<Entity> candidates = world.getOtherEntities(mc.player, ray.expand(expansion));
        for (Entity e : candidates) {
            if (!(e instanceof LivingEntity) || e == mc.player) continue;
            if (e.isInvisible() && !e.isSpectator()) { /* still hittable in SP */ }
            Box expanded = e.getBoundingBox().expand(expansion);
            var hit = expanded.raycast(start, end);
            if (hit.isPresent()) {
                double t = hit.get().squaredDistanceTo(start);
                if (t < bestT) {
                    bestT = t;
                    best = e;
                }
            }
        }
        return best;
    }

    @Override
    public void onTick() {
        var player = mc().player;
        if (player == null) return;
        // Keep mc.targetedEntity refreshed through the expanded raycast, so
        // vanilla's attackEntity path picks the widened target naturally.
        var cam = mc().gameRenderer.getCamera().getFocusedEntity() == null ? player : mc().gameRenderer.getCamera().getFocusedEntity();
        var rot = cam.getRotationVec(1.0f);
        double reach = 4.5;
        var end = cam.getEyePos().add(rot.multiply(reach));
        Entity target = raycastExpanded(cam.getEyePos(), end, expand.getValue());
        if (target != null && mc().targetedEntity == null) {
            mc().targetedEntity = target;
            mc().crosshairTarget = new EntityHitResult(target);
        }
    }
}
