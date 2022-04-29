package game.helper;

import game.packet.AbstractPacket;
import game.server.ServerSettings;

import java.util.Random;

public class MathHelper {


  /**
   * returns the clamped value of val, min and max. This means the following:
   * if val is greater than max it will return max
   * if val is greater than min it will return min
   * if val is neither greater nor smaller it will return val
   */
  public static int clamp(int val, int min, int max) {
    return Math.max(Math.min(max, val), min);
  }


  /**
   * Returns the distance between the points (P(nx, ny) and P(x, y)) by taking the
   * square root of the absolute difference between the two points raised to the m th power where m is a constant
   *
   * @param nx x of the first point
   * @param ny y of the first point
   * @param x  x of the second point
   * @param y  y of the second point
   * @return the calculated distance
   */
  public static double exactDistanceBetweenPoints(int nx, int ny, int x, int y) {
    double m = 2; // Default distance function for m = 2
    // CAN control density, for m > 2 the density shrinks
    return Math.sqrt(Math.pow(Math.abs(nx - x), m) + Math.pow(Math.abs(ny - y), m));
  }


  /**
   * Returns the inverse of an exponential function from the form:
   * a - b ^ (c*d + f)
   *
   * @param max   the y-offset - can be any real value.
   * @param exp   - the base of the exponential. - should be between 0 and 1.
   * @param in    - the value being evaluated. - can be any real value
   * @param fac   - the factor of the value that is being evaluated. - can be any real value.
   * @param shift - the strength of the exponential decay. - should be between 0 and 1
   * @return
   */
  public static double inverseExponential(double max, double exp, double in, double fac, double shift) {
    return max - (Math.pow(exp, in * fac + shift));
  }


  /**
   * @param now current position
   * @param then destination
   * @return the amount of single cell moves that would need to be done to reach a destination
   */
  public static int absoluteCellDistance(int[] now, int[] then) {
    return Math.abs(now[0] - then[0]) + Math.abs(now[1] - then[1]);
  }


  public static boolean isInBounds(int x, int y, int min_x, int max_x, int min_y, int max_y) {
    return x >= min_x && x < max_x && y >= min_y && y < max_y;
  }

  public static boolean isInBounds(int[] xy, int[] minDimension, int[] maxDimension) {
    return isInBounds(xy[0], xy[1], minDimension[0], maxDimension[0], minDimension[1], maxDimension[1]);
  }

  public static boolean isInBounds (int x, int y, ServerSettings serverSettings) {
    return isInBounds(x, y, 0, serverSettings.getMapWidth(), 0,serverSettings.getMapHeight());
  }


  /**
   * Returns a random number between 0 and 1
   *
   * @return the random number as a double
   */
  public static double getRandomNumber() {
    return getRandomNumber((new Random()).nextLong());
  }

  public static double getRandomNumber(long seed) {
    Random random = new Random();
    random.setSeed(seed);
    return random.nextDouble();
  }

}
