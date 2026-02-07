package advancejavaproject3;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ComposeController implements Initializable {

    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private Button attachButton;
    @FXML private TextArea bodyArea;
    @FXML private Button sendButton;
    @FXML private Button cancelButton;

    private HostConfig currentHost;
    private String attachmentPath;

    public void setHostConfig(HostConfig host) {
        this.currentHost = host;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        sendButton.setOnAction(e -> {
            String to = toField.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();

            if (currentHost == null) {
                showAlert("Error", "No host selected. Please configure a host first.");
                return;
            }

            if (to == null || to.trim().isEmpty()) {
                showAlert("Error", "Recipient (To) cannot be empty.");
                return;
            }

            try {
                EmailService.sendEmail(currentHost, to, subject, body, attachmentPath);
                showAlert("Success", "Email sent successfully!");
                closeWindow();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to send email: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> {
            closeWindow();
        });

        attachButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Attachment");
            File file = fileChooser.showOpenDialog(attachButton.getScene().getWindow());
            if (file != null) {
                attachmentPath = file.getAbsolutePath();
                showAlert("Info", "Attachment selected: " + file.getName());
            }
        });
    }
    /**
 *
 * @author aral
 */
    private void closeWindow() {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        stage.close();
    }
    /**
 *
 * @author aral
 */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
