package by.advertcrawler.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    public void writeToFile(String text, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(text);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, e, () -> "Could not write to file " + fileName);
        }
    }

    public String readFromFile(String fileName) {
        List<String> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, e, () -> "Could not read from file " + fileName);
        }

        return String.join("\n", lines);
    }
}
