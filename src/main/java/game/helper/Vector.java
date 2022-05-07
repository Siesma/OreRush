package game.helper;

public class Vector implements Comparable<Vector> {
  int x, y;

  public Vector(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  static int getDistance(Vector a, Vector b) {
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
  }


  public void add(Vector b) {
    this.x += b.x;
    this.y += b.y;
  }

  public int[] toArray () {
    return new int[] {this.x, this.y};
  }


  @Override
  public String toString() {
    return x + " " + y;
  }

  @Override
  public int compareTo(Vector o) {
    return this.getX() == o.getX() && this.getY() == o.getY() ? 1 : 0;
  }
}