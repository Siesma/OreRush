package game.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * this class is used in the lobby player table
 * it holds the nickname and score of the players in the lobby
 */
public class Player {

    private final StringProperty nickname;
    private StringProperty score = new SimpleStringProperty("0");

    public Player(String nickname) {
        this.nickname = new SimpleStringProperty(nickname);
    }

    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public String getScore() {
        return score.get();
    }

    public StringProperty scoreProperty() {
        return score;
    }

    public void setScore(String score) {
        this.score.set(score);
    }
}
