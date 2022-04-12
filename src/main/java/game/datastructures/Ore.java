package game.datastructures;

/**
 * This Class represents an ore.
 * IT holds the following information:
 * Type / Value
 * X, Y Coordinates
 */
public class Ore implements GameObject {

  private OreType oreType;
  private int amount;

  public Ore (OreType oreType, int amount){
    this.oreType = oreType;
    this.amount = 1; //amount; Currently hardcoded!!
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
