package pfatool.codegen;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.avro.compiler.specific.SpecificCompiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Code generator makes Java classes to represent data structures in the PFA document.
 * Intended to be run via command line.
 * Usage example: <pre>
 * {@code
 * java ... sourcePfaFile destinationPath packageName
 * // ... is replaced by the necessary classpath dependencies and classname to define main method to run.
 * }
 * </pre>
 */
public class GenerateJavaCode {

    public static final String SCHEMA_FILE_EXT = ".avsc";
    public static final String DATA_FILE_EXT = ".data";
    private final Path basePath;
    private final Path destPath;
    private final ObjectMapper mapper;
    private final JsonNode rootNode;
    private final String srcFileName;
    private final String packageName;
    private final JsonPointer allPointer;
    private final JsonPointer cellTypePointer;
    private final JsonPointer cellInitialDataPointer;
    private final List<Path> schemaFilesWritten;

    /**
     * Generator for Java code using the given PFA file, and writing to the
     * destination path.
     * @param srcFileName Name of PFA source file
     * @param destPath Destination to write code
     * @param packageName Package name of destination eg "pfatool.tmpgenerated"
     *                    Can be empty string "" if not part of a package.
     */
    public GenerateJavaCode(String srcFileName,
                            String destPath,
                            String packageName) throws IOException {
        this.srcFileName = srcFileName;
        this.mapper = new ObjectMapper();
        this.basePath = FileSystems.getDefault().getPath("");
        this.destPath = basePath.resolve(destPath);
        this.rootNode = parsePfa(Files.readString(
                basePath.resolve(srcFileName)
        ));
        this.packageName = packageName;
        this.allPointer = JsonPointer.compile("");
        this.cellTypePointer = JsonPointer.compile("/type");
        this.cellInitialDataPointer = JsonPointer.compile("/init");
        this.schemaFilesWritten = new ArrayList<>();
    }

    /**
     * Process the PFA document and create Schema files that are ready to
     * be used by the Apache Avro code generators.
     *
     * @throws IOException if error processing files
     */
    public void makeSchemaFiles() throws IOException {
        Iterator<Map.Entry<String, JsonNode>> it = rootNode.fields();
        while (it.hasNext()) {
            processNode(it.next());
        }
    }

    /**
     * Process the list of schema files that have been written to create
     * the corresponding Java classes using the Apache Avro code generators.
     *
     * @throws IOException if error processing files
     */
    public void makeJavaFromSchemaFiles() throws IOException {
        if (schemaFilesWritten.isEmpty()) {
            throw new IllegalStateException("no schema files written");
        }
        File[] files = schemaFilesWritten.stream()
                .map(Path::toFile)
                .toArray(File[]::new);
        SpecificCompiler.compileSchema(files, destPath.toFile());
    }

    /**
     * Writes the final output file. Avro schemas use "namespace" to correspond
     * with the package declaration at the top of Java classes. So add a namespace
     * to the schema definitions if a package name is defined.
     */
    private void insertNamespace(JsonNode node) {
        if (!packageName.isBlank()
                && node instanceof ObjectNode objNode) {
            objNode.put("namespace", packageName);
        }
    }

    /**
     * Depending on the type of JsonNode, call the corresponding function to process the entry.
     */
    private void processCellGroup(JsonNode node) throws IOException {
        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            // save original schema then insert namespace and save again
            processEntry(entry, cellTypePointer, "_data" + SCHEMA_FILE_EXT, false);
            insertNamespace(entry.getValue().at(cellTypePointer));
            processEntry(entry, cellTypePointer, "_class" + SCHEMA_FILE_EXT, true);

            // save cell data
            processEntry(entry, cellInitialDataPointer, DATA_FILE_EXT, false);
        }
    }

    /**
     * Writes the final output file. Only need to save schemas for processing later.
     * The boolean flag isSchema triggers save for processing later.
     */
    private void processEntry(Map.Entry<String, JsonNode> entry,
                              JsonPointer ptr,
                              String fileExtension,
                              boolean isSchema) throws IOException {
        String outName = makeDataFilename(entry.getKey(), fileExtension);
        JsonNode node = entry.getValue().at(ptr);
        Path outputPath = basePath.resolve(outName);
        Files.writeString(outputPath, node.toString());
        if (isSchema) {
            schemaFilesWritten.add(outputPath);
        }
    }

    /**
     * Process a JsonNode. At this stage only `cells` node needs further processing.
     */
    private void processNode(Map.Entry<String, JsonNode> x) throws IOException {
        if (x.getKey().equals("cells")) {
            processCellGroup(x.getValue());
        }
    }

    /**
     * Package private method for testing
     * @return root node of Json tree
     */
    JsonNode getRootNode() {
        return rootNode;
    }

    /**
     * Generates Tree representation of Json text using
     * com.fasterxml.jackson.databind.JsonNode
     * @param pfaText PFA document
     * @throws IOException is error reading the Json
     */
    JsonNode parsePfa(String pfaText) throws IOException {
        return mapper.readTree(pfaText);
    }

    /**
     * Creates filename based on fieldName
     * "name_01.ext" -> "name_01_input.avsc"
     * @param fieldName name to use
     * @return filename
     */
    String makeDataFilename(String fieldName, String extension) {
        return removeFileExtension(srcFileName) + "_"
                + fieldName
                + extension;
    }

    /**
     * Removes the file extension from a name
     * "name.ext" -> "name"
     * @param name original name with extension
     * @return name with removed extension
     */
    String removeFileExtension(String name) {
        if (name.startsWith(".")) {
            return name;
        }
        String[] parts = name.split("\\.");
        if (parts.length <= 1) {
            return parts[parts.length - 1];
        }
        return parts[parts.length - 2];
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Need three arguments:");
            System.out.println("sourcePfaFile destinationPath packageName");
        } else {
            System.out.println("arg0: " + args[0]);
            System.out.println("arg1: " + args[1]);
            System.out.println("arg2: " + args[2]);
            GenerateJavaCode generator = new GenerateJavaCode(
                    args[0],
                    args[1],
                    args[2]
            );
            generator.makeSchemaFiles();
            generator.makeJavaFromSchemaFiles();
        }
    }
}
