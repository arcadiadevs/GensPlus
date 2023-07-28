package xyz.arcadiadevs.gensplus.utils.formatter;

import java.util.ArrayList;
import java.util.List;

public class Formatter {

  public static <T extends Formattable> String format(T t, String input) {
    for (var entry : t.getPlaceHolders().entrySet()) {
      input = input.replace(entry.getKey(), entry.getValue());
    }

    return input;
  }

  public static <T extends Formattable> List<String> format(T t, List<String> input) {
    List<String> output = new ArrayList<>();

    for (String s : input) {
      output.add(format(t, s));
    }

    return output;
  }
}
