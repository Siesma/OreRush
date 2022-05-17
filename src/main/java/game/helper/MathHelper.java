package game.helper;

import game.server.ServerSettings;

import java.awt.*;
import java.util.Random;

/**
 * This class is a collection of all the mathematical functions.
 */
public class MathHelper {


  /**
   * returns one of the following variables
   * @param val if between min and max
   * @param min if val under min
   * @param max if val over max
   * @return the clamped value of val, min and max
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
   * @param serverSettings gets the max allowed moved for this game
   * @param position is the position of the Robot in question that tries to move.
   * @param destination is the Position to which the robot in question wants to move to. Expecteto be two integers.
   * @return the next Position that is within the reach of the robots original position without moving more than allowed.
   */
  public static int[] getNextMove(int[] position, int[] destination, ServerSettings serverSettings) {
    int xDif = destination[0] - position[0];
    int yDif = destination[1] - position[1];
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
   * @return inverse of an exponential function
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
   * @return  the exponential function
   */
  public static double exponential(double max, double exp, double in, double fac, double shift) {
    max = -1*max;
    exp = -1*max;
    return inverseExponential(max, exp, in, fac, shift);
  }


  /**
   * @param now  current position
   * @param then destination
   * @return the amount of single cell moves that would need to be done to reach a destination
   */
  public static int absoluteCellDistance(int[] now, int[] then) {
    return Math.abs(now[0] - then[0]) + Math.abs(now[1] - then[1]);
  }


  /**
   * Checks whether the x, y pair is within the bounds
   * @param x the x coordinate that is being evaluated
   * @param y the y coordinate that is being evaluated
   * @param min_x the value that the x value is not allowed to be lower than
   * @param max_x the value that the x value is not allowed to be greater than
   * @param min_y the value that the y value is not allowed to be lower than
   * @param max_y the value that the y value is not allowed to be greater than
   * @return whether the x, y pair is within the bounds
   */
  public static boolean isInBounds(int x, int y, int min_x, int max_x, int min_y, int max_y) {
    return x >= min_x && x < max_x && y >= min_y && y < max_y;
  }

  /**
   * Checks whether the x, y pair is within the bounds
   * An overloaded function to make accessing it easier when given the arrays of the parameters
   * @param xy array containing the x (at pos 0) and y (at pos 1) coordinates
   * @param minDimension array containing the minimum x (at pos 0) and y (at pos 1) dimensions
   * @param maxDimension array containing the maximum x (at pos 0) and y (at pos 1) dimensions
   * @return whether the x, y pair is within the bounds
   */
  public static boolean isInBounds(int[] xy, int[] minDimension, int[] maxDimension) {
    return isInBounds(xy[0], xy[1], minDimension[0], maxDimension[0], minDimension[1], maxDimension[1]);
  }

  /**
   * Checks whether the x, y pair is within the bounds
   * An overloaded function to make accessing it easier when given the settings of the game
   * @param x the x coordinate that is being evaluated
   * @param y the y coordinate that is being evaluated
   * @param serverSettings the settings of the game
   * @return
   */
  public static boolean isInBounds (int x, int y, ServerSettings serverSettings) {
    return isInBounds(x, y, 0, serverSettings.getMapWidth(), 0,serverSettings.getMapHeight());
  }

  /**
   * Helper function.
   * Calls the getRandomColours of ColourUtils.
   * It generates a number of colours, making sure that they are diffrent enough.
   * @param num the amount of colours that are needed.
   * @return a random colour that is different from all the other already used colours.
   */
  public static Color[] getRandomColours (int num) {
    return ColourUtils.getRandomColours(num);
  }


  /**
   * Returns a random number between 0 and 1
   *
   * @return the random number as a double
   */
  public static double getRandomNumber() {
    return getRandomNumber((new Random()).nextLong());
  }

  /**
   * Generates a random Double based on some seed
   * @param seed seed for the random generation
   * @return the random double
   */
  public static double getRandomNumber(long seed) {
    Random random = new Random();
    random.setSeed(seed);
    return random.nextDouble();
  }

}
