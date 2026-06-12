package org.hodytrapl.stream_camera;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.hodytrapl.stream_camera.camera.CameraPointManager;
import org.hodytrapl.stream_camera.camera.LifetimeManager;
import org.hodytrapl.stream_camera.command.ServerCommands;
import org.hodytrapl.stream_camera.config.ModPaths;
import org.hodytrapl.stream_camera.config.ModSettings;
import org.hodytrapl.stream_camera.config.ServerAccessConfig;
import org.hodytrapl.stream_camera.key.KeyInputHandler;
import org.hodytrapl.stream_camera.network.TeleportPayload;
import org.slf4j.Logger;

@Mod(Stream_camera.MODID)
public class Stream_camera {
    public static final String MODID = "stream_camera";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Stream_camera.java
    public Stream_camera(IEventBus modBus, ModContainer modContainer) {
        ModPaths.getConfigDir();
        ModSettings.load();          // клиентский интервал – на сервере бесполезен, но не вредит

        // Загружаем серверный конфиг, если мы на выделенном сервере
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            ServerAccessConfig.load();
            NeoForge.EVENT_BUS.addListener(ServerCommands::register);
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(KeyInputHandler::registerKeys);
            NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, KeyInputHandler::onClientTick);
            NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
                CameraPointManager.reloadPoints();
            });
        }

        modBus.addListener(this::registerPayloads);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").optional().playToServer(
                TeleportPayload.TYPE,
                TeleportPayload.STREAM_CODEC,
                TeleportPayload::handle
        );
    }
}