package com.thepikabone.rcpokememory.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
//import me.lucko.fabric.api.permissions.v0.Permissions;
import com.thepikabone.rcpokememory.PlayerMemory;
import com.thepikabone.rcpokememory.PokeMemoryGUI;
import com.thepikabone.rcpokememory.RCPokeMemory;
import com.thepikabone.rcpokememory.storage.PlayersConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public class PokeMemoryClearCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("memory")
                .then(CommandManager.literal("clear")
                    .then(CommandManager.argument("player", StringArgumentType.string())
                        .suggests(new PokeMemoryPlayerSuggestionProvider())
                            .requires(source -> source.hasPermissionLevel(4))
                        .executes(PokeMemoryClearCommand::run))));

    }

    private static int run(CommandContext<ServerCommandSource> context) {
        String player_name = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getServer();
        ServerPlayerEntity targetPlayer = server.getPlayerManager().getPlayer(player_name);
        if (targetPlayer != null) {
            UUID uuid = targetPlayer.getUuid();
            Optional<PlayerMemory> memory = ((PlayersConfig) RCPokeMemory.getPlayers().get()).find(uuid);

            if (memory.isEmpty()) {
                PlayerMemory player_mem = new PlayerMemory(uuid);
                player_mem.init();
                ((PlayersConfig) RCPokeMemory.getPlayers().get()).update(player_mem);
                RCPokeMemory.getPlayers().save();
            } else {
                memory.get().init();
                RCPokeMemory.getPlayers().save();
            }

        } else {
            context.getSource().sendFeedback(() -> Text.literal("RCPokeMemory: No player found"), true);
            return 0;
        }
        return 1;
    }
}
