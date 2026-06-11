package org.hodytrapl.stream_camera.config;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ModPaths {
    //храним гдето в своем файле, пример с плагинов
    private static final String MOD_DIR_NAME = "stream_camera";
    private static Path configDir = null;

    public static Path getConfigDir() {
        if (configDir == null) {
            configDir = FMLPaths.CONFIGDIR.get().resolve(MOD_DIR_NAME);
            if (!configDir.toFile().exists()) {
                configDir.toFile().mkdirs();
            }
        }
        return configDir;
    }

    public static Path getPointsFile() {
        return getConfigDir().resolve("points.toml");
    }
}