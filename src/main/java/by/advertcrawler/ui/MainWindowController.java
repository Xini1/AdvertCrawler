package by.advertcrawler.ui;

import by.advertcrawler.model.Advert;
import by.advertcrawler.model.AdvertContainer;
import by.advertcrawler.utils.FilesUtils;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindowController {

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<AdvertViewMode> advertViewComboBox;

    @FXML
    private ListView<Advert> advertsListView;

    @FXML
    private Text linkText;

    @FXML
    private Text titleText;

    @FXML
    private Text addressText;

    @FXML
    private Text areaText;

    @FXML
    private Text floorText;

    @FXML
    private Text totalFloorText;

    @FXML
    private Text priceText;

    @FXML
    private Text phoneNumbersText;

    @FXML
    private Text lastRefreshDateText;

    @FXML
    private CheckBox isFavoriteCheckBox;

    @FXML
    private Button refreshAdvertContainer;

    @FXML
    private Button getNewAdvertPhoneNumbers;

    @FXML
    private Button openLinkButton;

    @FXML
    private Button checkoutPriceHistoryButton;

    @FXML
    private GridPane advertGridPane;

    private AdvertContainer container;

    public static final String SAVE_PATH = "container.save";

    @FXML
    public void initialize() {
        loadAdvertContainer();

        configViewComboBox();
        changeView();

        configAdvertsListView();

        configButtons();

        configFavoriteCheckBox();
    }

    private void configFavoriteCheckBox() {
        isFavoriteCheckBox.setOnAction(actionEvent ->
                advertsListView.getSelectionModel().getSelectedItem().setFavorite(isFavoriteCheckBox.isSelected()));
    }

    private void configButtons() {
        openLinkButton.setOnAction(actionEvent -> openAdvertUrlInBrowser());
        checkoutPriceHistoryButton.setOnAction(actionEvent -> checkoutPriceHistory());
    }

    private void checkoutPriceHistory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdvertCrawlerPriceHistoryWindow.fxml"));

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("История изменений цены");
            stage.initOwner(checkoutPriceHistoryButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.showAndWait();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Could not load price history window", e);
        }

    }

    private void openAdvertUrlInBrowser() {
        Class<HostServices> hostServicesClass = HostServices.class;
        HostServices hostServices = hostServicesClass.cast(openLinkButton.getScene().getWindow().getProperties()
                .get(hostServicesClass));
        hostServices.showDocument(linkText.getText());
    }

    private void configAdvertsListView() {
        advertsListView.setCellFactory(advertListView -> {
            TextFieldListCell<Advert> cell = new TextFieldListCell<>();

            cell.setConverter(new StringConverter<>() {
                @Override
                public String toString(Advert advert) {
                    return advert.getAddress();
                }

                @Override
                public Advert fromString(String s) {
                    return null;
                }
            });

            return cell;
        });

        advertsListView.setOnMouseClicked(mouseEvent -> viewAdvert());
    }

    private void viewAdvert() {
        Advert selected = advertsListView.getSelectionModel().getSelectedItem();

        linkText.setText(selected.getAdvertUrl());
        titleText.setText(selected.getTitle());
        addressText.setText(selected.getAddress());
        areaText.setText("" + selected.getArea());
        floorText.setText("" + selected.getFloor());
        totalFloorText.setText("" + selected.getTotalFloors());
        priceText.setText("" + selected.getPriceHistoryDeque().getFirst().getPrice());

        String phoneNumbers = String.join("; ", selected.getPhoneNumbers());
        phoneNumbersText.setText(phoneNumbers);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        lastRefreshDateText.setText(formatter.format(selected.getLastRefreshDate()));

        isFavoriteCheckBox.setSelected(selected.isFavorite());

        advertGridPane.setVisible(true);
    }

    private void loadAdvertContainer() {
        FilesUtils filesUtils = new FilesUtils();
        String csv = filesUtils.readFromFile(SAVE_PATH);
        container = AdvertContainer.parseCsv(csv);
    }

    private void configViewComboBox() {
        advertViewComboBox.getItems().addAll(AdvertViewMode.values());
        advertViewComboBox.setValue(AdvertViewMode.values()[0]);
        advertViewComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(AdvertViewMode advertViewMode) {
                return advertViewMode.getTitle();
            }

            @Override
            public AdvertViewMode fromString(String s) {
                return null;
            }
        });
        advertViewComboBox.setOnAction(actionEvent -> changeView());
    }

    private void changeView() {
        AdvertViewMode viewMode = advertViewComboBox.getSelectionModel().getSelectedItem();
        ObservableList<Advert> adverts = advertsListView.getItems();
        adverts.clear();

        switch (viewMode) {
            case ALL:
                adverts.addAll(container.getAdverts());
                break;
            case NEW:
                adverts.addAll(container.getNewAdverts());
                break;
            case FAVORITE:
                adverts.addAll(container.getFavoriteAdverts());
                break;
        }
    }
}
