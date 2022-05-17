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

  public void setValue(String variable, Number newValue) {
    String variableName = getSimilarNameToVariable(variable, "", this.getClass().getDeclaredFields());
    Object obj;
    try {
      obj = this.getClass().getField(variableName).get(this);
    } catch (Exception e) {
      logger.error(e.getMessage());
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

  private String getSimilarNameToVariable(String similarName, String preFix, Field[] fields) {
    for (Field f : fields) {
      if (f.getName().toLowerCase(Locale.ROOT).matches(preFix + similarName.toLowerCase(Locale.ROOT))) {
        return f.getName();
      }
    }
    return "NONE";
  }

  public int getNumberOfRobots() {
    return numberOfRobots.getVal();
  }

  public ServerSettings setNumberOfRobots(Setting<Integer> numberOfRobots) {
    this.numberOfRobots = numberOfRobots;
    return this;
  }

  public int getMapWidth() {
    return mapWidth.getVal();
  }

  public ServerSettings setMapWidth(Setting<Integer> mapWidth) {
    this.mapWidth = mapWidth;
    return this;
  }

  public int getMapHeight() {
    return mapHeight.getVal();
  }

  public ServerSettings setMapHeight(Setting<Integer> mapHeight) {
    this.mapHeight = mapHeight;
    return this;
  }

  public int getNumberOfRounds() {
    return numberOfRounds.getVal();
  }

  public ServerSettings setNumberOfRounds(Setting<Integer> numberOfRounds) {
    this.numberOfRounds = numberOfRounds;
    return this;
  }

  public float getOreDensity() {
    return oreDensity.getVal();
  }

  public ServerSettings setOreDensity(Setting<Float> oreDensity) {
    this.oreDensity = oreDensity;
    return this;
  }

  public int getMaxAllowedMoves() {
    return maxAllowedMoves.getVal();
  }

  public ServerSettings setMaxAllowedMoves(Setting<Integer> maxAllowedMoves) {
    this.maxAllowedMoves = maxAllowedMoves;
    return this;
  }

  public int getRadarDistance() {
    return radarDistance.getVal();
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

  public int getMaxClusterSize() {
    return maxClusterSize.getVal();
  }

  public ServerSettings setMaxClusterSize(Setting<Integer> maxClusterSize) {
    this.maxClusterSize = maxClusterSize;
    return this;
  }

  public float getOreThreshold() {
    return oreThreshold.getVal();
  }

  public ServerSettings setOreThreshold(Setting<Float> oreThreshold) {
    this.oreThreshold = oreThreshold;
    return this;
  }
}
