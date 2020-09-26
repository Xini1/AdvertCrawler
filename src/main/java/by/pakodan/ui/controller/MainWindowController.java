package by.pakodan.ui.controller;

import by.pakodan.model.Advert;
import by.pakodan.model.AdvertContainer;
import by.pakodan.model.Status;
import by.pakodan.ui.AdvertViewMode;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    private ComboBox<Status> statusComboBox;

    @FXML
    private Button openWindowRefreshAdvertContainerButton;

    @FXML
    private Button openWindowNewAdvertPhoneNumbersButton;

    @FXML
    private Button openAdvertUrlInBrowserButton;

    @FXML
    private Button openWindowCheckoutPriceHistoryButton;

    @FXML
    private GridPane advertGridPane;

    private AdvertContainer container;
    private Advert shownAdvert;

    private final Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    public void initialize() {
        configSearchTextField();
        configViewComboBox();
        configAdvertsListView();
        configButtons();
        configStatusComboBox();
    }

    public AdvertContainer getContainer() {
        return container;
    }

    public void setContainer(AdvertContainer container) {
        this.container = container;
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
        openAdvertUrlInBrowserButton.setOnAction(actionEvent -> openAdvertUrlInBrowser());
        openWindowCheckoutPriceHistoryButton.setOnAction(actionEvent -> openWindowCheckoutPriceHistory());
        openWindowRefreshAdvertContainerButton.setOnAction(actionEvent -> openWindowRefreshAdvertContainer());
        openWindowNewAdvertPhoneNumbersButton.setOnAction(actionEvent -> openWindowNewAdvertPhoneNumbers());
    }

    private void configStatusComboBox() {
        statusComboBox.getItems().addAll(Status.values());
        statusComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Status status) {
                if (status == null) {
                    return "";
                }

                return status.getTitle();
            }

            @Override
            public Status fromString(String s) {
                return null;
            }
        });

        statusComboBox.setOnAction(actionEvent -> {
            shownAdvert.setStatus(statusComboBox.getSelectionModel().getSelectedItem());
            changeView();
        });
    }

    private void openAdvertUrlInBrowser() {
        Class<HostServices> hostServicesClass = HostServices.class;
        HostServices hostServices = hostServicesClass.cast(openAdvertUrlInBrowserButton.getScene().getWindow()
                .getProperties().get(hostServicesClass));
        hostServices.showDocument(linkText.getText());
    }

    private void openWindowCheckoutPriceHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdvertCrawlerPriceHistoryWindow.fxml"));

            Parent root = loader.load();
            CheckoutPriceHistoryWindowController controller = loader.getController();
            controller.fillInTableView(shownAdvert.getPriceHistoryDeque());

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("История изменений цены");
            stage.getIcons().add(new Image("file:advert_crawler.png"));
            stage.initOwner(openWindowCheckoutPriceHistoryButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.showAndWait();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load price history window", e);
        }
    }

    private void openWindowRefreshAdvertContainer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdvertCrawlerRefreshWindow.fxml"));

            Parent root = loader.load();
            RefreshAdvertContainerWindowController controller = loader.getController();
            controller.setMainWindowController(this);
            controller.setOldContainer(container);

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Обновление базы объявлений");
            stage.getIcons().add(new Image("file:advert_crawler.png"));
            stage.initOwner(openWindowRefreshAdvertContainerButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnShowing(windowEvent -> controller.run());
            stage.setOnHiding(windowEvent -> controller.shutdown());

            stage.showAndWait();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load refresh advert container window", e);
        }
    }

    private void openWindowNewAdvertPhoneNumbers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/AdvertCrawlerNewAdvertPhoneNumbersWindow.fxml"));

            Parent root = loader.load();
            NewAdvertPhoneNumbersWindowController controller = loader.getController();

            Set<String> seenPhoneNumbers = getSeenPhoneNumbers();

            Set<String> phoneNumbers = container.getNewAdverts().stream()
                    .map(advert -> {
                        List<String> phoneNumbersList = advert.getPhoneNumbers();
                        return phoneNumbersList.isEmpty() ? null : phoneNumbersList.get(0);
                    })
                    .filter(Objects::nonNull)
                    .filter(phoneNumber -> !phoneNumber.startsWith("+375232"))
                    .collect(Collectors.toSet());

            Set<String> uniquePhoneNumbers = phoneNumbers.stream()
                    .filter(phoneNumber -> !seenPhoneNumbers.contains(phoneNumber))
                    .collect(Collectors.toSet());

            controller.setPhoneNumbersText(String.join(";", phoneNumbers));
            controller.setUniquePhoneNumbersText(String.join(";", uniquePhoneNumbers));

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Номера телефонов из новых объявлений");
            stage.getIcons().add(new Image("file:advert_crawler.png"));
            stage.initOwner(openWindowNewAdvertPhoneNumbersButton.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.showAndWait();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load refresh advert container window", e);
        }
    }

    private Set<String> getSeenPhoneNumbers() {
        Set<String> seenPhoneNumbers = new HashSet<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("SeenPhoneNumbers.save"))) {
            seenPhoneNumbers = (Set<String>) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, "Could not find class", e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load seen phone numbers", e);
        }
        return seenPhoneNumbers;
    }

    private void viewAdvert() {
        shownAdvert = advertsListView.getSelectionModel().getSelectedItem();

        linkText.setText(shownAdvert.getAdvertUrl());
        titleText.setText(shownAdvert.getTitle());
        addressText.setText(shownAdvert.getAddress());
        areaText.setText("" + shownAdvert.getArea());
        floorText.setText("" + shownAdvert.getFloor());
        totalFloorText.setText("" + shownAdvert.getTotalFloors());
        priceText.setText("" + shownAdvert.getPriceHistoryDeque().getFirst().getPrice());

        String phoneNumbers = String.join("; ", shownAdvert.getPhoneNumbers());
        phoneNumbersText.setText(phoneNumbers);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        lastRefreshDateText.setText(formatter.format(shownAdvert.getLastRefreshDate()));

        statusComboBox.setValue(shownAdvert.getStatus());

        advertGridPane.setVisible(true);
    }

    public void changeView() {
        List<Advert> advertsToShow = getAdvertsBasedOnViewMode();
        List<Advert> filteredAdverts = filterAdverts(advertsToShow);
        fillInAdvertsListView(filteredAdverts);
    }

    private List<Advert> getAdvertsBasedOnViewMode() {
        AdvertViewMode viewMode = advertViewComboBox.getSelectionModel().getSelectedItem();
        List<Advert> advertsToShow;

        switch (viewMode) {
            case NEW:
                advertsToShow = container.getNewAdverts();
                break;
            case FAVORITE:
                advertsToShow = container.getFavoriteAdverts();
                break;
            case IGNORED:
                advertsToShow = container.getIgnoredAdverts();
                break;
            default:
                advertsToShow = container.getNotIgnoredAdverts();
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
        Advert selected = advertsListView.getSelectionModel().getSelectedItem();
        ObservableList<Advert> adverts = advertsListView.getItems();
        adverts.clear();
        adverts.addAll(advertsToShow);

        if (selected != null && adverts.contains(selected)) {
            advertsListView.getSelectionModel().select(selected);
        }
    }
}
