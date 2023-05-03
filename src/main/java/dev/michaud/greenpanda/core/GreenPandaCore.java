package dev.michaud.greenpanda.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.michaud.greenpanda.core.blocks.CustomBlockRegistry;
import dev.michaud.greenpanda.core.blocks.GPBlockPacketAdapter;
import dev.michaud.greenpanda.core.blocks.TestBlock;
import dev.michaud.greenpanda.core.commands.GiveItem;
import dev.michaud.greenpanda.core.commands.ItemMenu;
import dev.michaud.greenpanda.core.commands.PlaceBlock;
import dev.michaud.greenpanda.core.commands.TestMobCap;
import dev.michaud.greenpanda.core.eventlistener.ArmorChangeListener;
import dev.michaud.greenpanda.core.eventlistener.BlockBreak;
import dev.michaud.greenpanda.core.eventlistener.BlockPlace;
import dev.michaud.greenpanda.core.eventlistener.ChunkPopulate;
import dev.michaud.greenpanda.core.eventlistener.ItemMenuListener;
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
    getCommand("itemmenu").setExecutor(new ItemMenu());

    getServer().getPluginManager().registerEvents(new PrepareAnvil(), this);
    getServer().getPluginManager().registerEvents(new PlayerGetItemListener(), this);
    getServer().getPluginManager().registerEvents(new ChunkPopulate(), this);
    getServer().getPluginManager().registerEvents(new PlayerGetItem(), this);
    getServer().getPluginManager().registerEvents(new BlockBreak(), this);
    getServer().getPluginManager().registerEvents(new ArmorChangeListener(), this);
    getServer().getPluginManager().registerEvents(new ItemMenuListener(), this);
    getServer().getPluginManager().registerEvents(new BlockPlace(), this);

    //Blocks & Items
    CustomBlockRegistry.register(TestBlock.class);

    //ProtocolLib
    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    manager.addPacketListener(new GPBlockPacketAdapter(this));

    getServer().getConsoleSender()
        .sendMessage(Component.text("[GPCore] Core Enabled").color(NamedTextColor.DARK_GREEN));

  }

  @Override
  public void onDisable() {
    getServer().getConsoleSender()
        .sendMessage(Component.text("[GPCore] Core Disabled").color(NamedTextColor.DARK_RED));
  }

}