package com.thepikabone.rcpokememory.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.thepikabone.rcpokememory.PlayerMemory;
import com.thepikabone.rcpokememory.PokeMemoryGUI;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.Objects;
import java.util.UUID;


public class PokeMemoryMainCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("memory")
                .executes(PokeMemoryMainCommand::run));
    }

    public static int run(CommandContext<ServerCommandSource> context) {
        UUID uuid = Objects.requireNonNull(context.getSource().getPlayer()).getUuid();
        PlayerMemory player_mem = new PlayerMemory(uuid);
        player_mem.init();
        PokeMemoryGUI gui = new PokeMemoryGUI(context.getSource().getPlayer(), player_mem);
        gui.open();
		return 1;
    }
}
