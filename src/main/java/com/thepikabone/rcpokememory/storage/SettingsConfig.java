package com.thepikabone.rcpokememory.storage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class SettingsConfig {
    @Setting
    public List<String> rewardCommands = new ArrayList<String>() {
        {
            this.add("give %player% minecraft:diamond 1");
        }
    };
    @Setting
    public int totalLivesPerGame = 15;
    @Setting
    public Duration cooldownAfterCompleted = Duration.ofSeconds(60);
}