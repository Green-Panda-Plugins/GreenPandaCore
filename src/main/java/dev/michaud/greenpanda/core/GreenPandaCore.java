package dev.michaud.greenpanda.core;

import dev.michaud.greenpanda.core.commands.GiveItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class GreenPandaCore extends JavaPlugin {

  @Override
  public void onEnable() {

    getCommand("giveitem").setExecutor(new GiveItem());

    getServer().getConsoleSender().sendMessage(
        Component.text("[GPCore] Core Enabled").color(NamedTextColor.DARK_GREEN));

  }

  @Override
  public void onDisable() {
    getServer().getConsoleSender().sendMessage(
        Component.text("[GPCore] Core Disabled").color(NamedTextColor.DARK_RED));
  }

}