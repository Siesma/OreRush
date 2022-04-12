package game.datastructures;

/**
 * This Class represents a radar.
 * IT holds the following information:
 * X, Y Coordinates
 * Player who placed the radar
 */
public class Radar implements GameObject {

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
