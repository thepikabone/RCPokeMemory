package com.thepikabone.rcpokememory.configs;


import java.lang.reflect.Type;
import java.time.Instant;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class InstantSerializer implements TypeSerializer<Instant> {
    public Instant deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        return Instant.ofEpochMilli(node.getLong());
    }

    public void serialize(@NotNull Type type, Instant obj, ConfigurationNode node) throws SerializationException {
        assert obj != null;
        node.set(obj.toEpochMilli());
    }
}
