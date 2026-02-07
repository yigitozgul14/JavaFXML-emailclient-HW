package advancejavaproject3;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class NewHostController implements Initializable {

    @FXML private RadioButton imapRadio;
    @FXML private RadioButton pop3Radio;
    @FXML private TextField receivePortField;
    @FXML private TextField sendPortField;
    @FXML private TextField receiveHostField;
    @FXML private TextField sendHostField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private HostConfig savedHostConfig;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imapRadio.setOnAction(e -> {
            receivePortField.setText("993");
        });

        pop3Radio.setOnAction(e -> {
            receivePortField.setText("995");
        });

        receivePortField.setText("993");
        sendPortField.setText("587");

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> closeWindow());
    }

    private void handleSave() {
        String protocol = imapRadio.isSelected() ? "IMAP" : "POP3";
        String receivePort = receivePortField.getText();
        String sendPort = sendPortField.getText();
        String receiveHost = receiveHostField.getText();
        String sendHost = sendHostField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (receiveHost.trim().isEmpty() || sendHost.trim().isEmpty() ||
            username.trim().isEmpty() || password.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Missing Information");
            alert.setContentText("Please fill all required fields.");
            alert.showAndWait();
            return;
        }

        try {
            int rPort = Integer.parseInt(receivePort);
            int sPort = Integer.parseInt(sendPort);

            savedHostConfig = new HostConfig(protocol, receiveHost, rPort,
                                             sendHost, sPort, username, password);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Host Configuration Saved");
            alert.setContentText("Host: " + receiveHost + "\nUsername: " + username);
            alert.showAndWait();

            closeWindow();
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Port Number");
            alert.setContentText("Ports must be numeric values.");
            alert.showAndWait();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    public HostConfig getSavedHostConfig() {
        return savedHostConfig;
    }
}
