package by.advertcrawler.ui;

import by.advertcrawler.model.Advert;
import by.advertcrawler.model.AdvertContainer;
import by.advertcrawler.utils.FilesUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;

public class MainWindowController {

    @FXML
    private TextField searchTextField;

    @FXML
    private ComboBox<AdvertViewMode> advertViewComboBox;

    @FXML
    private ListView<Advert> advertsListView;

    @FXML
    private Text linkText, titleText, addressText, areaText, floorText, totalFloorText, priceText, phoneNumbersText,
            lastRefreshDateText;

    @FXML
    private CheckBox isFavoriteCheckBox;

    @FXML
    private Button refreshAdvertContainer, getNewAdvertPhoneNumbers, openLinkButton, checkoutPriceHistoryButton;

    private AdvertContainer container;

    public static final String SAVE_PATH = "container.save";

    @FXML
    public void initialize() {
        loadAdvertContainer();

        configViewComboBox();
        changeView();

        configAdvertsListView();
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

        advertsListView.setOnMouseClicked(event -> viewAdvert());
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
                for (AdvertViewMode viewMode : AdvertViewMode.values()) {
                    if (viewMode.getTitle().equals(s)) {
                        return viewMode;
                    }
                }

                return null;
            }
        });
        advertViewComboBox.setOnAction(event -> changeView());
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
