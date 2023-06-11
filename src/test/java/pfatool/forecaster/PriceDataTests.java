package pfatool.forecaster;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PriceDataTests {

    private static final String TEST_DATA_FILE = "src/test/resources/data/price_sample_01.tsv";
    private static String txtData;

    @BeforeAll
    static void loadFromFile() throws IOException {
        Path p = FileSystems.getDefault().getPath("");
        txtData = Files.readString(p.resolve(TEST_DATA_FILE));
    }

    private String formatRow(PriceData row) {
        String datePattern = "yyyy-MM-dd'T'HH:mm:ssxxx";
        return String.format(
                "%s %.03f %s %s %s",
                DateTimeFormatter.ofPattern(datePattern).format(row.date()),
                row.price(),
                row.instanceType(),
                row.instanceDescription(),
                row.regionZone()
        );
    }

    @Test
    void testLoadFromTsvFirstAndLastRows() {
        List<PriceData> data = PriceData.loadFromTsv(txtData);
        String row0 = formatRow(data.get(0));
        String rowLast = formatRow(data.get(data.size() - 1));
        assertEquals(
                "2022-06-07T09:05:40+00:00 0.487 g4ad.4xlarge Red Hat Enterprise Linux eu-west-1c",
                row0
        );
        assertEquals(
                "2022-03-09T10:59:13+00:00 0.669 g4ad.8xlarge Linux/UNIX eu-west-1a",
                rowLast
        );
    }

    @Test
    void testLoadFromTsvErrorWithQuotes() {
        String doubleQuote = "\"";
        String input = "a\t"
                + doubleQuote + "def" + doubleQuote
                + "\n ghi";
        assertThrows(
                IllegalArgumentException.class,
                () -> PriceData.loadFromTsv(input)
        );
    }
}
