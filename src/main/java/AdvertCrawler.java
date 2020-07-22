import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AdvertCrawler {

    private String searchResultsUrl;
    private ExecutorService executorService;
    private Random random;

    public AdvertCrawler(String searchResultsUrl) {
        this.searchResultsUrl = searchResultsUrl;
        executorService = Executors.newFixedThreadPool(10);
        random = new Random();
    }

    public AdvertContainer getAdvertContainer() {
        int pagesTotal = getPagesTotal();

        List<Callable<List<String>>> tasks = new ArrayList<>();
        for (int i = 1; i <= pagesTotal; i++) {
            String pageUrl = searchResultsUrl + i + '/';
            tasks.add(new SearchPageScanner(pageUrl, random));
        }

        List<Future<List<String>>> futures;
        try {
            futures = executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Interrupted waiting for scanning search results pages", e);
            return null;
        }

        List<String> advertLinks = futures.stream()
                .map(listFuture -> {
                    try {
                        return listFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Logger.getLogger(getClass().getName())
                                .log(Level.SEVERE, "Interrupted getting links", e);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        executorService.shutdown();

        System.out.println(advertLinks.size());
        advertLinks.forEach(System.out::println);

        return null;
    }

    private int getPagesTotal() {
        Document document;
        try {
            document = Jsoup.connect(searchResultsUrl).get();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Could not connect to search results page", e);
            return 0;
        }

        PageParser parser = new PageParser(document);
        List<String> pages = parser.selectElementsWithClass("div", "page-link").getAsText();
        System.out.println("pages " + pages);
        return pages.stream()
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(1);
    }
}
