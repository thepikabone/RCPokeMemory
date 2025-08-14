package com.thepikabone.rcpokememory;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.common.collect.Lists;
import com.thepikabone.rcpokememory.storage.PlayersConfig;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.html.HTMLElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;


public class PokeMemoryGUI extends SimpleGui {

    private int ticker = 0;
    private boolean correct = true;
    private final PlayerMemory player_memory;
    public int temp_card = -1;
    public int prev_card = -1;
    public ServerPlayerEntity player;
    public <T extends PokeMemoryGUI> PokeMemoryGUI(ServerPlayerEntity player, PlayerMemory player_memory) {

        super(ScreenHandlerType.GENERIC_9X5, player, false);
        this.setTitle(Text.literal(RCPokeMemory.getLang().gui.lives.replace("%lives%", String.valueOf(player_memory.memory.lives))));
        this.player_memory = player_memory;
        this.player = player;

        try{
            this.updateDisplay();
        } catch (FileNotFoundException | UnsupportedEncodingException e){
            RCPokeMemory.LOGGER.info(e.toString());
        }
    }

    private void flip(int card) throws FileNotFoundException, UnsupportedEncodingException {
        if (player_memory.memory.canGuess()) {
            if (player_memory.memory.canGuess() && correct) {
                if (player_memory.memory.activeGuess.isEmpty()) {
                    player_memory.memory.answer(card);
                    this.updateDisplay();
                } else if (player_memory.memory.canGuess()) {
                    this.prev_card = player_memory.memory.activeGuess().getFirst();
                    boolean temp = player_memory.memory.answer(card);
                    if (!temp)
                        this.temp_card = card;
                    this.updateDisplay();
                    correct = temp;
                }
            }
            if (player_memory.memory.hasAnsweredCorrectly()) {
                this.player.sendMessage(
                        Text.literal(RCPokeMemory.getLang().gui.feedback.successfullyCompleted));
                MinecraftServer server = player.getServer();
                if (server == null) return;

                ServerCommandSource src = server.getCommandSource().withLevel(4);

                String name = player.getGameProfile().getName();

                for (String raw : RCPokeMemory.getSettings().rewardCommands) {
                    String cmd = raw.replace("%player%", name);
                    server.getCommandManager().executeWithPrefix(src, cmd);
                }
                this.player_memory.lastCompletedAt = Instant.now();
                this.updateDisplay();

            } else if (!player_memory.memory.canGuess()) {
                this.player.sendMessage(
                        Text.literal(RCPokeMemory.getLang().gui.feedback.failCompleted));
                this.player_memory.lastCompletedAt = Instant.now();
                this.updateDisplay();
            }

            ((PlayersConfig) RCPokeMemory.getPlayers().get()).update(this.player_memory);
            RCPokeMemory.getPlayers().save();

        }
    }

    public void apply_cooldown() {
        if (this.player_memory.lastCompletedAt != null &&
                Instant.now().isAfter(this.player_memory.lastCompletedAt.plus(RCPokeMemory.getSettings().cooldownAfterCompleted))) {
            this.player_memory.reset();
            this.player_memory.init();
            ((PlayersConfig) RCPokeMemory.getPlayers().get()).update(this.player_memory);
            RCPokeMemory.getPlayers().save();
        }
    }

    public DisplayElement generate_clock_or_star() {
        if (this.player_memory.lastCompletedAt == null) {
            return DisplayElement.star();
        } else {
            Instant target = this.player_memory.lastCompletedAt.plus(RCPokeMemory.getSettings().cooldownAfterCompleted);
            long secondsLeft = Duration.between(Instant.now(), target).getSeconds();

            return DisplayElement.of(
                    new GuiElementBuilder(Items.CLOCK)
                            .setName(Text.literal("Cooldown: " + secondsLeft + "s"))
                            .hideDefaultTooltip()
            );
        }
    }

