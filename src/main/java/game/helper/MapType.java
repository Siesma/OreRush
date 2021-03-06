package game.helper;

import java.util.Locale;

/**
 * this enum holds the type of the hashmaps used by the FileHelper class
 */
public enum MapType {
    GameObjects(), Packets();

    /**
     * Constructor for the map type enum
     */
    MapType () {

    }

    /**
     * Getter for the HashName
     * @return the hash name as a String
     */
    public String getHashName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

}
