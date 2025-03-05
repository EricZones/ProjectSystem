package de.ericzones.projectsystem.collectives.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum PlayerGroup {

    ADMIN("§4", NamedTextColor.DARK_RED, Component.text("Admin ").color(NamedTextColor.DARK_RED)
            .append(Component.text("• ").color(NamedTextColor.DARK_GRAY)),
            "§4Admin §8• ", "Admin"),

    SEASON1("§c", NamedTextColor.RED, Component.text("I ").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
                    .append(Component.text("• ").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, false)),
            "§c§lI §8• ", "Season 1"),

    SEASON2("§b", NamedTextColor.AQUA, Component.text("II ").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
            .append(Component.text("• ").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, false)),
            "§b§lII §8• ", "Season 2"),

    SEASON3("§e", NamedTextColor.YELLOW, Component.text("III ").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)
            .append(Component.text("• ").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, false)),
            "§e§lIII §8• ", "Season 3"),

    PLAYER("§7", NamedTextColor.GRAY, Component.text("? ").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD)
            .append(Component.text("• ").color(NamedTextColor.DARK_GRAY).decoration(TextDecoration.BOLD, false)),
            "§7§l? §8• ", "Player");

    private final String colorCode;
    private final NamedTextColor nameColor;
    private final Component prefix;
    private final String colorPrefix;
    private final String name;

    PlayerGroup(String colorCode, NamedTextColor nameColor, Component prefix, String colorPrefix, String name) {
        this.colorCode = colorCode;
        this.nameColor = nameColor;
        this.prefix = prefix;
        this.colorPrefix = colorPrefix;
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public NamedTextColor getNameColor() {
        return nameColor;
    }

    public Component getPrefix() {
        return prefix;
    }

    public String getColorPrefix() {
        return colorPrefix;
    }

    public String getName() {
        return name;
    }
}
