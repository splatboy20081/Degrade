package xyz.elevated.frequency.wrapper;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.v1_8_R3.Packet;

public abstract class PacketWrapper {
  private final Map<String, Field> fields = new WeakHashMap<>();

  private final Packet<?> instance;
  private final Class<? extends Packet<?>> clazz;

  public PacketWrapper(Packet<?> instance, Class<? extends Packet<?>> clazz) {
    Field[] declaredFields = clazz.getDeclaredFields();

    // Loop around all the declared fields and make them accessible
    for (Field declaredField : declaredFields) {
      String fieldName = declaredField.getName();

      declaredField.setAccessible(true);
      fields.put(fieldName, declaredField);
    }

    // Set the clazz from constructor
    this.instance = instance;
    this.clazz = clazz;
  }

  /**
   * @param name - The name of the field you want to get
   * @param <T> - The data-type
   * @return - The value (possibly null) that we got from the field.
   */
  public <T> T get(String name) {
    try {
      //noinspection unchecked
      return (T) fields.get(name).get(instance);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * @param name - The name of the field you want to edit.
   * @param alteration - The new object to set in the field.
   */
  public void set(String name, Object alteration) {
    try {
      fields.get(name).set(clazz, alteration);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
