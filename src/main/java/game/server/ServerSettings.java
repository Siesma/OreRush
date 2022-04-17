package game.server;


import java.util.ArrayList;

public class ServerSettings {

    /*
    TODO: Make a new Datatype called "ServerSettings" which holds information about packet-replacement procedure,
     the setting and a way to default back to working values in case something gets messed up.
     */
    private int numberOfRobots;
    private int mapWidth = 10, mapHeight = 10;
    private int numberOfRounds = 100;
    private float oreDensity = 0.75f;
    private int maxAllowedMoves;
    private ArrayList<Object> ores;

    private int maxClusterSize = 2;
    private float oreThreshold;

    /**
     * @param pathToFile Used  to import settings from a ".ore_game_settings" file
     */
    public ServerSettings(String pathToFile) {

    }

    /**
     * @param numberOfRobots
     * @param mapWidth
     * @param mapHeight
     * @param numberOfRounds
     * @param ores           Default user settings initialization
     */
    public ServerSettings(int numberOfRobots, int mapWidth, int mapHeight, int numberOfRounds, ArrayList<Object> ores) {
        numberOfRobots = numberOfRobots;
        mapWidth = mapWidth;
        mapHeight = mapHeight;
        this.numberOfRounds = numberOfRounds;
        this.ores = ores;
    }

    public int getNumberOfRobots() {
        return numberOfRobots;
    }

    public ServerSettings setNumberOfRobots(int numberOfRobots) {
        numberOfRobots = numberOfRobots;
        return this;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public ServerSettings setMapWidth(int mapWidth) {
        mapWidth = mapWidth;
        return this;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public ServerSettings setMapHeight(int mapHeight) {
        mapHeight = mapHeight;
        return this;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public ServerSettings setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
        return this;
    }

    public ArrayList<Object> getOres() {
        return ores;
    }

    public ServerSettings setOres(ArrayList<Object> ores) {
        this.ores = ores;
        return this;
    }

    public int getMaxAllowedMoves() {
        return maxAllowedMoves;
    }

    public float getOreDensity() {
        return oreDensity;
    }

    public float getOreThreshold() {
        return oreThreshold;
    }

    public int getMaxClusterSize() {
        return maxClusterSize;
    }
}
