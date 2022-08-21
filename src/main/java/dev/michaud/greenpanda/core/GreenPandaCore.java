package dev.michaud.greenpanda.core;

import dev.michaud.greenpanda.core.commands.GiveItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class GreenPandaCore extends JavaPlugin {

  private static GreenPandaCore core;

  public static GreenPandaCore getCore() {
    if (core == null) {
      System.out.println("The core is null. I dunno why, but it is. It should have been set in "
          + "onEnable() but it wasn't because it's stinky.");
    }
    return core;
  }

  @Override
  public void onEnable() {

    core = this;

    getCommand("giveitem").setExecutor(new GiveItem());

    getServer().getConsoleSender()
        .sendMessage(Component.text("[GPCore] Core Enabled").color(NamedTextColor.DARK_GREEN));

  }

  @Override
  public void onDisable() {
    getServer().getConsoleSender()
        .sendMessage(Component.text("[GPCore] Core Disabled").color(NamedTextColor.DARK_RED));
  }

}