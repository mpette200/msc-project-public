package pfatool.forecaster;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PreProcessorTests {
    private static final String TEST_DATA_FILE = "src/test/resources/data/price_sample_01.tsv";
    private static final String EXPECTED_RESAMPLED = "src/test/resources/data/price_sample_01_res.tsv";
    private static final String EXPECTED_LAGS = "src/test/resources/data/price_sample_01_pp.tsv";
    private static final String CHOSEN_CATEGORY = "g4ad.2xlarge__Linux/UNIX__eu-west-1b";
    private static final int NUM_LAGS = 42;
    private static final int STEP_HOURS = 4;

    private static PreProcessor pp;
    private static Path p;

    @BeforeAll
    static void loadFromFile() throws IOException {
        p = FileSystems.getDefault().getPath("");
        String txtData = Files.readString(p.resolve(TEST_DATA_FILE));
        pp = new PreProcessor(
                PriceData.loadFromTsv(txtData),
                STEP_HOURS,
                NUM_LAGS
        );
    }

    @Test
    void testResampleTimesMatchesPythonTSV() throws IOException {
        PriceFrame actualFrame = pp.resampleTimes();
        String expectedText = Files.readString(p.resolve(EXPECTED_RESAMPLED));
        String actualText = actualFrame.toStringFormatted(
                "date",
                "yyyy-MM-dd HH:mm:ss",
                "%s__price",
                "%.04f",
                "\n",
                "\t"
        );
        // \R matches different types of linebreak
        String[] expected = expectedText.split("\\R");
        String[] actual = actualText.split("\\R");
        assertArrayEquals(expected, actual);
    }

    @Test
    void testMakeLagsMatchesPythonTSV() throws IOException {
        PriceFrame actualFrame = pp.makeLags(CHOSEN_CATEGORY);
        String expectedText = Files.readString(p.resolve(EXPECTED_LAGS));
        String actualText = actualFrame.toStringFormatted(
                "date",
                "yyyy-MM-dd HH:mm:ss",
                CHOSEN_CATEGORY + "__price__%s",
                "%.04f",
                "\n",
                "\t"
        );
        // \R matches different types of linebreak
        String[] expected = expectedText.split("\\R");
        String[] actual = actualText.split("\\R");
        assertArrayEquals(expected, actual);
    }

}
