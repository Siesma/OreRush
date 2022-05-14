package game.datastructures;

/**
 * Enum that holds the name of the ore and its value
 */
public enum OreType {

    Copper(1), Iron(2), Gold(4), Platinum(6), Indium(9);

    /**
     * The value of points this type of are is worth
     */
    int value;

    /**
     * Constructor of the OreType
     * @param value The desired value the OreType should hold
     */
    OreType(int value) {
        this.value = value;
    }

    /**
     * Getter for the Value of the OreType
     * @return
     */
    public int getValue() {
        return this.value;
    }

}
