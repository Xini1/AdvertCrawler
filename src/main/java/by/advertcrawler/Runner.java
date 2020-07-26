package by.advertcrawler;

import by.advertcrawler.ui.GuiStarter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Runner {

    public static void main(String[] args) {
        loadLoggingProperties();

        try {
            GuiStarter.run(args);
        } catch (Exception e) {
            Logger.getLogger(Runner.class.getName())
                    .log(Level.SEVERE, "Unexpected exception occurred", e);
        }
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
