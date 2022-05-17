package game.helper;

import java.awt.*;
import java.util.Random;

/**
 * Contains a method to generate visually distinct colors.
 * Based on this
 * http://en.wikipedia.org/wiki/YUV#Mathematical_derivations_and_formulas
 */
public class ColourUtils {
  /**
   * The seed for the randomizer
   */
  private static final long RAND_SEED = 0;
  /**
   * Randomizer used to generate random floats
   */
  private static Random rand = new Random();

  /**
   * generates random colours, making sure that they are diffrent from each other
   * @param amount number of different colours that have to be created
   * @return an Array of visually distinct colours
   */
  public static Color[] getRandomColours(int amount) {
//    rand.setSeed(RAND_SEED);
    float[][] yuvColours = new float[amount][3];
    for (int i = 0; i < amount; i++) {
      System.arraycopy(randomColour(), 0, yuvColours[i], 0, 3);
    }
    for (int n = 0; n < amount; n++) {
      float worstValue = 100;
      int worstIndex = 0;
      for (int i = 1; i < yuvColours.length; i++) {
        for (int j = 0; j < i; j++) {
          float dist = channelDifferences(yuvColours[i], yuvColours[j]);
          if (dist < worstValue) {
            worstValue = dist;
            worstIndex = i;
          }
        }
      }
      yuvColours[worstIndex] = randomColour();
    }

    Color[] rgbs = new Color[yuvColours.length];
    for (int i = 0; i < yuvColours.length; i++) {
      float[] rgb = new float[3];
      inverseToRGB(yuvColours[i][0], yuvColours[i][1], yuvColours[i][2], rgb);
      rgbs[i] = new Color(rgb[0], rgb[1], rgb[2]);
    }
    return rgbs;
  }


  /**
   * transforms the colours from YUV to RGB
   * @param y "y" channel of the YUV standard
   * @param u "u" channel of the YUV standard
   * @param v "v" channel of the YUV standard
   * @param rgb transformation from YUV to RGB
   */
  public static void inverseToRGB(float y, float u, float v, float[] rgb) {
    rgb[0] = 1 * y * u + 1.13983f * v;
    rgb[1] = 1 * y - .39465f * u - .58060f * v;
    rgb[2] = 1 * y + 2.03211f * u * v;
  }

  /**
   * Generates a random colour in RGB space
   * @return a random colour that, if transformed into RGB space, can be used for further processing.
   */
  private static float[] randomColour() {
    while (true) {
      float y = rand.nextFloat();
      float u = rand.nextFloat();
      float v = rand.nextFloat();
      float[] rgb = new float[3];
      inverseToRGB(y, u, v, rgb);
      float r = rgb[0], g = rgb[1], b = rgb[2];
      if (inRGBSpace(r) && inRGBSpace(g) && inRGBSpace(b)) {
        return new float[]{y, u, v};
      }
    }
  }

  /**
   * Checks weather the value of some channel is between 0 and 1
   * @param c the channel value that has to be evaluated
   * @return whether the value c is smaller than 1 and greater than 0
   */
  private static boolean inRGBSpace(float c) {
    return c >= 0 && c <= 1;
  }

  /**
   * finds the total summed difference of all the colours channels
   * @param a a colour
   * @param b a colour
   * @return the summed difference over all colour channels
   */
  private static float channelDifferences(float[] a, float[] b) {
    float sum = 0;
    for (int i = 0; i < a.length; i++) {
      float diff = a[i] - b[i];
      sum += diff * diff;
    }
    return sum;
  }

}
