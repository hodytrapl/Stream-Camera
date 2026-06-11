package org.hodytrapl.stream_camera.camera;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.world.entity.player.Player;
import org.hodytrapl.stream_camera.config.ModPaths;

import java.util.ArrayList;
import java.util.List;

public class CameraPointManager {
    private static final List<PointData> points = new ArrayList<>();

    public static class PointData {
        public final double x, y, z;
        public final float yaw, pitch;
        public PointData(double x, double y, double z, float yaw, float pitch) {
            this.x = x; this.y = y; this.z = z; this.yaw = yaw; this.pitch = pitch;
        }
    }

    public static void saveCurrentPoint(Player player) {
        PointData newPoint = new PointData(player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot());
        loadPoints();
        points.add(newPoint);
        savePoints();
    }

    public static List<PointData> getPoints() {
        return new ArrayList<>(points);
    }

    public static void reloadPoints() {
        loadPoints();
    }

    private static void loadPoints() {
        var pointsFile = ModPaths.getPointsFile().toFile();
        if (!pointsFile.exists()) return;

        try (CommentedFileConfig config = CommentedFileConfig.builder(pointsFile).sync().build()) {
            config.load();
            var pointsList = config.get("points");
            if (pointsList instanceof List<?>) {
                points.clear();
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

    private static void savePoints() {
        var pointsFile = ModPaths.getPointsFile().toFile();
        try (CommentedFileConfig config = CommentedFileConfig.builder(pointsFile).sync().build()) {
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
}