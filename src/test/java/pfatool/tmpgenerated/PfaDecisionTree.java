package pfatool.tmpgenerated;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class PfaDecisionTree {

    private static final String PATH_DATA = "pfa/tmp_generated_01/pfa_example_01_tree.data";
    private static final String PATH_DATA_SCHEMA = "pfa/tmp_generated_01/pfa_example_01_tree_data.avsc";
    private static final Class<PfaDecisionTree> myLock = PfaDecisionTree.class;
    private static volatile PfaDecisionTree instance = null;

    private final TreeNode root;

    /**
     * Reading and parsing Pfa definition is slow so use singleton pattern.
     */
    private PfaDecisionTree() {
        String treeData = readFile(PATH_DATA);
        String schemaTxt = readFile(PATH_DATA_SCHEMA);
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
     * Static method to get and return an instance
     * @return instance of PfaDecisionTree
     */
    public static PfaDecisionTree getInstance() {
        if (instance == null) {
            synchronized (myLock) {
                if (instance == null) {
                    instance = new PfaDecisionTree();
                }
            }
        }
        return instance;
    }

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

    @Override
    public String toString() {
        return root.toString();
    }

    private String readFile(String path) {
        URL url = ClassLoader.getSystemResource(path);
        Objects.requireNonNull(url, "not found: " + path);
        try {
            return Files.readString(Path.of(url.toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void main(String[] args) {
        PfaDecisionTree doc = PfaDecisionTree.getInstance();
        System.out.println(doc);
        System.out.println(doc.root.get("field"));
        System.out.println(Arrays.toString(ColumnNames.values()));
        System.out.println(doc.root.get("field").getClass());

        ColumnNames colName = (ColumnNames) doc.root.get("field");
        System.out.println(ColumnNames.x0.getClass());
        System.out.println(doc.root.getOperator().toString().equals("<="));

        double[] v1 = new double[] {1.1, 2.2, 3.3, 4.4};
        double[] v2 = new double[] {5.5, 4.4, 3.3, 2.2};
        double[] v3 = new double[] {2.2, 2.2, 4.4, 5.5};
        System.out.println(doc.predict(v1));
        System.out.println(doc.predict(v2));
        System.out.println(doc.predict(v3));

        System.out.println(Arrays.toString(doc
                .root
                .getField()
                .getDeclaringClass()
                .getEnumConstants()));
    }
}
