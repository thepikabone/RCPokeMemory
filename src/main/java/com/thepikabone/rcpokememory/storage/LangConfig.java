package com.thepikabone.rcpokememory.storage;


import java.util.ArrayList;
import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class LangConfig {
    @Setting
    public GuiMessages gui = new GuiMessages();

    @ConfigSerializable
    public static class GuiMessages {
        @Setting
        public String welcome = "Welcome to RCPokememory";
        @Setting
        public String lives = "RCPokememory >> %lives% lives";
        @Setting
        public Feedback feedback = new Feedback();
        @Setting
        public List<String> instruction_item = new ArrayList<String>() {
            {
                this.add("");
                this.add("Find the pairs in the deck");
                this.add("of Pokemon! The goal is to find all");
                this.add("pairs before you run out of lives!");
            }
        };

        @ConfigSerializable
        public static class Feedback {
            @Setting
            public String cooldown = "You can solve another RCPokéMemory in %time%.";
            @Setting
            public String failCompleted = "You were unable to solve the RCPokéMemory.";
            @Setting
            public String successfullyCompleted = "Congratulations! You successfully solved the RCPokéMemory!";
            @Setting
            public String partialCompleted = "Congratulations! You successfully matched one pair of pokemon.";
        }
    }
}