    @Override
    public void onTick() {
        if (!correct) {
            ticker++;
            if (ticker >= 20) {
                ticker = 0;
                this.correct = true;
                this.temp_card = -1;
                this.prev_card = -1;
                try {
                    updateDisplay();
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        } else if (this.player_memory.lastCompletedAt != null) {
            ticker++;
            if (ticker >= 20) {
                ticker = 0;
                try {
                    this.apply_cooldown();
                    this.updateDisplay();
                } catch (FileNotFoundException | UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void updateDisplay() throws FileNotFoundException, UnsupportedEncodingException {
        this.setTitle(Text.literal(RCPokeMemory.getLang().gui.lives.replace("%lives%", String.valueOf(player_memory.memory.lives))));
        for (int i = 0; i < 45; i++) {
            var element = this.getElement(i);

            if (element.element() != null) {
                this.setSlot(i, element.element());
            }
        }

    }

    public ItemStack givePokemonModel() {
        // Create the base item

//        NbtCompound pokemonNbt = new NbtCompound();
//        pokemonNbt.putString("species", "cobblemon:pikachu");
//        pokemonNbt.put("aspects", new NbtCompound()); // empty aspects
//
//        // Load the Pokémon object from NBT
//        DynamicRegistryManager regs = Objects.requireNonNull(this.player.getServer()).getRegistryManager();
//        Pokemon pokemon = Pokemon.Companion.create(Species.INSTANCE.getByIdentifier(speciesId));

        Pokemon pokemon = Objects.requireNonNull(PokemonSpecies.INSTANCE.getByPokedexNumber(25, "cobblemon"))
                .create(1);
        // Create the model item with the Pokémon data

        return PokemonItem.from(pokemon);
    }

    public DisplayElement pika() {
            return DisplayElement.of(
                    new GuiElementBuilder(this.givePokemonModel())
                            .setName(Text.literal("PIKAS"))
                            .hideDefaultTooltip());
    }

    protected DisplayElement getElement(int id) throws FileNotFoundException, UnsupportedEncodingException {
        if ((id >= 0 && id <= 9) || id == 17) {
            try {
                DisplayElement a = this.pika();
                return a;
            } catch (Exception e)  {
                RCPokeMemory.LOGGER.info(e.toString());
                return DisplayElement.red();
            }

        } else if (id == 18 || id == 26) {
            return DisplayElement.black();
        } else if ((id >= 35 && id <= 44) || id == 27) {
            return DisplayElement.white();
        } else if (id == 22) {
            return this.generate_clock_or_star();
        } else {
            return DisplayElement.cards(this, id);
        }
    }

    public record DisplayElement(@Nullable GuiElementInterface element, @Nullable Slot slot) {

        private static final DisplayElement EMPTY = DisplayElement.of(
                new GuiElement(ItemStack.EMPTY, GuiElementInterface.EMPTY_CALLBACK));

        private static final DisplayElement RED = DisplayElement.of(
                new GuiElementBuilder(Items.RED_STAINED_GLASS_PANE)
                        .setName(Text.literal(""))
                        .hideDefaultTooltip()
        );

        private static final DisplayElement BLACK = DisplayElement.of(
                new GuiElementBuilder(Items.BLACK_STAINED_GLASS_PANE)
                        .setName(Text.literal(""))
                        .hideDefaultTooltip()
        );

        private static final DisplayElement WHITE = DisplayElement.of(
                new GuiElementBuilder(Items.WHITE_STAINED_GLASS_PANE)
                        .setName(Text.literal(""))
                        .hideDefaultTooltip()
        );

//        private static final DisplayElement PIKA = DisplayElement.of(
//                new GuiElementBuilder(givePokemonModel())
//                        .setName(Text.literal("Pikachu"))
//                        .hideDefaultTooltip()
//        );


        private static final List<String> arr = RCPokeMemory.getLang().gui.instruction_item;;
        private static final DisplayElement STAR = DisplayElement.of(
                new GuiElementBuilder(Items.NETHER_STAR)
                        .setName(Text.literal(RCPokeMemory.getLang().gui.welcome))
                        .setLore(arr.stream()
                                .map(Text::literal)
                                .collect(Collectors.toList()))
                        .hideDefaultTooltip()
        );

        public static DisplayElement of(GuiElementInterface element) {
            return new DisplayElement(element, null);
        }

        public static DisplayElement of(GuiElementBuilderInterface<?> element) {
            return new DisplayElement(element.build(), null);
        }

        public static DisplayElement red() {
            return RED;
        }

//        public static DisplayElement pika() {
//            return PIKA;
//        }
//
        public static DisplayElement black() {
            return BLACK;
        }

        public static DisplayElement white() {
            return WHITE;
        }

        public static DisplayElement star() {
            return STAR;
        }

        public static DisplayElement empty() {
            return EMPTY;
        }

        private static List<Integer> getDeckSlots() {
            return Lists.newArrayList(new Integer[] {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34});
        }

        public static DisplayElement cards(PokeMemoryGUI gui, int card) throws FileNotFoundException, UnsupportedEncodingException {

            int card_flip = getDeckSlots().indexOf(card);

            if ((gui.player_memory.memory.answered().contains(card_flip) || gui.player_memory.memory.activeGuess().contains(card_flip) || gui.temp_card == card_flip || gui.prev_card == card_flip)) {// "namespace:path"
                Item item = Registries.ITEM.get(Identifier.of(gui.player_memory.memory.deck().get(card_flip)));
                return DisplayElement.of(
                        new GuiElementBuilder(item)
                                .setName(Text.translatable(item.getTranslationKey()))
                                .hideDefaultTooltip());
            } else {
                return DisplayElement.of(
                        new GuiElementBuilder(Items.GOLD_BLOCK)
                                .setName(Text.literal("???"))
                                .hideDefaultTooltip()
                                .setCallback((index, type1, action) -> {
                                    try {
                                        gui.flip(card_flip);
                                    } catch (FileNotFoundException | UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
            }
        }
    }
}