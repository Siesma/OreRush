package game.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class holds the settings for a particular game instance such as game length and map size
 * It can  also be used to set the default values of these parameters.
 */
public class ServerSettings {

  public static final Logger logger = LogManager.getLogger(ServerSettings.class);
  /**
   *
   */
  public Setting<Integer> numberOfRobots;
  /**
   *
   */
  public Setting<Integer> mapWidth, mapHeight;
  /**
   *
   */
  public Setting<Integer> numberOfRounds;
  /**
   *
   */
  public Setting<Float> oreDensity;
  /**
   *
   */
  public Setting<Integer> maxAllowedMoves;
  /**
   *
   */
  public Setting<Integer> radarDistance;
  /**
   *
   */
  public Setting<Integer> maxClusterSize;
  /**
   *
   */
  public Setting<Float> oreThreshold;


  public ServerSettings() {
    setDefaultValues();
  }

  /**
   * sets the default values for all the settings
   */
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

  /**
   * sets a field to a new value based on the name of the field.
   * @param variable the name of the variable that has to be changed
   * @param newValue the Value to which the variable has to be changed to
   */
  public void setValue(String variable, Number newValue) {
    String variableName = getSimilarNameToVariable(variable, "", this.getClass().getDeclaredFields());
    Object obj;
    try {
      obj = this.getClass().getField(variableName).get(this);
    } catch (Exception e) {
      logger.error("no field by the name \"" + variable + "\" exists");
      return;
    }
    if (!(obj instanceof Setting)) {
      return;
    }
    Setting setting = (Setting) obj;
    if(setting.getVal() instanceof Integer) {
      setting.setVal(newValue.intValue());
    } else if(setting.getVal() instanceof Float) {
      setting.setVal(newValue.floatValue());
    } else if(setting.getVal() instanceof Double) {
      setting.setVal(newValue.doubleValue());
    }

  }

  /**
   * matches any case to camelCase for all the fields
   * @param similarName the variable name in any case matched to camelCase
   * @param preFix whether the variable has a prefix or not (not used in here)
   * @param fields all the fields where the variable name should be searched
   * @return the camelCase of a variableName
   */
  private String getSimilarNameToVariable(String similarName, String preFix, Field[] fields) {
    for (Field f : fields) {
      if (f.getName().toLowerCase(Locale.ROOT).matches(preFix + similarName.toLowerCase(Locale.ROOT))) {
        return f.getName();
      }
    }
    return "NONE";
  }

  /**
   *
   * @return the number of robots for the current game map
   */
  public int getNumberOfRobots() {
    return numberOfRobots.getVal();
  }

  /**
   *
   * @return the width of the map
   */
  public int getMapWidth() {
    return mapWidth.getVal();
  }

  /**
   *
   * @return the height of the map
   */
  public int getMapHeight() {
    return mapHeight.getVal();
  }

  /**
   *
   * @return the number of rounds the game should play
   */
  public int getNumberOfRounds() {
    return numberOfRounds.getVal();
  }

  /**
   *
   * @return the density of the ores
   */
  public float getOreDensity() {
    return oreDensity.getVal();
  }

  /**
   *
   * @return the maximum amount of moves allowed for each robot for one turn
   */
  public int getMaxAllowedMoves() {
    return maxAllowedMoves.getVal();
  }

  /**
   *
   * @return the distance of the radars reveal area
   */
  public int getRadarDistance() {
    return radarDistance.getVal();
  }

  /**
   *
    * @return the maximum size of a cluster
   */
  public int getMaxClusterSize() {
    return maxClusterSize.getVal();
  }

  /**
   *
   * @return the threshold for generating an ore
   */
  public float getOreThreshold() {
    return oreThreshold.getVal();
  }
}
