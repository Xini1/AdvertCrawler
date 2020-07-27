package by.pakodan.crawling;

import by.pakodan.model.Advert;
import by.pakodan.model.AdvertContainer;
import by.pakodan.utils.AdvertContainerMerger;
import by.pakodan.utils.PageParser;
import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AdvertCrawlerTask extends Task<AdvertContainer> {

    private String searchResultsUrl;
    private LocalDate date;
    private ExecutorService executorService;
    private AdvertContainer oldContainer;

    private volatile int targetProgress;
    private volatile int currentProgress;

    private Logger logger = Logger.getLogger(getClass().getName());

    public AdvertCrawlerTask() {
        date = LocalDate.now();
        executorService = Executors.newFixedThreadPool(10);
    }

    public void setSearchResultsUrl(String searchResultsUrl) {
        this.searchResultsUrl = searchResultsUrl;
    }

    public void setOldContainer(AdvertContainer oldContainer) {
        this.oldContainer = oldContainer;
    }

    @Override
    protected AdvertContainer call() throws Exception {
        updateMessage("Подготовка...");

        AdvertContainer container = new AdvertContainer();
        container.setCreationDate(date);

        updateMessage("Получаем количество страниц в поиске...");
        int pagesTotal = getPagesTotal();
        logger.info(() -> "Total number of pages: " + pagesTotal);

        targetProgress = pagesTotal * 21;
        currentProgress = 0;
        updateProgress(currentProgress, targetProgress);

        updateMessage("Получаем ссылки на объявления...");
        List<String> advertLinks = getAdvertLinks(pagesTotal);
        targetProgress = pagesTotal + advertLinks.size();
        logger.info(() -> "Total number of collected advert urls: " + advertLinks.size());

        updateMessage("Сканируем страницы с объявлениями...");
        List<Advert> adverts = parseAdverts(advertLinks);
        logger.info(() -> "Total numbers of parsed adverts: " + adverts.size());

        container.setAdverts(adverts);
        executorService.shutdown();

        updateMessage("Выполняем слияние объявлений....");
        AdvertContainerMerger merger = new AdvertContainerMerger();
        AdvertContainer mergedContainer = merger.merge(oldContainer, container);
        updateProgress(targetProgress, targetProgress);

        updateMessage("Готово!");

        return mergedContainer;
    }

    public int getPagesTotal() {
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

    public List<String> getAdvertLinks(int pagesTotal) throws InterruptedException {
        List<Callable<List<String>>> tasks = new ArrayList<>();

        for (int i = 1; i <= pagesTotal; i++) {
            String pageUrl = searchResultsUrl + i + '/';
            tasks.add(new SearchPageScanner(pageUrl, this));
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

    public List<Advert> parseAdverts(List<String> advertLinks) throws InterruptedException {
        List<Callable<Advert>> tasks = advertLinks.stream()
                .map(advertUrl -> new AdvertBuilder(date, advertUrl, this))
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

    public synchronized void updateProgressProperty() {
        currentProgress++;
        updateProgress(currentProgress, targetProgress);
    }
}
