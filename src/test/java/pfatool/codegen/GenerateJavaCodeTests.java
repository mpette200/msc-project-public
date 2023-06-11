package pfatool.codegen;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static pfatool.codegen.CodeTestingUtils.assertSchemasEqual;

class GenerateJavaCodeTests {

    private static final String TEST_FILES_PATH = "src/test/resources/pfa/example_01/";
    private static final String OUTPUT_FILES_PATH = "src/test/resources/pfa/example_01_out";
    private static final String OUTPUT_PACKAGE = "dummy.name";
    private static final String OUTPUT_SUB_FOLDER = "/dummy/name";
    private static String expectedCellSchema;
    private static String actualCellFilename;
    private GenerateJavaCode gen;

    @BeforeAll
    static void beforeAll() throws IOException {
        Path p = FileSystems.getDefault().getPath("");
        expectedCellSchema = Files.readString(p.resolve(
                TEST_FILES_PATH + "expected_cell_scheme.test"
        ));
    }

    @AfterAll
    static void afterAll() throws IOException {
        Path p = FileSystems.getDefault().getPath("");
        deleteMatchingFiles(
                p.resolve(TEST_FILES_PATH),
                x -> x.toString().endsWith(".avsc")
                    || x.toString().endsWith(".data")
        );
        deleteFolderRecursively(p.resolve(OUTPUT_FILES_PATH));
    }

    private static void deleteMatchingFiles(Path folder,
                                            Predicate<? super Path> condition) throws IOException {
        try (Stream<Path> contents = Files.list(folder)) {
            List<Path> toDelete = contents
                    .filter(condition)
                    .toList();
            // cannot delete within stream because lambda doesn't handle exceptions
            for (Path fp : toDelete) {
                Files.delete(fp);
            }
        }
    }

    /**
     * This method is copied and pasted from the Java docs.
     * {@link FileVisitor}
     * @param folder folder to delete
     * @throws IOException on deletion error
     */
    private static void deleteFolderRecursively(Path folder) throws IOException {
        try (DirectoryStream<Path> contents = Files.newDirectoryStream(folder)) {
            for (Path fp : contents) {
                // Copied from:
                // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/FileVisitor.html
                Files.walkFileTree(fp, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException
                    {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e)
                            throws IOException
                    {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            // directory iteration failed
                            throw e;
                        }
                    }
                });
            }
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        gen = new GenerateJavaCode(
                TEST_FILES_PATH + "pfa_example_01.pfa",
                OUTPUT_FILES_PATH,
                OUTPUT_PACKAGE
        );
        actualCellFilename = TEST_FILES_PATH + "pfa_example_01_tree_class.avsc";
    }

    @Test
    void testRemoveFileExtensionEmptyStr() {
        String removed = gen.removeFileExtension("");
        assertEquals("", removed);
    }

    @Test
    void testRemoveFileExtensionEndsWithDot() {
        String removed = gen.removeFileExtension("badger.");
        assertEquals("badger", removed);
    }

    @Test
    void testRemoveFileExtensionTypical() {
        String removed = gen.removeFileExtension("name.ext");
        assertEquals("name", removed);
    }

    @Test
    void testRemoveFileExtensionBeginsWithDot() {
        String removed = gen.removeFileExtension(".config");
        assertEquals(".config", removed);
    }

    @Test
    void testMakeSchemaFileNameBasic() {
        String filename = gen.makeDataFilename("myField", ".avsc");
        assertEquals(
                TEST_FILES_PATH + "pfa_example_01_myField.avsc",
                filename
        );
    }

    @Test
    void testMakeSchemaFilesAsExpected() throws IOException {
        gen.makeSchemaFiles();
        Path p = FileSystems.getDefault().getPath("");
        assertSchemasEqual(
                expectedCellSchema,
                Files.readString(p.resolve(actualCellFilename))
        );
    }

    @Test
    void testMakeJavaFromSchemaFilesAsExpected() throws IOException {
        gen.makeSchemaFiles();
        gen.makeJavaFromSchemaFiles();
        Path p = FileSystems.getDefault().getPath("");
        try (Stream<Path> s = Files.list(p.resolve(OUTPUT_FILES_PATH + OUTPUT_SUB_FOLDER))) {
            List<String> contents = s
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .toList();
            assertLinesMatch(
                    List.of("ColumnNames.java", "TreeNode.java"),
                    contents
            );
        }
    }

    @Test
    void testMakeJavaFromSchemaFilesThrowsException() {
        assertThrows(
                IllegalStateException.class,
                gen::makeJavaFromSchemaFiles
        );
    }

}