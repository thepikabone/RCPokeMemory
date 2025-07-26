package com.thepikabone.rcpokememory;

import com.google.common.collect.Lists;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilderInterface;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class PokeMemoryGUI extends SimpleGui {

    private int ticker = 0;
    private boolean correct = true;
    private int countdown = 0;
    private final PlayerMemory player_memory;
    public int temp_card = -1;
    public int prev_card = -1;
    public ServerPlayerEntity player;
    public <T extends PokeMemoryGUI> PokeMemoryGUI(ServerPlayerEntity player, PlayerMemory player_memory) {

        super(ScreenHandlerType.GENERIC_9X5, player, false);
        this.setTitle(Text.literal("PokeMemory >> 15 Lives"));
        this.player_memory = player_memory;
        this.player = player;

        try{
            this.updateDisplay();
        } catch (FileNotFoundException | UnsupportedEncodingException e){
            RCPokeMemory.LOGGER.info(e.toString());
        }
    }

    private void flip(int card) throws FileNotFoundException, UnsupportedEncodingException {
        if (player_memory.memory.canGuess() && correct){
            if (player_memory.memory.activeGuess.isEmpty()) {
                player_memory.memory.answer(card);
                this.updateDisplay();
            } else if (player_memory.memory.canGuess()) {
                this.prev_card = player_memory.memory.activeGuess().getFirst();
                boolean temp = player_memory.memory.answer(card);
                if(!temp)
                    this.temp_card = card;
                this.setTitle(Text.literal(String.format("PokeMemory >> %d Lives", player_memory.memory.lives)));
                this.updateDisplay();
                correct = temp;
            }
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
        }

        if (player_memory.memory.completed) {

            this.player.sendMessage(
                        Text.literal("PokeMemory Cleared"),
                        true);
        }
    }

    public void updateDisplay() throws FileNotFoundException, UnsupportedEncodingException {
        for (int i = 0; i < 45; i++) {
            var element = this.getElement(i);

            if (element.element() != null) {
                this.setSlot(i, element.element());
            }
        }
    }

    protected DisplayElement getElement(int id) throws FileNotFoundException, UnsupportedEncodingException {
        if ((id >= 0 && id <= 9) || id == 17) {
            return DisplayElement.red();
        } else if (id == 18 || id == 26 || id == 22) {
            return DisplayElement.black();
        } else if ((id >= 35 && id <= 44) || id == 27) {
            return DisplayElement.white();
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

        public static DisplayElement of(GuiElementInterface element) {
            return new DisplayElement(element, null);
        }

        public static DisplayElement of(GuiElementBuilderInterface<?> element) {
            return new DisplayElement(element.build(), null);
        }

        public static DisplayElement red() {
            return RED;
        }

        public static DisplayElement black() {
            return BLACK;
        }

        public static DisplayElement white() {
            return WHITE;
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