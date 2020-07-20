import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdvertCrawler {

    private String searchResultsUrl;
    private ExecutorService executorService;
    private Random random;

    public AdvertCrawler(String searchResultsUrl) {
        this.searchResultsUrl = searchResultsUrl;
        executorService = Executors.newCachedThreadPool();
        random = new Random();
    }

    public AdvertContainer getAdvertContainer() {
        int pagesTotal = getPagesTotal();

        List<String> advertUrls = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(pagesTotal);
        for (int i = 1; i <= pagesTotal; i++) {
            String pageUrl = searchResultsUrl + i + '/';
            Runnable searchPageScanner = new SearchPageScanner(pageUrl, advertUrls, latch);
            executorService.execute(searchPageScanner);
            try {
                Thread.sleep(random.nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Waiting for executing page scanners was interrupted", e);
        }
        executorService.shutdown();
        advertUrls.forEach(System.out::println);
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
                .orElse(0);
    }
}
