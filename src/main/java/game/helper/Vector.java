package game.helper;

public class Vector implements Comparable<Vector> {
  int x, y;

  /**
   * Constructor of the Vector
   * @param x the x coordinate of the vector
   * @param y the y coordinate of the vector
   */
  public Vector(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Getter of the X Coordinate
   * @return the x Coordinate of the Vector
   */
  public int getX() {
    return x;
  }

  /**
   * Getter of the y Coordinate
   * @return the y Coordinate of the Vector
   */
  public int getY() {
    return y;
  }

  /**
   * Calculates the distance between two vectors
   * @param a first vector
   * @param b second vector
   * @return the distance between the vectors
   */
  static int getDistance(Vector a, Vector b) {
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
  }

  /**
   * Add some Vector to this Vector
   * @param b the vector to be added
   */
  public void add(Vector b) {
    this.x += b.x;
    this.y += b.y;
  }

  /**
   * Transforms the Vector into an Array
   * @return the array with the x and y coordinates [x, y]
   */
  public int[] toArray () {
    return new int[] {this.x, this.y};
  }

  /**
   * Trandforms the Vector int a String
   * @return the Vector as a String 'x y'
   */
  @Override
  public String toString() {
    return x + " " + y;
  }

  /**
   * Compare some other Vector with this one
   * @param o the other Vector
   * @return 1 if the x and y coordinates of the vectors match, else returns 0
   */
  @Override
  public int compareTo(Vector o) {
    return this.getX() == o.getX() && this.getY() == o.getY() ? 1 : 0;
  }
}