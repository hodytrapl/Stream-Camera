package org.hodytrapl.stream_camera.key;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.hodytrapl.stream_camera.camera.CameraPointManager;
import org.hodytrapl.stream_camera.camera.LifetimeManager;
import org.hodytrapl.stream_camera.config.ModSettings;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class KeyInputHandler {
    // Определение клавиш а то задолбался команды вводить
    public static final KeyMapping KEY_F6 = new KeyMapping(
            "key.stream_camera.f6",
            GLFW.GLFW_KEY_F6,
            "key.categories.stream_camera"
    );

    public static final KeyMapping KEY_F7 = new KeyMapping(
            "key.stream_camera.f7",
            GLFW.GLFW_KEY_F7,
            "key.categories.stream_camera"
    );

    public static final KeyMapping KEY_F8 = new KeyMapping(
            "key.stream_camera.f8",
            GLFW.GLFW_KEY_F8,
            "key.categories.stream_camera"
    );

    public static final KeyMapping KEY_UP = new KeyMapping(
            "key.stream_camera.up",
            GLFW.GLFW_KEY_UP,
            "key.categories.stream_camera"
    );

    public static final KeyMapping KEY_DOWN = new KeyMapping(
            "key.stream_camera.down",
            GLFW.GLFW_KEY_DOWN,
            "key.categories.stream_camera"
    );

    // Регистрация клавиш (вызывается на модовой шине)
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY_F6);
        event.register(KEY_F7);
        event.register(KEY_F8);
        event.register(KEY_UP);
        event.register(KEY_DOWN);
    }

    // Обработка нажатий (вызывается на игровой шине)
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        // ВАЖНО: вызов цикла телепортации
        LifetimeManager.tick();

        if (KEY_F6.consumeClick()) {
            LifetimeManager.toggle();
        }
        if (KEY_F7.consumeClick()) {
            CameraPointManager.saveCurrentPoint(minecraft.player);
            minecraft.player.sendSystemMessage(Component.translatable("message.stream_camera.point_saved"));
        }
        if (KEY_F8.consumeClick()) {
            ModSettings.reload();
            LifetimeManager.updateInterval();
            minecraft.player.sendSystemMessage(Component.translatable("message.stream_camera.config_reloaded",
                    ModSettings.getTeleportIntervalSeconds()));
        }
        if (KEY_UP.consumeClick()) {
            LifetimeManager.nextPoint();
        }
        if (KEY_DOWN.consumeClick()) {
            LifetimeManager.previousPoint();
        }
    }
}