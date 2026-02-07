package advancejavaproject3;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 *
 * @author aral
 */
public class FXMLDocumentController implements Initializable {

    @FXML private Label currentHostLabel;
    @FXML private Button refreshButton;
    @FXML private Button composeButton;
    @FXML private Button replyButton;
    @FXML private Button newHostButton;
    @FXML private ComboBox<HostConfig> hostComboBox;

    @FXML private ListView<String> folderListView;
    @FXML private TableView<Email> emailTableView;

    @FXML private TextField senderField;
    @FXML private TextField subjectField;
    @FXML private TextField dateField;
    @FXML private TextArea messageBodyArea;

    private ObservableList<Email> inboxEmails;
    private ObservableList<HostConfig> hostConfigs;
    /**
 *
 * @author aral
 */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hostConfigs = FXCollections.observableArrayList();
        hostComboBox.setItems(hostConfigs);

        hostComboBox.setOnAction(e -> {
            HostConfig selected = hostComboBox.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentHostLabel.setText("Current host: " + selected.getReceiveHost());
            }
        });

        folderListView.getItems().addAll("INBOX", "Trial folder", "Sent Items");
        folderListView.getSelectionModel().selectFirst();

        folderListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(oldVal)) {
                loadEmailsForFolder(newVal);
            }
        });

        // Tablo kolonları
        TableColumn<Email, String> senderCol = new TableColumn<>("Sender");
        senderCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSender())
        );

        TableColumn<Email, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSubject())
        );

        TableColumn<Email, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDate().toString())
        );

        TableColumn<Email, String> attachCol = new TableColumn<>("Attachment");
        attachCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isHasAttachment() ? "Yes" : "No")
        );

        emailTableView.getColumns().addAll(senderCol, subjectCol, dateCol, attachCol);
        emailTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        inboxEmails = FXCollections.observableArrayList();
        emailTableView.setItems(inboxEmails);

        emailTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                senderField.setText(newSel.getSender());
                subjectField.setText(newSel.getSubject());
                dateField.setText(newSel.getDate().toString());
                messageBodyArea.setText(newSel.getBody());
            } else {
                senderField.clear();
                subjectField.clear();
                dateField.clear();
                messageBodyArea.clear();
            }
        });


        refreshButton.setOnAction(e -> {
            String selectedFolder = folderListView.getSelectionModel().getSelectedItem();
            if (selectedFolder == null) {
                selectedFolder = "INBOX";
            }
            loadEmailsForFolder(selectedFolder);
        });

        composeButton.setOnAction(e -> {
            HostConfig selectedHost = hostComboBox.getSelectionModel().getSelectedItem();

            if (selectedHost == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No host selected");
                alert.setContentText("Please select or configure a host first.");
                alert.showAndWait();
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ComposeView.fxml"));
                Parent root = loader.load();

                ComposeController controller = loader.getController();
                controller.setHostConfig(selectedHost);

                Stage stage = new Stage();
                stage.setTitle("Compose New Email");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot open compose window");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        replyButton.setOnAction(e -> {
            HostConfig selectedHost = hostComboBox.getSelectionModel().getSelectedItem();

            if (selectedHost == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No host selected");
                alert.setContentText("Please select or configure a host first.");
                alert.showAndWait();
                return;
            }

            Email selected = emailTableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No email selected");
                alert.setContentText("Please select an email to reply.");
                alert.showAndWait();
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReplyView.fxml"));
                Parent root = loader.load();

                ReplyController controller = loader.getController();
                controller.setHostConfig(selectedHost);
                controller.setOriginalEmail(selected);

                Stage stage = new Stage();
                stage.setTitle("Reply");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot open reply window");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });


        newHostButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NewHostView.fxml"));
                Parent root = loader.load();

                NewHostController controller = loader.getController();

                Stage stage = new Stage();
                stage.setTitle("New Host Configuration");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                HostConfig newHost = controller.getSavedHostConfig();
                if (newHost != null) {
                    hostConfigs.add(newHost);
                    hostComboBox.getSelectionModel().select(newHost);
                    currentHostLabel.setText("Current host: " + newHost.getReceiveHost());
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot open host configuration window");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void loadEmailsForFolder(String folderName) {
        HostConfig selectedHost = hostComboBox.getSelectionModel().getSelectedItem();

        if (selectedHost == null) {
            return;
        }

        if (folderName.equals("Trial folder")) {
            inboxEmails.clear();
            return;
        }

        currentHostLabel.setText("Current host: " + selectedHost.getReceiveHost() + " (Loading " + folderName + "...)");

        new Thread(() -> {
            try {
                System.out.println("Loading folder: " + folderName);
                Email[] emails = EmailService.receiveEmailsFromFolder(selectedHost, folderName);
                System.out.println("Received " + emails.length + " emails from " + folderName);

                javafx.application.Platform.runLater(() -> {
                    try {
                        inboxEmails.clear();
                        for (Email email : emails) {
                            inboxEmails.add(email);
                        }
                        currentHostLabel.setText("Current host: " + selectedHost.getReceiveHost());

                        if (emails.length > 0) {
                            emailTableView.getSelectionModel().selectFirst();
                        }
                    } catch (Exception uiEx) {
                        uiEx.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    currentHostLabel.setText("Current host: " + selectedHost.getReceiveHost());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to load folder");
                    alert.setContentText("Error loading " + folderName + ": " + ex.getMessage());
                    alert.showAndWait();
                });
            }
        }).start();
    }
}
