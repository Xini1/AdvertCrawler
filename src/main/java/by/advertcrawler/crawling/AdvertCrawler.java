package by.advertcrawler.crawling;

import by.advertcrawler.model.Advert;
import by.advertcrawler.model.AdvertContainer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import by.advertcrawler.utils.PageParser;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AdvertCrawler {

    private String searchResultsUrl;
    private LocalDate date;
    private ExecutorService executorService;

    private Logger logger = Logger.getLogger(getClass().getName());

    public AdvertCrawler(String searchResultsUrl) {
        this.searchResultsUrl = searchResultsUrl;
        date = LocalDate.now();
        executorService = Executors.newFixedThreadPool(10);
    }

    public AdvertContainer getAdvertContainer() {
        AdvertContainer container = new AdvertContainer();
        container.setCreationDate(date);
        int pagesTotal = getPagesTotal();
        try {
            List<String> advertLinks = getAdvertLinks(pagesTotal);
            List<Advert> adverts = parseAdverts(advertLinks);
            container.setAdverts(adverts);
            executorService.shutdown();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Interrupted while waiting", e);
            Thread.currentThread().interrupt();
        }
        return container;
    }

    private int getPagesTotal() {
        Document document;
        try {
            document = Jsoup.connect(searchResultsUrl).get();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "Could not connect to search results page " + searchResultsUrl);
            return 0;
        }

        List<String> pages = new PageParser(document)
                .selectElementsWithClass("div", "page-link")
                .getAsText();

        return pages.stream()
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(1);
    }

    private List<String> getAdvertLinks(int pagesTotal) throws InterruptedException {
        List<Callable<List<String>>> tasks = new ArrayList<>();

        for (int i = 1; i <= pagesTotal; i++) {
            String pageUrl = searchResultsUrl + i + '/';
            tasks.add(new SearchPageScanner(pageUrl));
        }

        List<Future<List<String>>> futures = executorService.invokeAll(tasks);

        return futures.stream()
                .map(listFuture -> {
                    try {
                        return listFuture.get();
                    } catch (InterruptedException e) {
                        logger.log(Level.WARNING, "Interrupted getting links", e);
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        logger.log(Level.WARNING, "Retrieval of page scanning result was aborted", e);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Advert> parseAdverts(List<String> advertLinks) throws InterruptedException {
        List<Callable<Advert>> tasks = advertLinks.stream()
                .map(advertUrl -> new AdvertBuilder(date, advertUrl))
                .collect(Collectors.toList());

        List<Future<Advert>> futures = executorService.invokeAll(tasks);

        return futures.stream()
                .map(listFuture -> {
                    try {
                        return listFuture.get();
                    } catch (InterruptedException e) {
                        logger.log(Level.WARNING, "Interrupted getting adverts", e);
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        logger.log(Level.WARNING, "Retrieval of advert building result was aborted", e);
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
