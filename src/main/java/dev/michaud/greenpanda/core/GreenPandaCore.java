package dev.michaud.greenpanda.core;

import dev.michaud.greenpanda.core.commands.GiveItem;
import dev.michaud.greenpanda.core.commands.PlaceBlock;
import dev.michaud.greenpanda.core.commands.TestMobCap;
import dev.michaud.greenpanda.core.eventlistener.ArmorChangeListener;
import dev.michaud.greenpanda.core.eventlistener.BlockUpdateEvent;
import dev.michaud.greenpanda.core.eventlistener.ChunkPopulate;
import dev.michaud.greenpanda.core.eventlistener.PlayerGetItem;
import dev.michaud.greenpanda.core.eventlistener.PlayerGetItemListener;
import dev.michaud.greenpanda.core.eventlistener.PrepareAnvil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * GreenPandaCore is a library that most of the other plugins in the Green/Panda plugin pack rely
 * on. It provides a few useful features, such as custom item interface.
 *
 * @author PandaDev
 */
public final class GreenPandaCore extends JavaPlugin {

  private static GreenPandaCore core;

  /**
   * Get the GreenPandaCore instance.
   *
   * @return The instance.
   */
  public static GreenPandaCore getCore() {
    return core;
  }

  @Override
  public void onEnable() {

    core = this;

    getCommand("giveitem").setExecutor(new GiveItem());
    getCommand("testmobcap").setExecutor(new TestMobCap());
    getCommand("placeblock").setExecutor(new PlaceBlock());

    getServer().getPluginManager().registerEvents(new PrepareAnvil(), this);
    getServer().getPluginManager().registerEvents(new PlayerGetItemListener(), this);
    getServer().getPluginManager().registerEvents(new ChunkPopulate(), this);
    getServer().getPluginManager().registerEvents(new PlayerGetItem(), this);
    getServer().getPluginManager().registerEvents(new BlockUpdateEvent(), this);
    getServer().getPluginManager().registerEvents(new ArmorChangeListener(), this);

    getServer().getConsoleSender()
        .sendMessage(Component.text("[GPCore] Core Enabled").color(NamedTextColor.DARK_GREEN));

  }

  @Override
  public void onDisable() {
    getServer().getConsoleSender()
        .sendMessage(Component.text("[GPCore] Core Disabled").color(NamedTextColor.DARK_RED));
  }

}