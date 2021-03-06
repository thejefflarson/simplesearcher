package org.propublica.simplesearcher;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration {
    private static Path path;

    public static void setPath(Path p) {
        assert (path == null);
        assert (Files.isDirectory(p));
        path = p.toAbsolutePath();
    }

    static public Path getPath() {
        return path;
    }

    static public Path getIndexPath() {
        return path.resolve(".simplesearcher-index");
    }

    static public Directory getDirectory() throws IOException {
        return FSDirectory.open(getIndexPath());
    }
}
