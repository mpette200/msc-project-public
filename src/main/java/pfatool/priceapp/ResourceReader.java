package pfatool.priceapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Resource reader for the spring boot application
 */
@Component
public class ResourceReader {
    private final ApplicationContext applicationContext;

    @Autowired
    public ResourceReader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Read a resource from the given path. Could be either a file
     * on the filesystem or an archive within the jar file
     */
    public String readResource(String path) throws IOException {
        try (InputStream stream = getResource(path).getInputStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * List the contents of the directory. Could be either a filesystem directory
     * or a folder within a jar archive.
     */
    public List<String> listDirContents(String path) throws IOException {
        Resource resource = getResource(path);
        URL url = resource.getURL();
        if (url.getProtocol().equals("jar")) {
            JarURLConnection jc = (JarURLConnection) url.openConnection();
            final String pathPrefix = jc.getEntryName();
            // should ensure jarFile is always closed
            try (JarFile jarFile = jc.getJarFile();
                 Stream<JarEntry> entryStream = jarFile.stream();
            ) {
                return entryStream
                        .map(JarEntry::getName)
                        .filter(x -> x.startsWith(pathPrefix) && x.length() > pathPrefix.length())
                        .map(x -> x.substring(pathPrefix.length()))
                        .toList();
            }
        } else if (url.getProtocol().equals("file")) {
            return listDirectory(Path.of(resource.getURI()));
        }
        throw new IllegalArgumentException("path not traversable: " + path);
    }

    /**
     * Helper to list files in a filesystem folder
     */
    private List<String> listDirectory(Path path) throws IOException {
        // need to close the stream to close the file system directory.
        try (Stream<Path> pathStream = Files.list(path)) {
            return pathStream
                    .map(x -> x.getFileName().toString())
                    .toList();
        }
    }

    /**
     * Helper to check resource exists
     */
    private Resource getResource(String path) throws IOException {
        Resource resource = applicationContext.getResource(path);
        if (!resource.exists()) {
            throw new IOException("file not accessible: " + path);
        }
        return resource;
    }

}
