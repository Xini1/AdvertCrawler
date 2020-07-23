import model.Advert;
import model.AdvertContainer;
import model.PriceHistory;
import utils.AdvertContainerMerger;
import utils.FilesUtils;

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

//        String searchResultsUrl="https://www.moyareklama.by/search/%D0%93%D0%BE%D0%BC%D0%B5%D0%BB%D1%8C/c56950abe5d86434d5e08455380399be/";
//        crawling.AdvertCrawler crawler = new crawling.AdvertCrawler(searchResultsUrl);
//        crawler.getAdvertContainer();
        AdvertContainer container1 = getFirstAdvertContainer();
        container1.getAdverts().forEach(System.out::println);
        System.out.println();

        FilesUtils filesUtils = new FilesUtils();
        filesUtils.writeToFile(container1.toCsv(), "container.save");
        String text = filesUtils.readFromFile("container.save");
        AdvertContainer parsedContainer = AdvertContainer.parseCsv(text);
        parsedContainer.getAdverts().forEach(System.out::println);
    }

    private static AdvertContainer getFirstAdvertContainer() {
        LocalDate creationDate = LocalDate.of(2020, 1, 1);

        Advert advert1 = new Advert();
        advert1.setAdvertUrl("url1");
        advert1.setTitle("title1");
        advert1.setAddress("address1");
        advert1.setArea(10);
        advert1.setFloor(1);
        advert1.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque1 = new LinkedList<>();
        PriceHistory priceHistoryForAdvert1 = new PriceHistory();
        priceHistoryForAdvert1.setPrice(100);
        priceHistoryForAdvert1.setDate(creationDate);
        priceHistoryDeque1.offerFirst(priceHistoryForAdvert1);
        advert1.setPriceHistoryDeque(priceHistoryDeque1);

        advert1.setPhoneNumbers(Collections.singletonList("+375291111111"));

        Advert advert2 = new Advert();
        advert2.setAdvertUrl("url2");
        advert2.setTitle("title2");
        advert2.setAddress("address2");
        advert2.setArea(20);
        advert2.setFloor(2);
        advert2.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque2 = new LinkedList<>();
        PriceHistory priceHistoryForAdvert2 = new PriceHistory();
        priceHistoryForAdvert2.setPrice(200);
        priceHistoryForAdvert2.setDate(creationDate);
        priceHistoryDeque2.offerFirst(priceHistoryForAdvert2);
        advert2.setPriceHistoryDeque(priceHistoryDeque2);

        advert2.setPhoneNumbers(Collections.singletonList("+375292222222"));

        Advert advert3 = new Advert();
        advert3.setAdvertUrl("url3");
        advert3.setTitle("title3");
        advert3.setAddress("address3");
        advert3.setArea(30);
        advert3.setFloor(3);
        advert3.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque3 = new LinkedList<>();
        PriceHistory priceHistoryForAdvert3 = new PriceHistory();
        priceHistoryForAdvert3.setPrice(300);
        priceHistoryForAdvert3.setDate(creationDate);
        priceHistoryDeque3.offerFirst(priceHistoryForAdvert3);
        advert3.setPriceHistoryDeque(priceHistoryDeque3);

        advert3.setPhoneNumbers(Collections.singletonList("+375293333333"));

        advert1.setLastEditDate(creationDate);
        advert2.setLastEditDate(creationDate);
        advert3.setLastEditDate(creationDate);

        AdvertContainer container = new AdvertContainer();
        List<Advert> adverts = new ArrayList<>();
        adverts.add(advert1);
        adverts.add(advert2);
        adverts.add(advert3);
        container.setAdverts(adverts);
        container.setCreationDate(creationDate);

        return container;
    }

    private static AdvertContainer getSecondAdvertContainer() {
        LocalDate creationDate = LocalDate.of(2020, 1, 15);

        Advert advert1 = new Advert();
        advert1.setAdvertUrl("url1");
        advert1.setTitle("new title1");
        advert1.setAddress("address1");
        advert1.setArea(10);
        advert1.setFloor(1);
        advert1.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque1 = new LinkedList<>();
        PriceHistory priceHistoryForAdvert1 = new PriceHistory();
        priceHistoryForAdvert1.setPrice(200);
        priceHistoryForAdvert1.setDate(creationDate);
        priceHistoryDeque1.offerFirst(priceHistoryForAdvert1);
        advert1.setPriceHistoryDeque(priceHistoryDeque1);

        advert1.setPhoneNumbers(Collections.singletonList("+375291111111"));

        Advert advert2 = new Advert();
        advert2.setAdvertUrl("url2");
        advert2.setTitle("title2");
        advert2.setAddress("address2");
        advert2.setArea(20);
        advert2.setFloor(2);
        advert2.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque2 = new LinkedList<>();
        PriceHistory priceHistoryForAdvert2 = new PriceHistory();
        priceHistoryForAdvert2.setPrice(200);
        priceHistoryForAdvert2.setDate(creationDate);
        priceHistoryDeque2.offerFirst(priceHistoryForAdvert2);
        advert2.setPriceHistoryDeque(priceHistoryDeque2);

        advert2.setPhoneNumbers(Collections.singletonList("+375292222222"));

        Advert advert4 = new Advert();
        advert4.setAdvertUrl("url4");
        advert4.setTitle("title4");
        advert4.setAddress("address4");
        advert4.setArea(40);
        advert4.setFloor(4);
        advert4.setTotalFloors(5);

        Deque<PriceHistory> priceHistoryDeque3 = new LinkedList<>();
        PriceHistory priceHistoryForAdvert3 = new PriceHistory();
        priceHistoryForAdvert3.setPrice(400);
        priceHistoryForAdvert3.setDate(creationDate);
        priceHistoryDeque3.offerFirst(priceHistoryForAdvert3);
        advert4.setPriceHistoryDeque(priceHistoryDeque3);

        advert4.setPhoneNumbers(Collections.singletonList("+375294444444"));

        advert1.setLastEditDate(creationDate);
        advert2.setLastEditDate(creationDate);
        advert4.setLastEditDate(creationDate);

        AdvertContainer container = new AdvertContainer();
        List<Advert> adverts = new ArrayList<>();
        adverts.add(advert1);
        adverts.add(advert2);
        adverts.add(advert4);
        container.setAdverts(adverts);
        container.setCreationDate(creationDate);

        return container;
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
}
