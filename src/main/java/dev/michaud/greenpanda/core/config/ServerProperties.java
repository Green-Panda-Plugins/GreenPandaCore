package dev.michaud.greenpanda.core.config;

import dev.michaud.greenpanda.core.GreenPandaCore;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerProperties {

  /**
   * Gets the default spawn location of the server.
   *
   * @return The default spawn location of the server.
   * @throws NullPointerException If the server has no default world.
   */
  public static @NotNull Location getDefaultWorldSpawn() {

    World world = getDefaultWorld();

    if (world == null) {
      throw new NullPointerException("The server has no default world.");
    }

    return getDefaultWorld().getSpawnLocation();

  }

  /**
   * Gets the default world of the server from the server.properties file. If the world name
   * couldn't be found for whatever reason then a fallback world is chosen
   * <code>server.getWorlds().get(0)</code>, which in most cases should be the same unless the
   * server has multiple worlds (such as when using Multiverse).
   *
   * @return The default world of the server.
   */
  public static @Nullable World getDefaultWorld() {

    Server server = GreenPandaCore.getCore().getServer();
    World fallback = server.getWorlds().get(0);

    String levelName = ServerProperties.getString("level-name");

    if (levelName == null) {
      return fallback;
    }

    World world = server.getWorld(levelName);

    if (world == null) {
      return fallback;
    }

    return world;
  }

  /**
   * Gets a string value from the server.properties file.
   *
   * @param key The key to search for.
   * @return The value of the key or null if the key was not found.
   */
  @Nullable
  public static String getString(@NotNull String key) {

    try {

      BufferedReader reader = new BufferedReader(new FileReader("server.properties"));
      Properties properties = new Properties();

      properties.load(reader);
      reader.close();

      return properties.getProperty(key);

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

}