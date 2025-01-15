package io.th0rgal.oraxen.mechanics.provided.cosmetic.skin;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SkinMechanic extends Mechanic {

    private final boolean consume;
    final List<Material> canApplyOn = new ArrayList<>();

    public SkinMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section);
        this.consume = section.getBoolean("consume");

        Objects.requireNonNull(section.getList("canApplyOn")).forEach(m -> canApplyOn.add(Material.valueOf((String) m)));
        System.out.println(canApplyOn);
    }

    public boolean doConsume() {
        return consume;
    }
}
