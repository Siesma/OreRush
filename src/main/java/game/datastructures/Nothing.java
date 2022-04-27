package game.datastructures;

public class Nothing implements GameObject {

  private int x;
  private int y;
  private int id;
  private String owner;

  @Override
  public void setID(int id) {
    this.id = id;
  }

  @Override
  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int[] getPosition() {
    return new int[] {x, y};
  }

  @Override
  public String encodeToString() {
    return "Nothing:" + id;
  }

  @Override
  public void fillGameObjectWithData(String... data) {

  }

  /**
   * Sets the owner of this dataType
   * @param nameOfOwner the name of the player creating this gameObject.
   */
  @Override
  public void setOwner(String nameOfOwner) {
    this.owner = nameOfOwner;
  }


}
