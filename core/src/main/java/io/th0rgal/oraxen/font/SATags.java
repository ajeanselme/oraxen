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
    private static Map<String, String[]> GRADIENTS = new HashMap<>();

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

        if(config.isConfigurationSection("gradients")) {
            ConfigurationSection gradients = config.getConfigurationSection("gradients");
            if(gradients != null) {
                for (String gradient : gradients.getKeys(false)) {
                    String value = gradients.getString(gradient);
                    if(value != null) {
                        String[] split = value.split(",");
                        if(split.length == 2) {
                            GRADIENTS.put(gradient, split);
                        } else {
                            OraxenPlugin.get().getLogger().log(Level.SEVERE, "Invalid color value set for " + gradient);
                        }
                    }
                }
            }
        }
    }

    public static TextColor getColor(String name) {
        TextColor color = COLORS.get(name);
        if(color == null) {
            OraxenPlugin.get().getLogger().log(Level.SEVERE, "Color" + name + " not found");
            return TextColor.color(254, 254, 254);
        }

        return color;
    }

    public static TagResolver getResolver() {
        loadConfig();

        TagResolver.Builder builder = TagResolver.builder();
        for (String color : COLORS.keySet()) {
            builder.tag(color, Tag.styling(COLORS.get(color)));
        }

        for (String gradient : GRADIENTS.keySet()) {
            TextColor firstColor = getColor(GRADIENTS.get(gradient)[0]);
            TextColor secondColor = getColor(GRADIENTS.get(gradient)[1]);
            builder.tag(gradient, Tag.preProcessParsed("<gradient:" + firstColor.asHexString() + ":" + secondColor.asHexString() + ">"));
        }

        return builder.build();
    }
}
