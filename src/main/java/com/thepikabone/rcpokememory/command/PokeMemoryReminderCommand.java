package com.thepikabone.rcpokememory.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.thepikabone.rcpokememory.PlayerMemory;
import com.thepikabone.rcpokememory.PokeMemoryGUI;
//import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;
import java.util.UUID;


public class PokeMemoryReminderCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
//        dispatcher.register(CommandManager.literal("memory")
//            .then(CommandManager.literal("reminder")
//                .then(CommandManager.argument("boolean", BoolArgumentType.bool()).
//                    requires(Permissions.require("rcpokememory.reminder", 0))
//                        .executes(PokeMemoryReminderCommand::run))));
    }

    public static int run(CommandContext<ServerCommandSource> context) {
		return 1;
    }
}
