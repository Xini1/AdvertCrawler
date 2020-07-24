package crawling;

import model.Advert;
import model.PriceHistory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.PageParser;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdvertBuilder implements Callable<Advert> {

    private LocalDate date;
    private String advertUrl;
    private String title;
    private String address;
    private float area;
    private int floor;
    private int totalFloors;
    private PriceHistory priceHistory;
    private List<String> phoneNumbers;

    public AdvertBuilder(LocalDate date, String advertUrl) {
        this.date = date;
        this.advertUrl = advertUrl;
    }

    @Override
    public Advert call() throws Exception {
        parsePage();
        return getAdvert();
    }

    private void parsePage() {
        Document document;
        try {
            document = Jsoup.connect(advertUrl).get();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.WARNING, e, () -> "Could not connect to advert page " + advertUrl);
            return;
        }

        title = new PageParser(document)
                .selectElementsWithClass("div", "title")
                .selectElements("span")
                .getAsTextFirst();

        address = new PageParser(document)
                .selectElementsWithClass("div", "address")
                .getAsTextLast();

        area = parseArea(document);
        floor = parseFloor(document);
        totalFloors = parseTotalFloors(document);
        priceHistory = getPrice(document);
        phoneNumbers = parsePhoneNumber(document);
    }

    private float parseArea(Document document) {
        return new PageParser(document)
                .selectElementsWithClass("div", "square")
                .getAsText()
                .stream()
                .flatMap(s -> Arrays.stream(s.split("[ :]")))
                .filter(string -> string.matches("\\d+(,\\d+)?"))
                .findFirst()
                .map(s -> s.replace(',', '.'))
                .map(Float::parseFloat)
                .orElse(0f);
    }

    private int parseFloor(Document document) {
        List<String> numberStrings = getFloorsFromPage(document);

        return numberStrings.size() == 2 ? Integer.parseInt(numberStrings.get(0)) : 0;
    }

    private int parseTotalFloors(Document document) {
        List<String> numberStrings = getFloorsFromPage(document);

        return numberStrings.size() == 2 ? Integer.parseInt(numberStrings.get(1)) : 0;
    }

    private List<String> getFloorsFromPage(Document document) {
        String elementString = new PageParser(document)
                .selectElementsWithClass("div", "floor")
                .getAsTextFirst();

        return findNumbersInString(elementString);
    }

    private PriceHistory getPrice(Document document) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setDate(date);

        String priceOnPage = new PageParser(document)
                .selectElementsWithClass("div", "price")
                .getAsTextFirst();

        if (!priceOnPage.endsWith("р.")) {
            return priceHistory;
        }

        String priceString = String.join("", findNumbersInString(priceOnPage));

        priceHistory.setPrice(Integer.parseInt(priceString));

        return priceHistory;
    }

    private List<String> parsePhoneNumber(Document document) {
        String phonesOnPage = new PageParser(document)
                .selectElementsWithClass("div", "phones")
                .getAsTextFirst();

        List<String> stringsWithNumbers = Arrays.stream(phonesOnPage.split(","))
                .filter(s -> s.matches(".*\\d+.*"))
                .collect(Collectors.toList());

        return formatPhoneNumbers(stringsWithNumbers);
    }

    private List<String> formatPhoneNumbers(List<String> phoneStrings) {
        return phoneStrings.stream()
                .map(phoneString -> String.join("", findNumbersInString(phoneString)))
                .filter(phoneString -> phoneString.length() >= 6)
                .map(phoneString -> phoneString.startsWith("80") ?
                        phoneString.replaceFirst("80", "+375") : phoneString)
                //.peek(System.out::println)
                .map(phoneString -> phoneString.length() == 6 ?
                        "+375232" + phoneString : phoneString)
                //.peek(System.out::println)
                .filter(phoneString -> phoneString.matches("\\+375\\d{9}"))
                .collect(Collectors.toList());
    }

    private List<String> findNumbersInString(String elementString) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(elementString);

        return matcher.results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
    }

    private Advert getAdvert() {
        Advert advert = new Advert();

        advert.setAdvertUrl(advertUrl);
        advert.setTitle(title);
        advert.setAddress(address);
        advert.setArea(area);
        advert.setFloor(floor);
        advert.setTotalFloors(totalFloors);
        advert.setPhoneNumbers(phoneNumbers);
        advert.setLastRefreshDate(date);
        advert.setFavorite(false);

        Deque<PriceHistory> priceHistoryDeque = new LinkedList<>();
        priceHistoryDeque.offerFirst(priceHistory);
        advert.setPriceHistoryDeque(priceHistoryDeque);

        return advert;
    }
}
