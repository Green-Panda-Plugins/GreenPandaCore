package me.greenpanda.core;

import me.greenpanda.core.commands.GiveItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class GreenPandaCore extends JavaPlugin {

  private static GreenPandaCore plugin;

  public static GreenPandaCore getCore() {
    return plugin;
  }

  @Override
  public void onEnable() {
    plugin = this;

    getCommand("giveitem").setExecutor(new GiveItem());

    getServer().getConsoleSender().sendMessage(
        Component.text("[GreenPanda] Core Enabled").color(NamedTextColor.DARK_GREEN));

  }

  @Override
  public void onDisable() {
    getServer().getConsoleSender().sendMessage(
        Component.text("[GreenPanda] Core Disabled").color(NamedTextColor.DARK_RED));
  }

}