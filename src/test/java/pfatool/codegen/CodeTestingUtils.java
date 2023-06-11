package pfatool.codegen;

import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeTestingUtils {
    /**
     * Normalize schema text based on "Parsing Canonical Form" as defined by Avro spec.
     * This is enables to schemas to be compared in unit tests. If two schemas have the
     * same canonical form then they define exactly the same data.
     * @param jsonSchema schema as JSON string
     * @return Canonical form of schema
     */
    public static String normalizeSchema(String jsonSchema) {
        Schema schema = new Schema.Parser().parse(jsonSchema);
        return SchemaNormalization.toParsingForm(schema);
    }

    /**
     * Assert whether two Avro schemas are equal.
     * @param a first avro schema as json text
     * @param b second avro schema as json text
     */
    public static void assertSchemasEqual(String a, String b) {
        assertEquals(
                normalizeSchema(a),
                normalizeSchema(b)
        );
    }

}
