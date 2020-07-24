package by.advertcrawler;

import by.advertcrawler.model.Advert;
import by.advertcrawler.model.AdvertContainer;
import by.advertcrawler.model.PriceHistory;
import by.advertcrawler.ui.MainWindowController;
import by.advertcrawler.ui.UiStarter;
import by.advertcrawler.utils.FilesUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Runner {

    public static void main(String[] args) {
        loadLoggingProperties();

        AdvertContainer container = getContainer();
        FilesUtils filesUtils = new FilesUtils();
        filesUtils.writeToFile(container.toCsv(), MainWindowController.SAVE_PATH);

        try {
            UiStarter.run(args);
        } catch (Exception e) {
            Logger.getLogger(Runner.class.getName())
                    .log(Level.SEVERE, "Unexpected exception occurred", e);
        }
    }

    private static void loadLoggingProperties() {
        try {
            LogManager.getLogManager().readConfiguration(
                    new FileInputStream("src/main/resources/logging.properties"));
        } catch (IOException e) {
            Logger.getLogger(Runner.class.getName())
                    .log(Level.SEVERE, "Could not load logging.properties file", e);
        }
    }

    private static AdvertContainer getContainer() {
        LocalDate date = LocalDate.now();

        AdvertContainer container = new AdvertContainer();
        container.setCreationDate(date);

        Advert advert1 = new Advert();
        advert1.setAdvertUrl("url1");
        advert1.setTitle("title1");
        advert1.setAddress("address1");
        advert1.setArea(10);
        advert1.setFloor(1);
        advert1.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque1 = new LinkedList<>();
        PriceHistory priceHistory1 = new PriceHistory();
        priceHistory1.setDate(date);
        priceHistory1.setPrice(100);
        priceHistoryDeque1.addFirst(priceHistory1);
        advert1.setPriceHistoryDeque(priceHistoryDeque1);

        List<String> phoneNumbers1 = new ArrayList<>(Collections.singletonList("+375291111111"));
        advert1.setPhoneNumbers(phoneNumbers1);

        advert1.setNew(true);
        advert1.setFavorite(true);
        advert1.setLastRefreshDate(date);

        Advert advert2 = new Advert();
        advert2.setAdvertUrl("url2");
        advert2.setTitle("title2");
        advert2.setAddress("address2");
        advert2.setArea(20);
        advert2.setFloor(2);
        advert2.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque2 = new LinkedList<>();
        PriceHistory priceHistory2 = new PriceHistory();
        priceHistory2.setDate(date);
        priceHistory2.setPrice(200);
        priceHistoryDeque2.addFirst(priceHistory2);
        advert2.setPriceHistoryDeque(priceHistoryDeque2);

        List<String> phoneNumbers2 = new ArrayList<>(Arrays.asList("+375291111111", "+375292222222"));
        advert2.setPhoneNumbers(phoneNumbers2);

        advert2.setNew(true);
        advert2.setFavorite(false);
        advert2.setLastRefreshDate(date);

        List<Advert> adverts = new ArrayList<>();
        adverts.add(advert1);
        adverts.add(advert2);

        container.setAdverts(adverts);

        return container;
    }
}
