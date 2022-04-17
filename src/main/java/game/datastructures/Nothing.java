package game.datastructures;

public class Nothing implements GameObject {

  private int x;
  private int y;
  private int id;
  @Override
  public void setID(int ID) {
    this.id = id;
  }

  @Override
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int[] getPosition() {
    return new int[]{x, y};
  }

  @Override
  public String encodeToString() {
    return "Nothing:" + this.id;
  }

  @Override
  public void fillGameObjectWithData(String... data) {

  }
}
