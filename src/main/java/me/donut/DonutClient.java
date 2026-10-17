package me.donut;

import me.donut.gui.ClickGuiScreen;
import me.donut.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class DonutClient implements ClientModInitializer {
    public static final String MOD_ID = "donut-client";
    private static DonutClient instance;

    private KeyBinding openGuiKey;

    public static DonutClient get() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        ModuleManager.init();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.donut-client.opengui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of(MOD_ID, "donut"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(new ClickGuiScreen());
            }
            ModuleManager.onClientTick(client);
        });
    }
}
