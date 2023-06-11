package pfatool.priceapp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.Map;

/**
 * Helpful functions for debugging.
 */
public class DebuggingUtils {

    public static final String NEWLINE = System.lineSeparator();


    /**
     * Useful to print a list of all folders and files to troubleshoot issues
     * with finding files on the classpath.
     */
    public String printClassPathResources() {
        Enumeration<URL> urls;
        StringBuilder out = new StringBuilder();
        out.append("Displaying classpath:").append(NEWLINE);
        try {
            urls = getClass().getClassLoader().getResources("");
        } catch (IOException ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            out.append(url.toString()).append(NEWLINE);
            try {
                URI uri = url.toURI();
                if (uri.getScheme().equals("jar")) {
                    printJarFile(uri, out);
                } else {
                    printFileFolder(uri, out);
                }
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            } catch (FileSystemNotFoundException ex) {
                out.append("Cannot open: ").append(url).append(NEWLINE);
            } catch (Exception ex) {
                out.append("EXCEPTION reading: ").append(url).append(NEWLINE);
                ex.printStackTrace();
            }
        }
        return out.toString();
    }

    /**
     * Helper to print details of a file on the filesystem
     */
    private void printFileFolder(URI uri,
                                 StringBuilder out) throws IOException {
            printFolderTree(Path.of(uri), 0, out);
    }

    /**
     * Helper to print details from an archive in a jar file.
     */
    private void printJarFile(URI uri,
                              StringBuilder out) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(
                uri,
                Map.of()
        )) {
            String subPath = uri.getSchemeSpecificPart()
                    .split("!")[1];
            printFolderTree(fs.getPath(subPath), 0, out);
        }
    }

    /**
     * Helper to print nested jars within a jar.
     */
    private void printNestedJar(Path path,
                                int indentLevel,
                                StringBuilder out) throws IOException {
        try (FileSystem fs = FileSystems.newFileSystem(path)) {
            printFolderTree(fs.getPath(""), indentLevel, out);
        }
    }

    /**
     * Helper to recursively print folder items
     */
    private void printFolderTree(Path folder,
                                 int indentLevel,
                                 StringBuilder out) throws IOException {
        printFolderTree(folder,indentLevel, out, false);
    }

    /**
     * Call via printClassPathResources() rather than directly invoking this method.
     */
    private void printFolderTree(Path folder,
                                 int indentLevel,
                                 StringBuilder out,
                                 boolean includeNestedJar) throws IOException {
        String indent = "  ".repeat(indentLevel);
        if (!Files.isDirectory(folder)) {
            out.append(indent).append(folder.getFileName()).append(NEWLINE);
            if (includeNestedJar
                    && folder.getFileName().toString().endsWith(".jar")) {
                 printNestedJar(folder, 1 + indentLevel, out);
            }
            return;
        }
        out.append(indent).append(folder).append(":").append(NEWLINE);
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(folder)) {

            for (Path path : paths) {
                printFolderTree(path, 1 + indentLevel, out);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encountered during the iteration, the cause is an IOException
            throw ex.getCause();
        }
    }

}
