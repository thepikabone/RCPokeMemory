package com.thepikabone.rcpokememory.configs;


import io.leangen.geantyref.TypeToken;
import java.io.File;
import java.io.IOException;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class ConfigManager<T> {
    public HoconConfigurationLoader loader;
    public CommentedConfigurationNode node;
    public Class<T> clazz;
    private TypeToken<T> token;
    public T value;
    private TypeSerializerCollection serializers;

    public ConfigManager(Class<T> clazz, File configDir, String file) {
        this(clazz, configDir, file, (TypeSerializerCollection)null);
    }

    public ConfigManager(Class<T> clazz, File configDir, String file, TypeSerializerCollection serializers) {
        this.clazz = clazz;
        this.token = TypeToken.get(clazz);
        this.serializers = serializers;
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File c = new File(configDir, file);

        try {
            if (!c.exists()) {
                c.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder().addNodeResolver(NodeResolver.onlyWithSetting()).build();
        this.loader = ((HoconConfigurationLoader.Builder)((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().file(c)).defaultOptions((options) -> ConfigurationOptions.defaults().serializers((builder) -> {
            builder.registerAnnotatedObjects(customFactory);
            if (serializers != null) {
                builder.registerAll(serializers);
            }

        }))).build();
        this.value = (T)this.load(false);
    }

    public T load(boolean set) {
        try {
            this.node = (CommentedConfigurationNode)this.loader.load();
            T value = (T)(set ? this.value : this.node.get(this.token, this.clazz.getDeclaredConstructor().newInstance()));
            this.node.set(this.token, value);
            this.loader.save(this.node);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** @deprecated */
    @Deprecated
    public T getConfig() {
        return this.value;
    }

    public T get() {
        return this.value;
    }

    public void reload() {
        this.value = (T)this.load(false);
    }

    public void save() {
        this.load(true);
    }
}
