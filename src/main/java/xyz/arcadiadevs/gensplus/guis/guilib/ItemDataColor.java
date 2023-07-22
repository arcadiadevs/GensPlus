package xyz.arcadiadevs.gensplus.guis.guilib;

public enum ItemDataColor {
  WHITE((short)0),
  ORANGE((short)1),
  MAGENTA((short)2),
  LIGHT_BLUE((short)3),
  YELLOW((short)4),
  LIME((short)5),
  PINK((short)6),
  GRAY((short)7),
  LIGHT_GRAY((short)8),
  CYAN((short)9),
  PURPLE((short)10),
  BLUE((short)11),
  BROWN((short)12),
  GREEN((short)13),
  RED((short)14),
  BLACK((short)15);

  private final short value;

  private ItemDataColor(short value) {
    this.value = value;
  }

  public short getValue() {
    return this.value;
  }

  public static ItemDataColor getByValue(short value) {
    ItemDataColor[] var1 = values();
    int var2 = var1.length;

    for(int var3 = 0; var3 < var2; ++var3) {
      ItemDataColor color = var1[var3];
      if (value == color.value) {
        return color;
      }
    }

    return null;
  }
}
