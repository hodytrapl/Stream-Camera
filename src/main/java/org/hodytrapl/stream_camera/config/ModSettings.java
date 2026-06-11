package org.hodytrapl.stream_camera.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.nio.file.Path;

public class ModSettings {
    //храним все настройки в конфиге
    private static CommentedFileConfig config;
    private static int teleportIntervalSeconds = 2;

    public static void load() {
        Path configPath = ModPaths.getConfigDir().resolve("settings.toml");
        config = CommentedFileConfig.builder(configPath).sync().build();
        config.load();

        // Проверяем, есть ли нужное значение в конфиге
        if (!config.contains("general.teleportIntervalSeconds")) {
            // Нет – записываем дефолты
            config.set("general.teleportIntervalSeconds", 2);
            config.setComment("general.teleportIntervalSeconds", "Interval between automatic teleports (seconds)");
            config.save();
        }

        // Теперь читаем значение (даже если только что записали)
        teleportIntervalSeconds = config.getIntOrElse("general.teleportIntervalSeconds", 2);
    }

    public static void reload() {
        if (config != null) {
            config.load(); // перезагрузить с диска
            teleportIntervalSeconds = config.getIntOrElse("general.teleportIntervalSeconds", 2);
        }
    }

    public static int getTeleportIntervalSeconds() {
        return teleportIntervalSeconds;
    }
}