import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Runner {

    public static void main(String[] args) {
        loadLoggingProperties();

        String searchResultsUrl="https://www.moyareklama.by/search/%D0%93%D0%BE%D0%BC%D0%B5%D0%BB%D1%8C/c56950abe5d86434d5e08455380399be/";
        AdvertCrawler crawler = new AdvertCrawler(searchResultsUrl);
        crawler.getAdvertContainer();
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
