package crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.PageParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchPageScanner implements Callable<List<String>> {

    private String pageUrl;

    public SearchPageScanner(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    @Override
    public List<String> call() throws Exception {
        Document document;
        try {
            document = Jsoup.connect(pageUrl).get();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName())
                    .log(Level.WARNING, e, () -> "Could not connect to search results page " + pageUrl);
            return Collections.emptyList();
        }

        return new PageParser(document)
                .selectElementsWithClass("div", "title")
                .getLinks();
    }
}
