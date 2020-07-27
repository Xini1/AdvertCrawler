package by.pakodan;

import by.pakodan.ui.GuiStarter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Runner {

    private static final Logger logger = Logger.getLogger(Runner.class.getName());


    public static void main(String[] args) {
        loadLoggingProperties();

        try {
            GuiStarter.run(args);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected exception occurred", e);
        }
    }

    private static void loadLoggingProperties() {
        try {
            LogManager.getLogManager().readConfiguration(
                    new FileInputStream("logging.properties"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load logging.properties file", e);
        }
    }
}
