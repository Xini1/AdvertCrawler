package by.pakodan.ui.controller;

import by.pakodan.model.AdvertContainer;
import by.pakodan.crawling.AdvertCrawlerTask;
import by.pakodan.ui.GuiStarter;
import by.pakodan.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RefreshAdvertContainerWindowController {

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressBar progressBar;

    private MainWindowController mainWindowController;
    private AdvertContainer oldContainer;
    private AdvertCrawlerTask task;
    private ExecutorService executorService;

    public static final String SEARCH_RESULTS_URL = "https://www.moyareklama.by/search/" +
            "%D0%93%D0%BE%D0%BC%D0%B5%D0%BB%D1%8C/c56950abe5d86434d5e08455380399be/";

    private final Logger logger = Logger.getLogger(getClass().getName());

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public void setOldContainer(AdvertContainer oldContainer) {
        this.oldContainer = oldContainer;
    }

    @FXML
    public void initialize() {
        task = new AdvertCrawlerTask();
        task.setOnSucceeded(workerStateEvent -> updateAdvertContainer());

        executorService = Executors.newSingleThreadExecutor();

        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
    }

    public void run() {
        task.setSearchResultsUrl(SEARCH_RESULTS_URL);
        task.setOldContainer(oldContainer);
        executorService.submit(task);
    }

    private void updateAdvertContainer() {
        backupContainer();
        AdvertContainer newContainer = task.getValue();
        mainWindowController.setContainer(newContainer);
        mainWindowController.changeView();
        progressBar.getScene().getWindow().hide();
    }

    private void backupContainer() {
        FileUtils fileUtils = new FileUtils();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, hh-mm");

        File backupsDirectory = new File("backups");
        if (!backupsDirectory.exists() && !backupsDirectory.mkdir()) {
            logger.warning("Could not create backups directory");
            return;
        }

        String destination = String.format("backups/%s(%s).csv", oldContainer.getClass().getName(),
                formatter.format(LocalDateTime.now()));

        fileUtils.writeToFile(oldContainer.toCsv(), destination);
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
