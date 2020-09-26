package by.pakodan.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

public class NewAdvertPhoneNumbersWindowController {

    @FXML
    private TabPane tabPane;

    @FXML
    private Text phoneNumbersText;

    @FXML
    private Text uniquePhoneNumbersText;

    @FXML
    private Button copyToClipboardButton;

    @FXML
    private Button closeWindowButton;

    @FXML
    public void initialize() {
        copyToClipboardButton.setOnAction(actionEvent -> {
            ClipboardContent content = new ClipboardContent();

            switch (tabPane.getSelectionModel().getSelectedIndex()) {
                case 0:
                    content.putString(phoneNumbersText.getText());
                    break;
                case 1:
                    content.putString(uniquePhoneNumbersText.getText());
                    break;
            }

            Clipboard.getSystemClipboard().setContent(content);
        });

        closeWindowButton.setOnAction(actionEvent -> closeWindowButton.getScene().getWindow().hide());
    }

    public void setPhoneNumbersText(String phoneNumbersText) {
        this.phoneNumbersText.setText(phoneNumbersText);
    }

    public void setUniquePhoneNumbersText(String uniquePhoneNumbersText) {
        this.uniquePhoneNumbersText.setText(uniquePhoneNumbersText);
    }
}
