package com.thepikabone.rcpokememory.storage;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import com.thepikabone.rcpokememory.PlayerMemory;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PlayersConfig {
    @Setting
    public HashMap<UUID, PlayerMemory> memories = new HashMap<>();

    public Optional<PlayerMemory> find(UUID uuid) {
        return Optional.ofNullable((PlayerMemory)this.memories.get(uuid));
    }

    public void update(PlayerMemory memory) {
        this.memories.put(memory.getUuid(), memory);
    }
}
