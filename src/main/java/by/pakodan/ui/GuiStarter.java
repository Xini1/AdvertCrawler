package by.pakodan.ui;

import by.pakodan.model.AdvertContainer;
import by.pakodan.ui.controller.MainWindowController;
import by.pakodan.utils.FileUtils;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GuiStarter extends Application {

    public static final String ADVERT_CONTAINER_SAVE_PATH = AdvertContainer.class.getName() + ".csv";

    public static void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdvertCrawlerMainWindow.fxml"));

        Parent root = loader.load();
        MainWindowController controller = loader.getController();

        FileUtils fileUtils = new FileUtils();
        AdvertContainer container = AdvertContainer.parseCsv(fileUtils.readFromFile(ADVERT_CONTAINER_SAVE_PATH));
        controller.setContainer(container);
        stage.setOnShowing(windowEvent -> controller.changeView());
        stage.setOnHiding(windowEvent -> fileUtils.writeToFile(controller.getContainer().toCsv(),
                ADVERT_CONTAINER_SAVE_PATH));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Объявления о продаже квартир в г. Гомель на сайте www.moyareklama.by");
        stage.getIcons().add(new Image("file:advert_crawler.png"));

        stage.getProperties().put(HostServices.class, getHostServices());
        stage.show();
    }
}
