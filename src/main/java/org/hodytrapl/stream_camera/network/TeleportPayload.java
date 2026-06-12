package org.hodytrapl.stream_camera.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.hodytrapl.stream_camera.config.ServerAccessConfig;

public record TeleportPayload(double x, double y, double z, float yaw, float pitch) implements CustomPacketPayload {
    public static final Type<TeleportPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("stream_camera", "teleport"));

    public static final StreamCodec<FriendlyByteBuf, TeleportPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, TeleportPayload::x,
            ByteBufCodecs.DOUBLE, TeleportPayload::y,
            ByteBufCodecs.DOUBLE, TeleportPayload::z,
            ByteBufCodecs.FLOAT, TeleportPayload::yaw,
            ByteBufCodecs.FLOAT, TeleportPayload::pitch,
            TeleportPayload::new
    );
    //абомба

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // org.hodytrapl.stream_camera.network.TeleportPayload
    public static void handle(TeleportPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                String playerName = serverPlayer.getGameProfile().getName();
                if (!ServerAccessConfig.isAllowed(playerName)) {
                    serverPlayer.displayClientMessage(
                            Component.literal("§cУ вас нет прав на использование телепортации!"),
                            false
                    );
                    return;
                }
                serverPlayer.connection.teleport(payload.x, payload.y, payload.z, payload.yaw, payload.pitch);
            }
        });
    }
}