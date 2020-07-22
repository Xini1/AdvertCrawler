import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdvertBuilder implements Callable<Advert> {

    private String advertUrl;
    private String title;
    private String address;
    private float area;
    private int floor;
    private int totalFloors;
    private int price;
    private String phoneNumber;

    public AdvertBuilder(String advertUrl) {
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

        System.out.println(advertUrl + ' ' + floor + ' ' + totalFloors);
    }

    private float parseArea(Document document) {
        return new PageParser(document)
                .selectElementsWithClass("div", "square")
                .getAsText()
                .stream()
                .flatMap(s -> Arrays.stream(s.split(" |:")))
                .filter(string -> string.matches("\\d+(,\\d+)?"))
                .findFirst()
                .map(s -> s.replace(',', '.'))
                .map(Float::parseFloat)
                .orElse(0f);
    }

    private int parseFloor(Document document) {
        List<Integer> numbers = getFloorsFromPage(document);

        return numbers.size() > 0 ? numbers.get(0) : 0;
    }

    private int parseTotalFloors(Document document) {
        List<Integer> numbers = getFloorsFromPage(document);

        return numbers.size() > 1 ? numbers.get(1) : 0;
    }

    private List<Integer> getFloorsFromPage(Document document) {
        String elementString = new PageParser(document)
                .selectElementsWithClass("div", "floor")
                .getAsTextFirst();

        return findNumbersInString(elementString);
    }

    private List<Integer> findNumbersInString(String elementString) {
        List<Integer> number = new ArrayList<>();

        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(elementString);

        return matcher.results()
                .map(MatchResult::group)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private Advert getAdvert() {
        Advert advert = new Advert();
        return advert;
    }
}
