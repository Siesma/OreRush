package game.server;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

/**
 * This class holds the settings for a particular game instance such as game length and map size
 * It can  also be used to set the default values of these parameters.
 */
public class ServerSettings {

  /*
  TODO: Make a new Datatype called "ServerSettings" which holds information about packet-replacement procedure,
   the setting and a way to default back to working values in case something gets messed up.
   */
  public Setting<Integer> numberOfRobots;
  public Setting<Integer> mapWidth, mapHeight;
  public Setting<Integer> numberOfRounds;
  public Setting<Float> oreDensity;
  public Setting<Integer> maxAllowedMoves;
  public Setting<Integer> radarDistance;
  public ArrayList<Object> ores;

  public Setting<Integer> maxClusterSize;
  public Setting<Float> oreThreshold;


  public ServerSettings() {
    setDefaultValues();
  }

  /**
   * @param pathToFile Used  to import settings from a ".ore_game_settings" file
   */
  public ServerSettings(String pathToFile) {
    setDefaultValues();
    // TODO: Make a file system for the settings
  }

  private void setDefaultValues() {
    this.numberOfRobots = new Setting<Integer>("numberOfRobots", 2);
    this.mapWidth = new Setting<Integer>("mapWidth", 30);
    this.mapHeight = new Setting<Integer>("mapHeight", 15);
    this.numberOfRounds = new Setting<Integer>("numberOfRounds", 25);
    this.oreDensity = new Setting<Float>("oreDensity", 1f);
    this.maxAllowedMoves = new Setting<Integer>("maxAllowedMoves", 4);
    this.radarDistance = new Setting<Integer>("radarDistance", 4);
    this.maxClusterSize = new Setting<Integer>("maxClusterSize", 4);
    this.oreThreshold = new Setting<Float>("oreThreshold", 0.75f);
  }

  public void setValue (String variable, Number newValue) {
    String variableName = getSimilarNameToVariable(variable, "", this.getClass().getDeclaredFields());
    Object obj;
    try {
      obj = this.getClass().getField(variableName).get(this);
    } catch (Exception e) {
//      System.out.println("test");
      e.printStackTrace();
      return;
    }
    if(!(obj instanceof Setting)) {
      return;
    }
    Setting<Number> setting = (Setting) obj;
    setting.setVal(newValue);

  }

  private String getSimilarNameToVariable(String similarName, String preFix, Field[] fields) {
    for (Field f : fields) {
      if (f.getName().toLowerCase(Locale.ROOT).matches(preFix + similarName.toLowerCase(Locale.ROOT))) {
        return f.getName();
      }
    }
    return "NONE";
  }

  public Setting<Integer> getNumberOfRobots() {
    return numberOfRobots;
  }

  public ServerSettings setNumberOfRobots(Setting<Integer> numberOfRobots) {
    this.numberOfRobots = numberOfRobots;
    return this;
  }

  public Setting<Integer> getMapWidth() {
    return mapWidth;
  }

  public ServerSettings setMapWidth(Setting<Integer> mapWidth) {
    this.mapWidth = mapWidth;
    return this;
  }

  public Setting<Integer> getMapHeight() {
    return mapHeight;
  }

  public ServerSettings setMapHeight(Setting<Integer> mapHeight) {
    this.mapHeight = mapHeight;
    return this;
  }

  public Setting<Integer> getNumberOfRounds() {
    return numberOfRounds;
  }

  public ServerSettings setNumberOfRounds(Setting<Integer> numberOfRounds) {
    this.numberOfRounds = numberOfRounds;
    return this;
  }

  public Setting<Float> getOreDensity() {
    return oreDensity;
  }

  public ServerSettings setOreDensity(Setting<Float> oreDensity) {
    this.oreDensity = oreDensity;
    return this;
  }

  public Setting<Integer> getMaxAllowedMoves() {
    return maxAllowedMoves;
  }

  public ServerSettings setMaxAllowedMoves(Setting<Integer> maxAllowedMoves) {
    this.maxAllowedMoves = maxAllowedMoves;
    return this;
  }

  public Setting<Integer> getRadarDistance() {
    return radarDistance;
  }

  public ServerSettings setRadarDistance(Setting<Integer> radarDistance) {
    this.radarDistance = radarDistance;
    return this;
  }

  public ArrayList<Object> getOres() {
    return ores;
  }

  public ServerSettings setOres(ArrayList<Object> ores) {
    this.ores = ores;
    return this;
  }

  public Setting<Integer> getMaxClusterSize() {
    return maxClusterSize;
  }

  public ServerSettings setMaxClusterSize(Setting<Integer> maxClusterSize) {
    this.maxClusterSize = maxClusterSize;
    return this;
  }

  public Setting<Float> getOreThreshold() {
    return oreThreshold;
  }

  public ServerSettings setOreThreshold(Setting<Float> oreThreshold) {
    this.oreThreshold = oreThreshold;
    return this;
  }
}
