package game.datastructures;

public enum OreType {

    Copper(1), Iron(2), Gold(4), Platinum(6), Indium(9);

    int value;

    OreType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
