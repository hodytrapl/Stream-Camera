package org.hodytrapl.stream_camera.camera;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.hodytrapl.stream_camera.config.ModPaths;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CameraPointManager {
    private static final List<PointData> points = new ArrayList<>();
    private static String currentServerId = null;

    // Вложенный класс точки
    public static class PointData {
        public final double x, y, z;
        public final float yaw, pitch;
        public PointData(double x, double y, double z, float yaw, float pitch) {
            this.x = x; this.y = y; this.z = z; this.yaw = yaw; this.pitch = pitch;
        }
    }

    // Получить идентификатор текущего сервера или одиночной игры
    private static String getCurrentServerId() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isSingleplayer()) {
            return "singleplayer";
        }
        ServerData serverData = mc.getCurrentServer();
        if (serverData != null) {
            String ip = serverData.ip;
            // Заменяем недопустимые символы для имени файла
            return ip.replaceAll("[:/\\\\]", "_");
        }
        return "unknown";
    }

    public static void saveCurrentPoint(Player player) {
        String serverId = getCurrentServerId();
        loadPointsForServer(serverId);
        points.add(new PointData(player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot()));
        savePointsForServer(serverId);
    }

    public static List<PointData> getPoints() {
        return new ArrayList<>(points);
    }

    public static void reloadPoints() {
        String serverId = getCurrentServerId();
        loadPointsForServer(serverId);
    }

    private static void loadPointsForServer(String serverId) {
        if (serverId == null) return;
        currentServerId = serverId;
        points.clear();
        Path pointsFile = ModPaths.getPointsFileForServer(serverId);
        if (!pointsFile.toFile().exists()) {
            migrateOldPoints(serverId);
            return;
        }
        loadFromFile(pointsFile);
    }

    private static void savePointsForServer(String serverId) {
        if (serverId == null) return;
        Path pointsFile = ModPaths.getPointsFileForServer(serverId);
        saveToFile(pointsFile);
    }

    private static void loadFromFile(Path file) {
        try (CommentedFileConfig config = CommentedFileConfig.builder(file).sync().build()) {
            config.load();
            var pointsList = config.get("points");
            if (pointsList instanceof List<?>) {
                for (Object obj : (List<?>) pointsList) {
                    if (obj instanceof com.electronwill.nightconfig.core.Config) {
                        com.electronwill.nightconfig.core.Config c = (com.electronwill.nightconfig.core.Config) obj;
                        Number xNum = (Number) c.get("x");
                        Number yNum = (Number) c.get("y");
                        Number zNum = (Number) c.get("z");
                        Number yawNum = (Number) c.get("yaw");
                        Number pitchNum = (Number) c.get("pitch");
                        if (xNum != null && yNum != null && zNum != null && yawNum != null && pitchNum != null) {
                            points.add(new PointData(
                                    xNum.doubleValue(), yNum.doubleValue(), zNum.doubleValue(),
                                    yawNum.floatValue(), pitchNum.floatValue()
                            ));
                        }
                    }
                }
            }
        }
    }

    private static void saveToFile(Path file) {
        try (CommentedFileConfig config = CommentedFileConfig.builder(file).sync().build()) {
            List<com.electronwill.nightconfig.core.Config> pointsList = new ArrayList<>();
            for (PointData p : points) {
                var pointConfig = com.electronwill.nightconfig.core.Config.inMemory();
                pointConfig.set("x", p.x);
                pointConfig.set("y", p.y);
                pointConfig.set("z", p.z);
                pointConfig.set("yaw", p.yaw);
                pointConfig.set("pitch", p.pitch);
                pointsList.add(pointConfig);
            }
            config.set("points", pointsList);
            config.save();
        }
    }

    private static void migrateOldPoints(String newServerId) {
        Path oldFile = ModPaths.getPointsFile();
        if (oldFile.toFile().exists()) {
            loadFromFile(oldFile);
            if (!points.isEmpty()) {
                savePointsForServer(newServerId);
                oldFile.toFile().renameTo(oldFile.resolveSibling("points.toml.bak").toFile());
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    mc.player.sendSystemMessage(Component.translatable("message.stream_camera.migrated_points"));
                }
            }
        }
    }
}