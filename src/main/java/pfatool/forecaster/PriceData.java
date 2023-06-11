package pfatool.forecaster;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Data structure to hold the historical price records obtained from
 * the AWS spot price history.
 */
public record PriceData(OffsetDateTime date,
                        double price,
                        String instanceType,
                        String instanceDescription,
                        String regionZone) {

    private static final String DOUBLE_QUOTE = "\"";

    /**
     * Load Price data from tab separated text. With fields:
     * omit, price, date, instance_type, instance_description, region_zone
     * Must use \n as line separator and tab as field separator.
     * Quoted values not supported.
     *
     * @param tsvData input as text
     * @return List of PriceData
     * @throws IllegalArgumentException if quotes exists in the input text
     */
    public static List<PriceData> loadFromTsv(String tsvData) {
        return getRows(tsvData)
                .map(PriceData::parseRow)
                .toList();
    }

    /**
     * Helper method to split at line breaks.
     */
    private static Stream<String> getRows(String tsvData) {
        // \R matches different types of linebreak
        return Arrays.stream(tsvData.split("\\R"));
    }

    /**
     * Parse row by splitting at tabs.
     */
    private static PriceData parseRow(String row) {
        if (row.contains(DOUBLE_QUOTE)) {
            throw new IllegalArgumentException("Quoted strings not supported: " + row);
        }
        String[] parts = row.split("\\t");
        return new PriceData(
                OffsetDateTime.parse(parts[2]),
                Double.parseDouble(parts[1]),
                parts[3],
                parts[4],
                parts[5]
        );
    }

}
