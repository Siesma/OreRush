package game.datastructures;

/**
 * This Class represents an ore.
 * IT holds the following information:
 * Type / Value
 * X, Y Coordinates
 * the quantity of Ore on the cell
 */
public class Ore implements GameObject {
  private OreType oreType;
  private int xCoordinate;
  private int yCoordinate;
  private int amount;

  public Ore (OreType type, int amount){
    this.amount = 1; //amount; Currently hardcoded!!
  }

  @Override
  public void setPosition(int x, int y) {
    xCoordinate = x;
    yCoordinate = y;
  }

  @Override
  public int[] getPosition() {
    int[] coordinate = new int[2];
    coordinate[0] = yCoordinate;
    coordinate[1] = xCoordinate;
    return coordinate;
  }

  @Override
  public String encodeToString() {
    StringBuilder s = new StringBuilder();
    return s.toString();
  }

  @Override
  public void parseGameObjectFromString(String encodedGameObject) {

  }

  @Override
  public void fillGameObjectWithData(String... data) {

  }


}
