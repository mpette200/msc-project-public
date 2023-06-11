package pfatool.forecaster;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of pre-processing steps to prepare historical price data
 * for forecasting
 */
public class PreProcessor {
    private final List<PriceData> inputData;
    private final int numLags;
    private final int timeStepHours;

    /**
     * Create a pre-processor instance based on the given data.
     *
     * @param inputData - list of historical price data records
     * @param timeStepHours - number of time steps ahead to make forecast
     * @param numLags - number of previous lag terms to include
     */
    public PreProcessor(List<PriceData> inputData,
                        int timeStepHours,
                        int numLags) {
        this.inputData = inputData.stream()
                .sorted((a, b) -> a.date().compareTo(b.date()))
                .toList();
        this.numLags = numLags;
        this.timeStepHours = timeStepHours;
    }

    /**
     * Create lag terms for the given category name.
     *
     * @param categoryName category to process.
     * @return PriceFrame structure with the processed data.
     */
    public PriceFrame makeLags(String categoryName) {
        PriceFrame resampled = resampleTimes();
        if (resampled.getNumRows() <= numLags) {
            throw new IllegalStateException("Insufficient data. Need numRows at least: " + numLags);
        }
        final int colIdx = resampled.getColumnNames().indexOf(categoryName);
        if (colIdx == -1) {
            throw new IllegalArgumentException("Could not find column name: " + categoryName);
        }
        List<String> lagNames = IntStream.range(0, numLags)
                .mapToObj("lag_%02d"::formatted)
                .toList();
        PriceFrame lagFrame = new PriceFrame(lagNames);

        for (int i = numLags; i < resampled.getNumRows(); i++) {
            double[] values = IntStream.range(i - numLags, i)
                    .mapToDouble(j -> resampled.getValue(j + 1, colIdx) /
                            resampled.getValue(j, colIdx))
                    .toArray();
            lagFrame.add(
                    resampled.getDate(i),
                    values
            );
        }
        return lagFrame;
    }

    /**
     * Compute rolling average (mean) for the given category
     *
     * @param category category to process.
     * @return rolling mean
     */
    public double getRollingMean(String category) {
        PriceFrame resampled = resampleTimes();
        final int colIdx = resampled.getColumnNames().indexOf(category);
        if (colIdx == -1) {
            throw new IllegalArgumentException("Could not find column name: " + category);
        }
        int nRows = resampled.getNumRows();
        double sum = IntStream.range(nRows - numLags, nRows)
                .mapToDouble(i -> resampled.getValue(i, colIdx))
                .sum();
        return sum / numLags;
    }

    /**
     * Package private method for testing.
     * Transforms a series of pricing data from having a single value column
     * to a wide format with multiple columns and all timestamps aligned to a regular
     * interval. Data values filled using a forward-fill method.
     * Should be equivalent to the Python operations using Pandas library
     * <a href="https://pandas.pydata.org/docs/index.html">https://pandas.pydata.org/docs/index.html</a>
     * <pre>{@code
     *     df.pivot(columns='category', values='price') \
     *         .fillna(method='ffill') \
     *         .resample('4h', origin=start_date) \
     *         .ffill() \
     *         .dropna(axis=0)
     * }</pre>
     * @return resampled data
     */
    PriceFrame resampleTimes() {
        // round start time to 00:00 of first day
        OffsetDateTime firstDate = inputData.get(0).date();
        OffsetDateTime startDate = OffsetDateTime.of(
                firstDate.getYear(),
                firstDate.getMonthValue(),
                firstDate.getDayOfMonth(),
                0, 0, 0, 0,
                ZoneOffset.UTC
        );

        // temporary structure to hold
        // i (column number)
        // prev (last seen value) - used to forward fill when resampling
        class ColumnData {
            final int i;
            double prev;
            ColumnData(int i) {
                this.i = i;
                this.prev = -1;
            }
        }

        // create map of column names
        SortedSet<String> columnNames = makeColumnNames();
        Map<String, ColumnData> categoryMap = new HashMap<>();
        int i = 0;
        for (String name : columnNames) {
            categoryMap.put(name, new ColumnData(i));
            i++;
        }
        PriceFrame resampled = new PriceFrame(columnNames);

        // iterate through data and remember previously seen values
        // to use for forward filling
        int numCols = columnNames.size();
        OffsetDateTime curDate = startDate;
        boolean mapFilled = false;
        for (PriceData d : inputData) {
            if (!mapFilled) {
                // only want to do the time-consuming check once
                mapFilled = categoryMap.entrySet()
                        .stream()
                        .allMatch(x -> x.getValue().prev >= 0);
            }
            while (d.date().compareTo(curDate) > 0) {
                if (mapFilled) {
                    double[] values = new double[numCols];
                    for (Map.Entry<String, ColumnData> e : categoryMap.entrySet()) {
                        values[e.getValue().i] = e.getValue().prev;
                    }
                    resampled.add(curDate, values);
                }
                curDate = curDate.plusHours(timeStepHours);
            }
            categoryMap.get(makeCategoryName(d)).prev = d.price();
        }
        return resampled;
    }

    /**
     * Create a category name by joining the three parts with double underscore
     * eg. c5d.xlarge__SUSE Linux__eu-west-1a
     */
    private String makeCategoryName(PriceData d) {
        return String.join(
                "__",
                d.instanceType(),
                d.instanceDescription(),
                d.regionZone()
        );
    }

    /**
     * Create list of column names sorted into order
     */
    private SortedSet<String> makeColumnNames() {
        return inputData.stream()
                .map(this::makeCategoryName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public static void main(String[] args) throws IOException {
        PriceFrame lags = new PriceFrame(List.of(
                "g00", "g01", "g02", "g03", "g04", "g05"
        ));
        OffsetDateTime date = OffsetDateTime.of(2022, 1, 1, 15, 30, 0, 0, ZoneOffset.UTC);
        lags.add(date, new double[] {2.1, 2.2, 2.3, 2.4, 2.5, 2.6});
        lags.add(date, new double[] {5.1, 5.2, 5.3, 5.4, 5.5, 5.6});
        System.out.println(lags);

        String filename = "src/test/resources/data/price_sample_01.tsv";
        Path p = FileSystems.getDefault().getPath("");
        String txtData = Files.readString(p.resolve(filename));
        PreProcessor pp = new PreProcessor(
                PriceData.loadFromTsv(txtData),
                4,
                42
        );
        System.out.println(pp.resampleTimes().subSet(0, 5));
        System.out.println("---");
        PriceFrame df = pp.resampleTimes();
        int numRows = df.getNumRows();
        System.out.println(df.subSet(numRows - 5, numRows));
        String reformatted = df.subSet(numRows - 5, numRows)
                .toStringFormatted(
                        "date",
                        "yyyy-MM-dd HH:mm:ss",
                        "%s__price",
                        "%s",
                        "\n",
                        "\t"
                );
        System.out.println(reformatted);

        PriceFrame df2 = pp.makeLags("g4ad.2xlarge__Linux/UNIX__eu-west-1b");
        int nRows2 = df2.getNumRows();
        System.out.println(df2.subSet(0, 5));
        System.out.println("---");
        System.out.println(df2.subSet(nRows2 - 5, nRows2));
    }
}
