package pfatool.forecaster;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Data structure to hold tabular data with generic column names
 */
public class PriceFrame {

    private final List<String> columnNames;
    private final List<OffsetDateTime> dates;
    private final List<double[]> priceValues;
    private final int numCols;

    /**
     * Create a new PriceFrame based on the given column names.
     *
     * @param columnNames column headings of the tabular data
     */
    public PriceFrame(Collection<String> columnNames) {
        this.columnNames = columnNames.stream().toList();
        this.dates = new ArrayList<>();
        this.priceValues = new ArrayList<>();
        this.numCols = columnNames.size();
    }

    /**
     * Add a row of data to the table.
     *
     * @param dateTime date and time of record
     * @param values array of values comprising a single row in the table
     */
    public void add(OffsetDateTime dateTime,
                    double[] values) {
        if (values.length != numCols) {
            throw new IllegalArgumentException(
                    "Mismatched number of columns: " + values.length + " and " + numCols
            );
        }
        dates.add(dateTime);
        priceValues.add(values);
    }

    /**
     * Returns a copy of the list of column names
     */
    public List<String> getColumnNames() {
        return columnNames.stream().toList();
    }

    /**
     * Returns the date at index i
     */
    public OffsetDateTime getDate(int i) {
        return dates.get(i);
    }

    /**
     * Returns the number of columns in the table
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Returns the number of rows in the table
     */
    public int getNumRows() {
        return dates.size();
    }

    /**
     * Returns the value at row i and column j of the table
     */
    public double getValue(int i, int j) {
        return priceValues.get(i)[j];
    }

    /**
     * Returns a copy of the entire row at row index i
     */
    public double[] getValues(int i) {
        return Arrays.copyOf(priceValues.get(i), getNumCols());
    }

    /**
     * Returns a subset of the PriceFrame. Makes a copy each time so is not very efficient.
     * @param start start index inclusive
     * @param stop stop index exclusive
     * @return new PriceFrame
     */
    public PriceFrame subSet(int start,
                             int stop) {
        PriceFrame out = new PriceFrame(columnNames);
        for (int i = start; i < stop; i++) {
            out.add(dates.get(i), priceValues.get(i));
        }
        return out;
    }

    /**
     * Return a string representation of the data in the table
     */
    @Override
    public String toString() {
        return IntStream.range(0, dates.size())
                .parallel()
                .mapToObj(this::formatRow)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Return a formatted string representation of data.
     * @param dateColName display name of date column
     * @param datePattern pattern for dates as {@link java.time.format.DateTimeFormatter}
     * @param colNameFormat format for column names as {@link java.util.Formatter}
     * @param valueFormat format for values as {@link java.util.Formatter}
     * @param lineSeparator line separator
     * @param fieldSeparator field separator
     * @return text output of data
     */
    public String toStringFormatted(String dateColName,
                                    String datePattern,
                                    String colNameFormat,
                                    String valueFormat,
                                    String lineSeparator,
                                    String fieldSeparator) {
        String headerRow = formatHeader(
                dateColName,
                colNameFormat,
                fieldSeparator
        );
        return headerRow + lineSeparator + IntStream.range(0, dates.size())
                .parallel()
                .mapToObj(i -> formatRow(
                        i,
                        datePattern,
                        valueFormat,
                        fieldSeparator
                ))
                .collect(Collectors.joining(lineSeparator))
                + lineSeparator;
    }

    /**
     * Helper method to format the string output
     */
    private String formatRow(int i,
                             String datePattern,
                             String valueFormat,
                             String fieldSeparator) {

        String dateText = DateTimeFormatter
                .ofPattern(datePattern)
                .format(dates.get(i));

        double[] values = priceValues.get(i);
        String lagValues = Arrays.stream(values)
                .parallel()
                .mapToObj(valueFormat::formatted)
                .collect(Collectors.joining(fieldSeparator));

        return dateText + fieldSeparator + lagValues;
    }

    /**
     * Helper method to provide a default string format
     */
    private String formatRow(int i) {
        double[] values = priceValues.get(i);
        String lagValues = IntStream.range(0, numCols)
                .parallel()
                .mapToObj(x -> String.format("%s=%s", columnNames.get(x), values[x]))
                .collect(Collectors.joining(", "));

        return String.format(
                "(date=%s, %s)",
                dates.get(i),
                lagValues
        );
    }

    /**
     * Return a formatted string representation of headers
     *
     * @param dateColName display name of date column
     * @param colNameFormat format for column names as {@link java.util.Formatter}
     * @param fieldSeparator field separator
     * @return text representation of column names
     */
    public String formatHeader(String dateColName,
                                String colNameFormat,
                                String fieldSeparator) {
        return dateColName + fieldSeparator + columnNames.stream()
                .map(colNameFormat::formatted)
                .collect(Collectors.joining(fieldSeparator));
    }

}
