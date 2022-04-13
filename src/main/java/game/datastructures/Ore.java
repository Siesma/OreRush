package game.datastructures;

/**
 * This Class represents an ore.
 * IT holds the following information:
 * Type / Value
 * X, Y Coordinates
 */
public class Ore implements GameObject {

  private OreType oreType;
  private  int xCoordinate;
  private int yCoordinate;
  private int amount;

  public Ore (OreType oreType, int amount){
    this.oreType = oreType;
    this.amount = 1; //amount; Currently hardcoded!!
  }

  @Override
  public void setPosition(int x, int y) {
    this.xCoordinate = x;
    this.yCoordinate = y;
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
    String s = "ore:" + oreType.name() + ":" + xCoordinate + ":" + yCoordinate + ":" + amount;
    return s;
  }

  @Override
  public void fillGameObjectWithData(String... data) {
    oreType = OreType.valueOf(data[1]);
    xCoordinate = Integer.parseInt(data[2]);
    yCoordinate = Integer.parseInt(data[3]);
    amount = Integer.parseInt(data[4]);
  }
}
