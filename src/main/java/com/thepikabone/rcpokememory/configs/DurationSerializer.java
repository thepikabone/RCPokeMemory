package com.thepikabone.rcpokememory.configs;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class DurationSerializer implements TypeSerializer<Duration> {
    public Duration deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        return Duration.parse(Objects.requireNonNull(node.getString()));
    }

    public void serialize(@NotNull Type type, Duration obj, ConfigurationNode node) throws SerializationException {
        assert obj != null;
        node.set(obj.toString());
    }
}
