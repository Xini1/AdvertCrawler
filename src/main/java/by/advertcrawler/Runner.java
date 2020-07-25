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
        List<Advert> adverts = new ArrayList<>();

        AdvertContainer container = new AdvertContainer();
        container.setCreationDate(date);
        container.setAdverts(adverts);

        for (int i = 0; i < 100; i++) {
            Advert advert = new Advert();
            advert.setAdvertUrl("https://o7planning.org/ru/11133/javafx-hyperlink-tutorial");
            advert.setTitle("title" + i);
            advert.setAddress("address" + i);
            advert.setArea(10f + 10 * i);
            advert.setFloor(1 + i);
            advert.setTotalFloors(3 + i);

            Deque<PriceHistory> priceHistoryDeque1 = new LinkedList<>();
            PriceHistory priceHistory1 = new PriceHistory();
            priceHistory1.setDate(date);
            priceHistory1.setPrice(100 + 100 * i);
            priceHistoryDeque1.addFirst(priceHistory1);
            advert.setPriceHistoryDeque(priceHistoryDeque1);

            List<String> phoneNumbers1 = new ArrayList<>(Collections.singletonList("+3752911111" + String.format("%02d", i)));
            advert.setPhoneNumbers(phoneNumbers1);

            advert.setNew(i % 2 == 0);
            advert.setFavorite(false);
            advert.setLastRefreshDate(date);

            adverts.add(advert);
        }

        return container;
    }
}
