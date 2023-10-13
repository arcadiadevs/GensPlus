package xyz.arcadiadevs.gensplus.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

public class ActionBarUtil {
  private static boolean works = true;

  private static final String version = ServerVersion.getServerVersionString();

  private static final boolean useOldMethods =
      version.equalsIgnoreCase("v1_8_R1") || version.startsWith("1_7_");

  public static void sendActionBar(Player player, String message) {
    if (!player.isOnline()) {
      return;
    }
    if (version.startsWith("v1_12_")) {
      sendActionBarPost112(player, message);
    } else {
      sendActionBarPre112(player, message);
    }
  }

  private static void sendActionBarPost112(Player player, String message) {
    if (!player.isOnline()) {
      return;
    }

    try {
      Class<?> craftPlayerClass =
          Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
      Object craftPlayer = craftPlayerClass.cast(player);
      Class<?> c4 = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
      Class<?> c5 = Class.forName("net.minecraft.server." + version + ".Packet");
      Class<?> c2 = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
      Class<?> c3 = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
      Class<?> chatMessageTypeClass =
          Class.forName("net.minecraft.server." + version + ".ChatMessageType");
      Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
      Object chatMessageType = null;
      byte b;
      int i;
      Object[] arrayOfObject1;
      for (i = (arrayOfObject1 = chatMessageTypes).length, b = 0; b < i; ) {
        Object obj = arrayOfObject1[b];
        if (obj.toString().equals("GAME_INFO")) {
          chatMessageType = obj;
        }
        b++;
      }
      Object o = c2.getConstructor(new Class[] {String.class}).newInstance(message);
      Object ppoc = c4.getConstructor(new Class[] {c3, chatMessageTypeClass}).newInstance(o,
          chatMessageType);
      Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
      Object h = m1.invoke(craftPlayer);
      Field f1 = h.getClass().getDeclaredField("playerConnection");
      Object pc = f1.get(h);
      Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
      m5.invoke(pc, ppoc);
    } catch (Exception ex) {
      ex.printStackTrace();
      works = false;
    }
  }

  private static void sendActionBarPre112(Player player, String message) {
    if (!player.isOnline()) {
      return;
    }

    try {
      Object ppoc;
      Class<?> craftPlayerClass =
          Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
      Object craftPlayer = craftPlayerClass.cast(player);
      Class<?> c4 = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
      Class<?> c5 = Class.forName("net.minecraft.server." + version + ".Packet");
      if (useOldMethods) {
        Class<?> c2 = Class.forName("net.minecraft.server." + version + ".ChatSerializer");
        Class<?> c3 = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
        Method m3 = c2.getDeclaredMethod("a", String.class);
        Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
        ppoc = c4.getConstructor(new Class[] {c3, byte.class}).newInstance(cbc,
            (byte) 2);
      } else {
        Class<?> c2 = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
        Class<?> c3 = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
        Object o = c2.getConstructor(new Class[] {String.class}).newInstance(message);
        ppoc = c4.getConstructor(new Class[] {c3, byte.class}).newInstance(o,
            (byte) 2);
      }
      Method m1 = craftPlayerClass.getDeclaredMethod("getHandle");
      Object h = m1.invoke(craftPlayer);
      Field f1 = h.getClass().getDeclaredField("playerConnection");
      Object pc = f1.get(h);
      Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
      m5.invoke(pc, ppoc);
    } catch (Exception ex) {
      ex.printStackTrace();
      works = false;
    }
  }
}
