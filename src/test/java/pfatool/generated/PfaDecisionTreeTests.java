package pfatool.generated;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PfaDecisionTreeTests {

    private static PfaDecisionTree y0Tree;
    private static PfaDecisionTree y1Tree;
    private static String inputData;
    private static String outputData;

    @BeforeAll
    static void loadData() {
        String y0File = "pfa/sample_01/price_sample_01_y0_tree";
        String y1File = "pfa/sample_01/price_sample_01_y1_tree";
        String xData = "pfa/sample_01/price_sample_01_pp.tsv";
        String yData = "pfa/sample_01/price_sample_01_y.tsv";
        y0Tree = PfaDecisionTree.getInstance(y0File);
        y1Tree = PfaDecisionTree.getInstance(y1File);
        inputData = readFile(xData);
        outputData = readFile(yData);
    }

    private static String readFile(String resourceName) {
        URL url =ClassLoader.getSystemResource(resourceName);
        try {
            return Files.readString(Path.of(url.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("error reading: " + resourceName);
        }
    }

    private double[] getInputRow(int i) {
        // \R matches different types of linebreak
        String[] lines = inputData.split("\\R");
        String[] fields = lines[i].split("\\t");
        return Arrays.stream(fields, 1, fields.length)
                .mapToDouble(Double::parseDouble)
                .toArray();
    }

    private double getOutputValue(int i, int j) {
        // \R matches different types of linebreak
        String[] lines = outputData.split("\\R");
        String[] fields = lines[i].split("\\t");
        return Double.parseDouble(fields[j]);
    }

    @Test
    void testTreeY0FirstRow() {
        double y0 = y0Tree.predict(getInputRow(1));
        double expected = getOutputValue(1, 1);
        assertTrue(Math.abs(y0 - expected) < 0.0001, y0 + " not close to " + expected);
    }

    @Test
    void testTreeY0LastRow() {
        double y0 = y0Tree.predict(getInputRow(453));
        double expected = getOutputValue(453, 1);
        assertTrue(Math.abs(y0 - expected) < 0.0001, y0 + " not close to " + expected);
    }

    @Test
    void testTreeY1FirstRow() {
        double y1 = y1Tree.predict(getInputRow(1));
        double expected = getOutputValue(1, 2);
        assertTrue(Math.abs(y1 - expected) < 0.0001, y1 + " not close to " + expected);
    }

    @Test
    void testTreeY1LastRow() {
        double y1 = y1Tree.predict(getInputRow(453));
        double expected = getOutputValue(453, 2);
        assertTrue(Math.abs(y1 - expected) < 0.0001, y1 + " not close to " + expected);
    }

}
