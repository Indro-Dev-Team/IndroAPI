package indroDevTeam.indrocraftLib.utils;

import java.io.File;
import java.io.IOException;

public class YamlUtils {
    private final File directory;

    public YamlUtils(File directory) {
        this.directory = directory;
    }

    public void createFile(String fileName) {
        try {
            File file1 = new File(directory.getAbsolutePath() + File.separator + fileName + ".yml");
            if (file1.createNewFile()) {
                System.out.println("File created: " + file1.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Save a default file saved in a file
     * @param file The default file saved in a plugin
     * @param fileName The file that you want to save in it
     */
    public void saveDefaultFile(File file, String fileName) {
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void getData() {

    }
}