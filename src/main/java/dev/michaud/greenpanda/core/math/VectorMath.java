package dev.michaud.greenpanda.core.math;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class VectorMath {

  /**
   * Clamps an angle to 0-360 degrees, with overflow retaining the correct direction (e.g. -90
   * becomes 270, 370 becomes 10).
   *
   * @param angle The angle to clamp.
   * @return A clamped angle.
   */
  public static float clampAngle(float angle) {

    angle %= 360;

    if (angle < 0) {
      angle = 360 - angle;
    }

    return angle;

  }

  /**
   * Creates a Vector (3d) using the pitch and yaw.
   *
   * @param pitch The pitch.
   * @param yaw   The yaw.
   * @return A rotation vector.
   */
  @Contract("_, _ -> new")
  public static @NotNull Vector getVectorForRotation(float pitch, float yaw) {

    float f = (float) Math.cos(-yaw * 0.017453292F - (float) Math.PI);
    float f1 = (float) Math.sin(-yaw * 0.017453292F - (float) Math.PI);
    float f2 = (float) Math.cos(-pitch * 0.017453292F);
    float f3 = (float) Math.sin(-pitch * 0.017453292F);

    return new Vector(f1 * f2, f3, f * f2);

  }

}