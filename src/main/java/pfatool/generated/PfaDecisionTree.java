package pfatool.generated;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implements the decision tree structure that makes predictions
 */
public class PfaDecisionTree {

    private static final String DATA_FILE_EXT = ".data";
    private static final String SCHEMA_FILE_EXT = "_data.avsc";
    private static final Class<PfaDecisionTree> myLock = PfaDecisionTree.class;
    private static final Map<String, PfaDecisionTree> instanceMap = new HashMap<>();

    private final TreeNode root;

    /**
     * Constructor takes name of data file (without extension).
     * There must be two files, one containing the data, and one containing the schema,
     * that follow this naming convention:<br>
     * "####.data" - the data in Avro JSON encoding form<br>
     * "####_data.avsc" - the Avro schema of the data<br>
     */
    public PfaDecisionTree(String dataFile) {
        String treeData = readFile(dataFile + DATA_FILE_EXT);
        String schemaTxt = readFile(dataFile + SCHEMA_FILE_EXT);
        DatumReader<TreeNode> treeReader = new SpecificDatumReader<>(TreeNode.class);
        Schema treeSchema = new Schema.Parser().parse(schemaTxt);
        try {
            Decoder jsonDecoder = DecoderFactory.get().jsonDecoder(treeSchema, treeData);
            this.root = treeReader.read(null, jsonDecoder);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Static method to create and return a decision tree instance by reading
     * the given dataFile. As reading the dataFile may be slow use the singleton
     * pattern so the file is only read once.
     * @param dataFile datafile to build tree
     * @return PfaDecisionTree instance
     */
    public static PfaDecisionTree getInstance(String dataFile) {
        synchronized (myLock) {
            return instanceMap.computeIfAbsent(dataFile, PfaDecisionTree::new);
        }
    }

    /**
     * Make a prediction based upon the decision tree stored in this class
     *
     * @param inputs array of pre-processed data
     * @return prediction value as floating point number
     */
    public double predict(double[] inputs) {
        Object obj = root;
        while (true) {
            if (obj instanceof TreeNode node) {
                if (node.getOperator().toString().equals("<=")) {
                    double inputValue = inputs[node.getField().ordinal()];
                    if (inputValue <= node.getValue()) {
                        obj = node.getPass();
                    } else {
                        obj = node.getFail();
                    }
                } else {
                    throw new IllegalStateException("unknown comparison operator");
                }
            } else if (obj instanceof Double dbl) {
                return dbl;
            }
        }
    }

    /**
     * Returns string representation of the decision tree structure
     */
    @Override
    public String toString() {
        return root.toString();
    }

    /**
     * Helper method to read data from file on disk
     */
    private String readFile(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        Objects.requireNonNull(url, "not found: " + path);
        try (InputStream stream = url.openStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
