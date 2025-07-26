package com.thepikabone.rcpokememory;



import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class PlayerMemory {
    private static final int NUMBER_OF_SPECIES = 10;

    public UUID uuid;
    public Memory memory;
    public Instant startedAt;
    public Instant lastCompletedAt;

    public PlayerMemory(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerMemory() {
    }

    public void reset() {
        this.memory = null;
    }

    public void init() {
//        Map<Integer, String> deck = new HashMap<>();
//
//        for(int i = 0; i < 10; ++i) {
//            Species species = null;
//
//            while(species == null || species.getDex() == 0) {
//                species = PixelmonSpecies.getRandomSpecies();
//                if (deck.containsValue(species.getName()) || blacklisted.contains(species.getName().toLowerCase())) {
//                    species = null;
//                }
//            }
//
//            this.addCardRandomlyInDeck(deck, species);
//            this.addCardRandomlyInDeck(deck, species);
//        }

        List<String> BLOCK_IDS = List.of(
                "minecraft:magma_block",
                "minecraft:grass_block",
                "minecraft:oak_log",
                "minecraft:oak_leaves",   // pick a specific leaf type
                "minecraft:end_stone",
                "minecraft:granite",
                "minecraft:oak_planks",   // choose your wood type
                "minecraft:glass",
                "minecraft:diamond_block",
                "minecraft:cobblestone"
        );

        List<String> bag = new ArrayList<>(20);
        for (String s : BLOCK_IDS) {
            bag.add(s);
            bag.add(s);
        }

        Collections.shuffle(bag, new Random());

        Map<Integer, String> deck = new HashMap<>();
        for (int i = 0; i < bag.size(); i++) {
            deck.put(i, bag.get(i));
        }

        this.memory = new Memory(deck);
        this.startedAt = Instant.now();
    }

//    private void addCardRandomlyInDeck(Map<Integer, String> cards, Species species) {
//        List<Integer> allowedNumbers = (List)IntStream.range(0, 21).filter((s) -> !cards.containsKey(s)).boxed().collect(Collectors.toList());
//        int index = ThreadLocalRandom.current().nextInt(0, allowedNumbers.size());
//        cards.put((Integer)allowedNumbers.get(index), species.getName());
//    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Memory getMemory() {
        return this.memory;
    }

    public void setCompleted() {
        this.lastCompletedAt = Instant.now();
    }

    public Instant getLastCompletedAt() {
        return this.lastCompletedAt;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }


    public static class Memory {
        public Map<Integer, String> deck = new HashMap<>();
        public List<Integer> answered = new ArrayList<>();
        public List<Integer> activeGuess = new ArrayList<>();
        public boolean completed = false;
        public boolean receivedReward = false;
        public int lives;


        public List<Integer> answered() {
            return answered;
        }
        public List<Integer> activeGuess() {
            return activeGuess;
        }
        public Memory(Map<Integer, String> deck) {
            this.lives = 15;
            this.deck = deck;
        }

        public Memory() {
            this.lives = 15;
        }

        public boolean answer(int pick) {
            this.activeGuess.add(pick);
            if (this.activeGuess.size() > 1) {
                String active = (String)this.deck.get(this.activeGuess.getFirst());
                String picked = (String)this.deck.get(pick);
                if (active.equalsIgnoreCase(picked)) {
                    this.answered.add((Integer)this.activeGuess.getFirst());
                    this.answered.add(pick);
                    this.activeGuess = new ArrayList<>();
                    if (this.foundAll()) {
                        this.completed = true;
                    }
                    return true;
                }
                this.activeGuess = new ArrayList<>();
                --this.lives;
            }
            return false;
        }

        public boolean foundAll() {
            return (new HashSet<>(this.answered)).containsAll(this.deck.keySet());
        }

        public boolean hasReceivedReward() {
            return this.receivedReward;
        }

        public boolean canGuess() {
            if (this.hasAnsweredCorrectly()) {
                return false;
            } else {
                return this.lives > 0;
            }
        }

        public boolean canGuessSlot(int slot) {
            return !this.answered.contains(slot) && !this.activeGuess.contains(slot);
        }

//        public void completeSuccess(ServerPlayerEntity player) {
//            if (!this.receivedReward) {
//                this.receivedReward = true;
//
//                for(String rewardCommand : PokeMemory.getSettings().rewardCommands) {
//                    ServerLifecycleHooks.getCurrentServer().func_195571_aL().func_197059_a(ServerLifecycleHooks.getCurrentServer().func_195573_aM(), rewardCommand.replace("@p", player.func_200200_C_().getString()));
//                }
//            }
//        }

        public boolean hasAnsweredCorrectly() {
            return this.completed;
        }

        public Map<Integer, String> deck() {
            return deck;
        }


    }
}