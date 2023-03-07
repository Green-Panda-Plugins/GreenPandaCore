package dev.michaud.greenpanda.core.math;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

public class Vector2 implements Serializable, ConfigurationSerializable {

  public float x;
  public float y;

  public static final Vector2 ZERO = new Vector2(0, 0);
  public static final Vector2 ONE = new Vector2(1, 1);
  public static final Vector2 UP = new Vector2(0, 1);
  public static final Vector2 DOWN = new Vector2(0, -1);
  public static final Vector2 LEFT = new Vector2(-1, 0);
  public static final Vector2 RIGHT = new Vector2(1, 0);

  public Vector2() {
    this(0, 0);
  }

  public Vector2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vector2(Vector2 other) {
    this(other.x, other.y);
  }

  @Override
  public String toString() {
    return "Vector2{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vector2 vector2 = (Vector2) o;
    return Float.compare(vector2.x, x) == 0 && Float.compare(vector2.y, y) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(x, y);
  }

  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.of("x", x, "y", y);
  }

  /**
   * Sets the length to 1, but keeps the direction.
   * @return The normalized vector.
   */
  public Vector2 normalize() {
    double length = Math.sqrt(x*x + y*y);

    if (length != 0) {
      float s = 1 / (float) length;
      x *= s;
      y *= s;
    }

    return this;
  }

  /**
   * Calculates the distance between two vectors.
   *
   * @param a The first vector.
   * @param b The second vector.
   * @return The distance between the two vectors.
   */
  public static double distance(Vector2 a, Vector2 b) {
    float v0 = b.x - a.x;
    float v1 = b.y - a.y;

    return Math.sqrt(v0*v0 + v1*v1);
  }

}