package game.helper;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class TestHelper {

    public static String generateRandomString(int len) {
        byte[] array = new byte[len];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    public static int generateRandomNumber (int high) {
        return (int) (Math.random() * high);
    }

  public static String capitalize(String str) {
    if(str == null || str.isEmpty()) {
      return str;
    }

    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
}
