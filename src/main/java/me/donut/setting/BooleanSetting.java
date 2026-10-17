package me.donut.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean value) {
        super(name, value);
    }

    @Override
    public void next() {
        value = !value;
    }
}
