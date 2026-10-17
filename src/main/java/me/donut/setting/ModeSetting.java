package me.donut.setting;

import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String... modes) {
        super(name, modes[0]);
        this.modes = List.of(modes);
    }

    public List<String> getModes() {
        return modes;
    }

    @Override
    public void next() {
        index = (index + 1) % modes.size();
        value = modes.get(index);
    }
}
