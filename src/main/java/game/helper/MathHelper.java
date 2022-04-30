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
   * Returns the position the robot is allowed to move to relative to the maximum allowed moves.
   * X-Coordinate will be prioritised if the wanted destination is not a valid move.
   * <p>
   * This function can be used to not have to validate moves as every invalid move will automatically will be
   * cropped down.
   *
   * @param position is the position of the Robot in question that tries to move.
   * @param destination is the Position to which the robot in question wants to move to. Expecteto be two integers.
   * @return the next Position that is within the reach of the robots original position without moving more than allowed.
   */
  public static int[] getNextMove(int[] position, int[] destination, ServerSettings serverSettings) {
    int xDif = position[0] + destination[0];
    int yDif = position[1] + destination[1];
    int maxTotalMoves = serverSettings.getMaxAllowedMoves();
    int xMoves = MathHelper.clamp(xDif, -maxTotalMoves, maxTotalMoves);
    maxTotalMoves -= Math.abs(xMoves);
    int yMoves = MathHelper.clamp(yDif, -maxTotalMoves, maxTotalMoves);
    return new int[]{position[0] + xMoves, position[1] + yMoves};

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
   * Returns the exponential function by calling the inverseExponential and inverting the max and exp.
   *
   * @param max   the y-offset - can be any real value.
   * @param exp   - the base of the exponential. - should be between 0 and 1.
   * @param in    - the value being evaluated. - can be any real value
   * @param fac   - the factor of the value that is being evaluated. - can be any real value.
   * @param shift - the strength of the exponential decay. - should be between 0 and 1
   * @return
   */
  public static double exponential(double max, double exp, double in, double fac, double shift) {
    max = -1*max;
    exp = -1*max;
    return inverseExponential(max, exp, in, fac, shift);
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
