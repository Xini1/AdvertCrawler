import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchPageScanner implements Callable<List<String>> {

    private String pageUrl;
    private Random random;

    public SearchPageScanner(String pageUrl, Random random) {
        this.pageUrl = pageUrl;
        this.random = random;
    }

    @Override
    public List<String> call() throws Exception {
        Document document;
        try {
            document = Jsoup.connect(pageUrl).get();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Could not connect to search results page", e);
            return Collections.emptyList();
        }

        return new PageParser(document)
                .selectElementsWithClass("div", "title")
                .getLinks();
    }
}
