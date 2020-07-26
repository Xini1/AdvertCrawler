package by.advertcrawler.ui.controller;

import by.advertcrawler.model.PriceHistory;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.Deque;

public class CheckoutPriceHistoryWindowController {

    @FXML
    private ScrollPane priceHistoryScrollPane;

    @FXML
    private TableView<PriceHistory> priceHistoryTableView;

    @FXML
    private Button closeWindowButton;

    @FXML
    public void initialize() {
        configPriceHistoryTableView();
        configCloseWindowButton();
    }

    public void fillInTableView(Deque<PriceHistory> priceHistoryDeque) {
        priceHistoryTableView.getItems().addAll(priceHistoryDeque);
    }

    private void configPriceHistoryTableView() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        TableColumn<PriceHistory, String> dateColumn = new TableColumn<>("Дата обновления");
        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatter.format(cellData.getValue().getDate())));

        TableColumn<PriceHistory, Integer> priceColumn = new TableColumn<>("Цена");
        priceColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getPrice()).asObject());

        ObservableList<TableColumn<PriceHistory, ?>> columns = priceHistoryTableView.getColumns();
        columns.add(dateColumn);
        columns.add(priceColumn);

        priceHistoryTableView.prefHeightProperty().bind(priceHistoryScrollPane.heightProperty().subtract(10));
        priceHistoryTableView.prefWidthProperty().bind(priceHistoryScrollPane.widthProperty().subtract(10));
    }

    private void configCloseWindowButton() {
        closeWindowButton.setOnAction(actionEvent -> closeWindowButton.getScene().getWindow().hide());
    }


}
