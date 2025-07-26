package com.thepikabone.rcpokememory.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;


public class PokeMemoryClearCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("memory")
                .then(CommandManager.literal("clear")
                    .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests(new PokeMemoryPlayerSuggestionProvider())
                        .executes(PokeMemoryClearCommand::run))));
                        // TODO: requires permission?
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "player");
        context.getSource().sendFeedback(() -> Text.literal("Called /memory clear %s".formatted(name)), true);
        return 1;
    }
}
