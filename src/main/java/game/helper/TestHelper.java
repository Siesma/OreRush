package game.helper;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * This class is to alleviate some calls in the Unit Tests.
 * It will only handle creating random stuff.
 */
public class TestHelper {

  /**
   * Generates a random sting of given length
   * @param len the length of the final string
   * @return a String of the length "len" containing random characters only
   */
  public static String generateRandomString(int len) {
    byte[] array = new byte[len];
    new Random().nextBytes(array);
    return new String(array, StandardCharsets.UTF_8);
  }

  /**
   * Generates a random number between 0 and a given int
   * @param high the maximum allowed value for the random number to be
   * @return a random number between 0 and high
   */
  public static int generateRandomNumber(int high) {
    return (int) (Math.random() * high);
  }


  /**
   * Capitalizes the first character of a given String
   * @param str the String that shall be capitalized
   * @return the same String but the first letter is capitalized
   */
  public static String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }

    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
}
