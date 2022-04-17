package game.gui;

import game.client.Client;
import game.client.LobbyInClient;
import game.datastructures.RobotAction;
import game.packet.packets.Move;
import game.server.ServerSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LobbyController {

    Client client;
    LobbyInClient lobby;

    @FXML
    private TableView<Player> playerTableView;
    @FXML
    private TableColumn<Player, String> nicknameColumn;
    @FXML
    private TableColumn<Player, String> scoreColumn;

    public ChoiceBox<String> playerRobotActionList;

    public ListView<String> playerRobotList;
    public ListView<String> currentRobotMovesList;

    public Button addPossibleMovesList;

    public TextField textFieldXCoord;
    public TextField textFieldYCoord;

    public Button buttonMakeMove;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        client = Client.getClient();
        lobby = client.getLobbyInClient();
        playerTableView.setItems(
                lobby.getPlayerData());
        // Initialize the person table with the two columns.
        nicknameColumn.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
       // Initialize the list of the robots of the player.
       // Also initializes the default Move of the robots, to wait.

        for (int i = 0; i < (new ServerSettings("")).getNumberOfRobots(); i++) {
            this.playerRobotList.getItems().add("Robot " + i);
            this.currentRobotMovesList.getItems().add(i + ":Wait:0:0");
        }
        // Adds all the possible robot actions to the combobox.
        for (RobotAction robotAction : RobotAction.values()) {
            this.playerRobotActionList.getItems().add(robotAction.name());
        }
    }

    public void onActionRobotMoveTypes(ActionEvent actionEvent) {

    }

    public void onActionAddPossibleMovesList(ActionEvent actionEvent) {

    }

    /**
     *
     * Once the player is finished with the move they can click the "Finish move" button that sends the given information to the server.
     * I sadly had to quickly modify the request structure to make this work.
     * TODO: Revert the request move structure to its original dynamic state.
     */
    public void onActionButtonMakeMove(ActionEvent actionEvent) {
        String defaultTextMessage = "";
        if (textFieldXCoord.getText().equals(defaultTextMessage) || textFieldYCoord.getText().equals(defaultTextMessage)) {
            return;
        }
        if (playerRobotActionList.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        if (playerRobotList.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        int index = playerRobotList.getSelectionModel().getSelectedIndex();
        String addition = "";
        if (playerRobotActionList.getSelectionModel().getSelectedItem().matches("^Request.*$")) {
            addition = ":" + playerRobotActionList.getSelectionModel().getSelectedItem().split("Request")[1];
        }
        this.currentRobotMovesList.getItems().set(index,
                index + ":" + playerRobotActionList.getSelectionModel().getSelectedItem().replace("Request", "Request_").split("_")[0] + ":" + textFieldXCoord.getText() + ":" + textFieldYCoord.getText() + addition);

    }

    public void onActionEndTurnButton(ActionEvent actionEvent) {
        if (client == null) {
            return;
        }
        client.makeMove(this);
    }


    public void handleStartGame(ActionEvent actionEvent) {
    }

    public void handleWinGame(ActionEvent actionEvent) {
        client.sendChatMessage("i won lul");
    }

    public void handleSendMessage(ActionEvent actionEvent) {
    }
}
