package me.donut.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Bridges the client to the integrated (singleplayer) server.
 * Everything here runs on the server thread and is valid only in singleplayer.
 */
public final class SSP {
    private SSP() {}

    public static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    /** The integrated server, or null if not in a singleplayer world. */
    public static IntegratedServer server() {
        MinecraftClient mc = mc();
        if (mc == null || !mc.isIntegratedServerRunning()) return null;
        return mc.getServer();
    }

    /** True when a singleplayer world is live. */
    public static boolean available() {
        return server() != null;
    }

    /** The server world matching the dimension the client player currently is in. */
    public static ServerWorld playerWorld() {
        IntegratedServer s = server();
        ServerPlayerEntity p = player();
        if (s == null || p == null || mc().world == null) return null;
        return s.getWorld(mc().world.getRegistryKey());
    }

    /** The server-side copy of the player, or null. */
    public static ServerPlayerEntity player() {
        IntegratedServer s = server();
        if (s == null || mc().player == null) return null;
        return s.getPlayerManager().getPlayer(mc().player.getUuid());
    }

    /** Run a task on the server thread. If not in singleplayer this does nothing. */
    public static void onServer(Runnable r) {
        IntegratedServer s = server();
        if (s != null) s.execute(r);
    }

    /** The block entity at {@code pos}, looked up from the server world. */
    public static BlockEntity beAt(BlockPos pos) {
        ServerWorld w = playerWorld();
        return w == null ? null : w.getBlockEntity(pos);
    }

    /**
     * Returns block entities in already-loaded chunks intersecting {@code box}.
     * This deliberately does not load chunks and must be called on the server thread.
     */
    public static List<BlockEntity> loadedBlockEntities(ServerWorld world, Box box) {
        List<BlockEntity> result = new ArrayList<>();
        int minChunkX = ((int) Math.floor(box.minX)) >> 4;
        int maxChunkX = ((int) Math.floor(box.maxX)) >> 4;
        int minChunkZ = ((int) Math.floor(box.minZ)) >> 4;
        int maxChunkZ = ((int) Math.floor(box.maxZ)) >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                WorldChunk chunk = world.getChunkManager().getWorldChunk(chunkX, chunkZ, false);
                if (chunk == null) continue;
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (box.contains(blockEntity.getPos().getX() + 0.5, blockEntity.getPos().getY() + 0.5, blockEntity.getPos().getZ() + 0.5)) {
                        result.add(blockEntity);
                    }
                }
            }
        }
        return result;
    }

    /** Distance between client player and an entity, in blocks. */
    public static double dist(Entity e) {
        var p = mc().player;
        if (p == null || e == null) return Double.MAX_VALUE;
        return p.getEyePos().distanceTo(e.getEntityPos());
    }

    public static String worldName() {
        ServerWorld w = playerWorld();
        return w == null ? "?" : w.getRegistryKey().getValue().getPath();
    }
}
