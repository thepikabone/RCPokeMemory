package com.thepikabone.rcpokememory;

import com.thepikabone.rcpokememory.configs.ConfigManager;
import com.thepikabone.rcpokememory.configs.DurationSerializer;
import com.thepikabone.rcpokememory.configs.InstantSerializer;
import com.thepikabone.rcpokememory.storage.LangConfig;
import com.thepikabone.rcpokememory.storage.PlayersConfig;
import com.thepikabone.rcpokememory.storage.SettingsConfig;
import io.leangen.geantyref.TypeToken;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import com.thepikabone.rcpokememory.command.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class RCPokeMemory implements ModInitializer {
	public static final String MOD_ID = "rcpokememory";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static ConfigManager<LangConfig> lang;
	private static ConfigManager<SettingsConfig> settings;
	private static ConfigManager<PlayersConfig> players;
	private static RCPokeMemory instance;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		instance = this;
		CommandRegistrationCallback.EVENT.register(PokeMemoryMainCommand::register);
		CommandRegistrationCallback.EVENT.register(PokeMemoryClearCommand::register);
//		CommandRegistrationCallback.EVENT.register(PokeMemoryReminderCommand::register);

		File directory = new File("config/RCPokeMemory");
		lang = new ConfigManager<>(LangConfig.class, directory, "lang.conf");
		settings = new ConfigManager<>(SettingsConfig.class, directory, "config.conf", TypeSerializerCollection.defaults().childBuilder().register(TypeToken.get(Duration.class), new DurationSerializer()).build());
		players = new ConfigManager<>(PlayersConfig.class, directory, "players.storage", TypeSerializerCollection.defaults().childBuilder().register(TypeToken.get(Instant.class), new InstantSerializer()).build());

		LOGGER.info("Registered RCPokeMemory");
	}

	public static RCPokeMemory getInstance() {
		return instance;
	}

	public static LangConfig getLang() {
		return (LangConfig)lang.get();
	}

	public static SettingsConfig getSettings() {
		return (SettingsConfig)settings.get();
	}

	public static ConfigManager<PlayersConfig> getPlayers() {
		return players;
	}
}
