package game.helper;

import java.util.Locale;

public enum MapType {
    GameObjects(), Packets();
    MapType () {

    }
    public String getHashName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

}
