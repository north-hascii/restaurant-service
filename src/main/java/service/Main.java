package service;

import service.util.info.Configurator;
import service.util.info.DataBase;
import service.util.env.Theme;

import java.io.File;


public class Main {
    // Using colors:
    // Example:    Theme.print("text", Theme.CYAN);
    //
    // Green - create an Agent
    // Red - delete an Agent
    // Cyan - Some Info
    // Purple - Some Info
    // Yellow - Some Info
    // Blue -


    // 0 - Nothing
    // 1 - Some Information
    // 2 - Information
    public static Integer storageInfo = 1;

    public static DataBase db;

    public static void main(String[] args) {
        // Delete logs
        File file = new File("logs");
        deleteDirectoryRec(file);

        // Read Database
        Configurator configurator = new Configurator();
        boolean isDataBaseCorrect = configurator.setUpDataBase();

        if (isDataBaseCorrect) {
            // MAS starts
            MainController mainController = new MainController();
            mainController.initAgents();
        } else {
            Theme.print("The restaurant is closed due to an input error", Theme.RED);
        }
    }

    private static void deleteDirectoryRec(File file) {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectoryRec(entry);
                }
            }
        }
        if (!file.isDirectory()) {
            file.delete();
        }
    }
}