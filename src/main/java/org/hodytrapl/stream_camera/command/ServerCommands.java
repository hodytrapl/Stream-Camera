// новый класс: ServerCommands
package org.hodytrapl.stream_camera.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.hodytrapl.stream_camera.config.ServerAccessConfig;

public class ServerCommands {
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("streamcamera")
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2)) // OP уровень 2
                        .executes(ServerCommands::reloadAccess))
        );
    }

    private static int reloadAccess(CommandContext<CommandSourceStack> ctx) {
        ServerAccessConfig.reload();
        ctx.getSource().sendSuccess(() -> Component.literal("Конфиг доступа перезагружен (по никам)."), true);
        return 1;
    }
}