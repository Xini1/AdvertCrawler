import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchPageScanner implements Runnable {

    private String pageUrl;
    private List<String> advertUrls;
    private CountDownLatch latch;

    public SearchPageScanner(String pageUrl, List<String> advertUrls, CountDownLatch latch) {
        this.pageUrl = pageUrl;
        this.advertUrls = advertUrls;
        this.latch = latch;
    }

    @Override
    public void run() {
        Document document;
        try {
            document = Jsoup.connect(pageUrl).get();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Could not connect to search results page", e);
            latch.countDown();
            return;
        }

        PageParser parser = new PageParser(document);
        List<String> text = parser.selectElementsWithClass("div", "title").getAsText();
        System.out.println(pageUrl + " Titles: " + text);
        advertUrls.addAll(text);
        latch.countDown();
    }
}
