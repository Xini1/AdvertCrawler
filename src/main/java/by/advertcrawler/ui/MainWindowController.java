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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private Button openWindowRefreshAdvertContainerButton;

    @FXML
    private Button openWindowGetNewAdvertPhoneNumbers;

    @FXML
    private Button openLinkButton;

    @FXML
    private Button checkoutPriceHistoryButton;

    @FXML
    private GridPane advertGridPane;

    private FilesUtils filesUtils;
    private AdvertContainer container;

    public static final String SAVE_PATH = AdvertContainer.class.getName() + ".save";

    @FXML
    public void initialize() {
        loadAdvertContainer();

        configSearchTextField();

        configViewComboBox();
        changeView();

        configAdvertsListView();

        configButtons();

        configFavoriteCheckBox();
    }

    private void loadAdvertContainer() {
        filesUtils = new FilesUtils();
        String csv = filesUtils.readFromFile(SAVE_PATH);
        container = AdvertContainer.parseCsv(csv);
    }

    private void configSearchTextField() {
        searchTextField.textProperty().addListener(((observableValue, oldInput, newInput) -> changeView()));
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

    private void configButtons() {
        openLinkButton.setOnAction(actionEvent -> openAdvertUrlInBrowser());
        checkoutPriceHistoryButton.setOnAction(actionEvent -> openWindowCheckoutPriceHistory());
        openWindowRefreshAdvertContainerButton.setOnAction(actionEvent -> openWindowRefreshAdvertContainer());
    }

    private void configFavoriteCheckBox() {
        isFavoriteCheckBox.setOnAction(actionEvent -> {
            advertsListView.getSelectionModel().getSelectedItem().setFavorite(isFavoriteCheckBox.isSelected());
            changeView();
        });
    }

    private void openWindowRefreshAdvertContainer() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AdvertCrawlerRefreshWindow.fxml"));

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Обновление базы объявлений");
            stage.initOwner(openWindowRefreshAdvertContainerButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.showAndWait();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Could not load refresh advert container window", e);
        }
    }

    private void openWindowCheckoutPriceHistory() {
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

    private void changeView() {
        List<Advert> advertsToShow = getAdvertsBasedOnViewMode();
        List<Advert> filteredAdverts = filterAdverts(advertsToShow);
        fillInAdvertsListView(filteredAdverts);
    }

    private List<Advert> getAdvertsBasedOnViewMode() {
        AdvertViewMode viewMode = advertViewComboBox.getSelectionModel().getSelectedItem();
        List<Advert> advertsToShow;

        switch (viewMode) {
            case ALL:
                advertsToShow = container.getAdverts();
                break;
            case NEW:
                advertsToShow = container.getNewAdverts();
                break;
            case FAVORITE:
                advertsToShow = container.getFavoriteAdverts();
                break;
            default:
                advertsToShow = container.getAdverts();
        }

        return advertsToShow;
    }

    private List<Advert> filterAdverts(List<Advert> advertsToShow) {
        if (searchTextField.getText().isEmpty()) {
            return advertsToShow;
        }

        return advertsToShow.stream()
                .filter(advert -> advert.contains(searchTextField.getText()))
                .collect(Collectors.toList());
    }

    private void fillInAdvertsListView(List<Advert> advertsToShow) {
        ObservableList<Advert> adverts = advertsListView.getItems();
        adverts.clear();
        adverts.addAll(advertsToShow);
    }

    public void saveAdvertContainer() {
        filesUtils.writeToFile(container.toCsv(), SAVE_PATH);
    }
}
