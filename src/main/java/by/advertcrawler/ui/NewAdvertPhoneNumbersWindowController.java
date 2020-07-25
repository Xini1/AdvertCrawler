package by.advertcrawler.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;

public class NewAdvertPhoneNumbersWindowController {

    @FXML
    private Text phoneNumbersText;

    @FXML
    private Button copyToClipboardButton;

    @FXML
    private Button closeWindowButton;

    @FXML
    public void initialize() {
        copyToClipboardButton.setOnAction(actionEvent -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(phoneNumbersText.getText());
            Clipboard.getSystemClipboard().setContent(content);
        });

        closeWindowButton.setOnAction(actionEvent -> closeWindowButton.getScene().getWindow().hide());
    }

    public void setText(String text) {
        phoneNumbersText.setText(text);
    }
}
