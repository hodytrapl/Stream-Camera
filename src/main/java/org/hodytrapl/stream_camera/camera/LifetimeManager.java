package org.hodytrapl.stream_camera.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.hodytrapl.stream_camera.config.ModSettings;
import org.hodytrapl.stream_camera.network.TeleportPayload;

import java.util.List;

public class LifetimeManager {
    private static boolean active = false;
    private static int currentIndex = 0;
    private static int tickCounter = 0;
    private static int intervalTicks = 20 * 2; // начальное значение 2 секунды
    private static List<CameraPointManager.PointData> points = List.of();

    // Вызывать при загрузке конфига
    public static void updateInterval() {
        intervalTicks = ModSettings.getTeleportIntervalSeconds() * 20;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal("Интервал телепортации: " + ModSettings.getTeleportIntervalSeconds() + " секунд"));
        }
    }

    public static void toggle() {
        if (active) {
            stop();
        } else {
            start();
        }
    }

    public static void start() {
        CameraPointManager.reloadPoints();
        points = CameraPointManager.getPoints();
        if (points.isEmpty()) {
            Minecraft.getInstance().player.sendSystemMessage(
                    Component.literal("Нет сохранённых точек! Нажмите F7 для сохранения."));
            return;
        }
        active = true;
        currentIndex = 0;
        tickCounter = 0;
        teleportToCurrentPoint();
        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("Цикл телепортации запущен. Текущая точка: 1/" + points.size()));
    }

    public static void stop() {
        active = false;
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Цикл телепортации остановлен."));
    }

    public static void tick() {
        if (!active) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        tickCounter++;
        if (tickCounter >= intervalTicks) {
            tickCounter = 0;
            currentIndex = (currentIndex + 1) % points.size();
            if (currentIndex == 0) {
                mc.player.sendSystemMessage(Component.literal("Цикл завершён, начинаем сначала."));
            }
            teleportToCurrentPoint();
            mc.player.sendSystemMessage(
                    Component.literal("Телепорт в точку " + (currentIndex + 1) + "/" + points.size()));
        }
    }

    public static void nextPoint() {
        if (points.isEmpty()) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Нет сохранённых точек."));
            return;
        }
        currentIndex = (currentIndex + 1) % points.size();
        teleportToCurrentPoint();
        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("Ручной переход → точка " + (currentIndex + 1) + "/" + points.size()));
        if (active) {
            tickCounter = 0;
        }
    }

    public static void previousPoint() {
        if (points.isEmpty()) {
            Minecraft.getInstance().player.sendSystemMessage(Component.literal("Нет сохранённых точек."));
            return;
        }
        currentIndex = (currentIndex - 1 + points.size()) % points.size();
        teleportToCurrentPoint();
        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("Ручной переход ← точка " + (currentIndex + 1) + "/" + points.size()));
        if (active) {
            tickCounter = 0;
        }
    }

    private static void teleportToCurrentPoint() {
        if (points.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        CameraPointManager.PointData p = points.get(currentIndex);
        PacketDistributor.sendToServer(new TeleportPayload(p.x, p.y, p.z, p.yaw, p.pitch));
    }

    public static boolean isActive() {
        return active;
    }
}