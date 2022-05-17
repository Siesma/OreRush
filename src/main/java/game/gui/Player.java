package game.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * this class is used in the lobby player table
 * it holds the nickname and score of the players in the lobby
 */
public class Player {

    /**
     * The nickname of the Player
     */
    private final StringProperty nickname;
    /**
     * The score of the Player
     */
    private StringProperty score = new SimpleStringProperty("0");

    /**
     * The constructor for the Player Class
     * @param nickname the Nickname the player should bear
     */
    public Player(String nickname) {
        this.nickname = new SimpleStringProperty(nickname);
    }

    /**
     * Getter for the Nickname of the player
     * @return the nickname of the player as a String
     */
    public String getNickname() {
        return nickname.get();
    }

    /**
     * Getter for the nickname stringProperty of the player
     * @return the stringproperty, holding the nickname
     */
    public StringProperty nicknameProperty() {
        return nickname;
    }

    /**
     * Setter for the Nickname of the Player
     * @param nickname the new nickname of the Player
     */
    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    /**
     * Getter for the Score of the player
     * @return the score of the player
     */
    public String getScore() {
        return score.get();
    }

    /**
     * Getter for the stringProperty holding the score of the player
     * @return the stringProperty holding the score of the player
     */
    public StringProperty scoreProperty() {
        return score;
    }

    /**
     * Setter for the score of the player
     * @param score new score of the player
     */
    public void setScore(String score) {
        this.score.set(score);
    }
}
