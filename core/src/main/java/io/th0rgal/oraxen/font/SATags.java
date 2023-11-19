package io.th0rgal.oraxen.font;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.utils.OraxenYaml;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class SATags {
    private static Map<String, TextColor> COLORS = new HashMap<>();

    private static void loadConfig() {
        if(!COLORS.isEmpty()) return;

        File file = new File("plugins/custom/tags.yml");
        if(!file.exists()) {
            OraxenPlugin.get().getLogger().log(Level.WARNING, "No custom tags set (custom/tags.yml)");
            return;
        }

        YamlConfiguration config = OraxenYaml.loadConfiguration(file);
        if(config.isConfigurationSection("colors")) {
            ConfigurationSection colors = config.getConfigurationSection("colors");
            if(colors != null) {
                for (String color : colors.getKeys(false)) {
                    String value = colors.getString(color);
                    if(value != null) {
                        String[] split = value.split(",");
                        if(split.length == 3) {
                            COLORS.put(color, TextColor.color(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2])));
                        } else {
                            OraxenPlugin.get().getLogger().log(Level.SEVERE, "Invalid rgb value set for " + color);
                        }
                    }
                }
            }
        }
    }

    public static TagResolver getResolver() {
        loadConfig();

        TagResolver.Builder builder = TagResolver.builder();
        for (String color : COLORS.keySet()) {
            builder.tag(color, Tag.styling(COLORS.get(color)));
        }

        return builder.build();
    }
}
