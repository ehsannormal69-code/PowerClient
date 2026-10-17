package me.donut.module;

public enum Category {
    CRYSTAL("\uD83E\uDD6A", "Crystal"),
    COMBAT("\u2694\uFE0F", "Combat"),
    MISC("\uD83C\uDF6A", "Misc"),
    DONUT("\uD83C\uDF69", "Donut"),
    RENDER("\uD83D\uDC41\uFE0F", "Render"),
    CLIENT("\uD83D\uDCBB", "Client"),
    MOVEMENT("\uD83C\uDFC3", "Movement");

    public final String icon;
    public final String displayName;

    Category(String icon, String displayName) {
        this.icon = icon;
        this.displayName = displayName;
    }
}
