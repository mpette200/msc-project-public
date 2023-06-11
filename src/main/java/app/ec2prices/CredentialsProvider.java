package app.ec2prices;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

/**
 * Implementation of Credentials Provider.
 * Reads from `aws.properties` file:<pre>
 * {@code
 * aws.accessKeyId=####
 * aws.secretAccessKey=####
 * }</pre>
 */
public class CredentialsProvider implements AwsCredentialsProvider {

    public static final String CONFIG_FILE = "aws.properties";
    public static final String PROP_NAME_KEY = "aws.accessKeyId";
    public static final String PROP_NAME_SECRET = "aws.secretAccessKey";
    private static final CredentialsProvider INSTANCE;
    private static final String KEY_ID;
    private static final String SECRET_KEY;

    static {
        URL url = CredentialsProvider.class.getClassLoader().getResource(CONFIG_FILE);
        Objects.requireNonNull(url, "error opening config_file: " + CONFIG_FILE);
        try (InputStream stream = url.openStream()) {
            Properties p = new Properties();
            p.load(stream);
            KEY_ID = p.getProperty(PROP_NAME_KEY);
            Objects.requireNonNull(KEY_ID,
                    "Not set %s in %s".formatted(PROP_NAME_KEY, CONFIG_FILE));
            SECRET_KEY = p.getProperty(PROP_NAME_SECRET);
            Objects.requireNonNull(SECRET_KEY,
                    "Not set %s in %s".formatted(PROP_NAME_SECRET, CONFIG_FILE));
        } catch (IOException e) {
            throw new IllegalStateException("opening config_file: " + CONFIG_FILE);
        }
        INSTANCE = new CredentialsProvider();
    }

    /**
     * Returns an instance of CredentialsProvider.
     */
    public static CredentialsProvider create() {
        return INSTANCE;
    }

    private CredentialsProvider() {}

    @Override
    public AwsCredentials resolveCredentials() {
        return new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return KEY_ID;
            }

            @Override
            public String secretAccessKey() {
                return SECRET_KEY;
            }
        };
    }

}
