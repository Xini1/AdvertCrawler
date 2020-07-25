package by.advertcrawler.ui;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UiStarter extends Application {

    public static void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/AdvertCrawlerMainWindow.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Объявления о продаже квартир на сайте www.moyareklama.by");

        stage.getProperties().put(HostServices.class, getHostServices());

        stage.show();
    }
}
