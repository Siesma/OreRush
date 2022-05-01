package game.helper;

import java.util.Locale;

/**
 * this enum holds the type of the hashmaps used by the FileHelper class
 */
public enum MapType {
    GameObjects(), Packets();
    MapType () {

    }
    public String getHashName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

}
