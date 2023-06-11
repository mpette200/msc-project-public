package pfatool.forecaster;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Holds the list of supported categories, after reading it from the configuration file
 */
public class SupportedCategories {
    private final List<String> categories;

    /**
     * Load the list of supported categories from the data file.
     *
     * @param filename Data file containing each category on a single line
     */
    public SupportedCategories(String filename) {
        categories = readFile(filename);
    }

    /**
     * Get the list of supported categories.
     */
    List<String> getCategories() {
        return categories;
    }

    /**
     * Helper method to read data from the given filename
     */
    private List<String> readFile(String filename) {
        URL url = getClass().getClassLoader().getResource(filename);
        Objects.requireNonNull(url, "file not found: " + filename);
        try (
                Reader r = new InputStreamReader(url.openStream());
                BufferedReader br = new BufferedReader(r);
                Scanner sc = new Scanner(br);
        ) {
            // \v is any vertical whitespace
            sc.useDelimiter("\\v+");
            return sc.tokens().toList();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
