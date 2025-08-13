package com.thepikabone.rcpokememory.command;

//import me.lucko.fabric.api.permissions.v0.Permissions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.thepikabone.rcpokememory.PlayerMemory;
import com.thepikabone.rcpokememory.PokeMemoryGUI;
import com.thepikabone.rcpokememory.RCPokeMemory;
import com.thepikabone.rcpokememory.storage.PlayersConfig;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public class PokeMemoryMainCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){
        dispatcher.register(CommandManager.literal("memory")
//                .requires(Permissions.require("rcpokememory.memory", 0))
                .executes(PokeMemoryMainCommand::run));
    }

    public static int run(CommandContext<ServerCommandSource> context) {
        UUID uuid = Objects.requireNonNull(context.getSource().getPlayer()).getUuid();
        Optional<PlayerMemory> memory = ((PlayersConfig) RCPokeMemory.getPlayers().get()).find(uuid);
        if (memory.isEmpty()) {
            PlayerMemory player_mem = new PlayerMemory(uuid);
            player_mem.init();
            ((PlayersConfig) RCPokeMemory.getPlayers().get()).update(player_mem);
            RCPokeMemory.getPlayers().save();
            PokeMemoryGUI gui = new PokeMemoryGUI(context.getSource().getPlayer(), player_mem);
            gui.open();
        } else {

            if (((PlayerMemory) memory.get()).getMemory() == null) {
                memory.get().init();
                RCPokeMemory.getPlayers().save();
            }
            PokeMemoryGUI gui = new PokeMemoryGUI(context.getSource().getPlayer(), memory.get());
            gui.open();
        }

		return 1;
    }
}
