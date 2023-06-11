package app.ec2prices;

import pfatool.forecaster.PriceData;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSpotPriceHistoryResponse;
import software.amazon.awssdk.services.ec2.model.SpotPrice;

import java.io.Closeable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Scanner;

/**
 * Fetches live spot prices using the Amazon AWS EC2 SDK.
 */
public class LiveSpotPrices implements Closeable {

    /**
     * Lock is on the class as a whole so only one instance at a time can fetch prices.
     */
    private static final Class<LiveSpotPrices> lock = LiveSpotPrices.class;
    private final Ec2Client ec2;

    /**
     * Create a new instance of LiveSpotPrices. Although possible for multiple instances
     * to exist, only one instance may query data at a time.
     */
    public LiveSpotPrices() {
        synchronized (lock) {
            ec2 = Ec2Client.builder()
                    .region(Region.EU_WEST_1)
                    .credentialsProvider(CredentialsProvider.create())
                    .overrideConfiguration(x ->
                            x.apiCallAttemptTimeout(Duration.ofMillis(1000)))
                    .build();
        }
    }

    /**
     * Fetch spot price history from Amazon API.
     * This is synchronized so only one instance at a time can fetch data.
     * @param category - category eg. c5d.xlarge__SUSE Linux__eu-west-1a
     * @param numDays - number of days of history to fetch
     * @return PriceData instance containing the historical prices
     */
    public List<PriceData> fetchPriceHistory(String category,
                                     int numDays) {
        String[] cats = category.split("__");
        if (cats.length != 3) {
            return List.of();
        }
        // only allow one instance at a time to fetch data
        synchronized (lock) {
            DescribeSpotPriceHistoryResponse response = ec2.describeSpotPriceHistory(x -> x
                    .startTime(Instant.now().minus(Duration.ofDays(numDays)))
                    .instanceTypesWithStrings(cats[0])
                    .productDescriptions(cats[1])
                    .availabilityZone(cats[2])
            );
            return response.spotPriceHistory()
                    .stream()
                    .map(this::adaptData)
                    .toList();
        }
    }

    /**
     * Adapts data to match the public interface
     */
    private PriceData adaptData(SpotPrice info) {
        return new PriceData(
                info.timestamp().atOffset(ZoneOffset.UTC),
                Double.parseDouble(info.spotPrice()),
                info.instanceTypeAsString(),
                info.productDescriptionAsString(),
                info.availabilityZone()
        );
    }

    /**
     * Allows auto-closing if used in try-with-resources statement.
     */
    @Override
    public void close() {
        synchronized (lock) {
            ec2.close();
        }
    }

    public static void main(String[] args) {
        try (LiveSpotPrices p = new LiveSpotPrices()) {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("Enter instance type or quit: ");
                String instanceType = sc.next();
                if (instanceType.equals("quit")) {
                    break;
                }
                List<PriceData> data = p.fetchPriceHistory(instanceType, 5);
                System.out.println(data);
            }
        }
    }

}
