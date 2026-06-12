package org.hodytrapl.stream_camera.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ServerAccessConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerAccessConfig.class);
    private static List<String> allowedNames = Collections.emptyList(); // в нижнем регистре

    public static void load() {
        Path configPath = ModPaths.getConfigDir().resolve("server.toml");
        CommentedFileConfig config = CommentedFileConfig.builder(configPath).sync().build();
        config.load();

        if (!config.contains("allowedPlayers")) {
            config.set("allowedPlayers", Collections.emptyList());
            config.setComment("allowedPlayers", "List of player names (case-insensitive) allowed to use teleportation");
            config.save();
        }

        List<String> rawList = config.getOrElse("allowedPlayers", Collections.emptyList());
        allowedNames = rawList.stream()
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        LOGGER.info("Loaded server access config: {} allowed player(s) - {}", allowedNames.size(), allowedNames);
    }

    public static boolean isAllowed(String playerName) {
        if (playerName == null) return false;
        return allowedNames.contains(playerName.toLowerCase(Locale.ROOT));
    }

    public static void reload() {
        load();
    }
}