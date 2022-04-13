package game.datastructures;

public abstract interface GameObject {

    /**
     * This function should encode all the information of a GameObject into a string to allow data
     * transfer via a package.
     * @return The encoded string that holds all the information of the robot
     */
    public String encodeToString();

    /*
        Maybe we should make a function that fills instead of creates, because we then can call
        "(new FileHelper()).createInstanceOfClass(PathToClass)" to create a new object of a given class
        for example:
        "(new FileHelper()).createInstanceOfClass("game.datastructures.Radar")" would create a new Radar object.
        We then could just fetch incoming data and apply it to the object afterwards!
     */

    public void parseGameObjectFromString(String encodedGameObject);

    public void fillGameObjectWithData (String... data);
}
