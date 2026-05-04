package filesystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * A static factory designed to facilitate the inspection of file attributes. This class
 * automatically detects the underlying filesystem implementation and returns the appropriate node.
 *
 * <ul>
 * <li>Updated by Trevor Maggs on April 2026</li>
 * </ul>
 *
 * @author Trevor Maggs
 * @version 1.0
 * @since 26 April 2026
 */
public final class FileInspector
{
    /**
     * Private constructor to prevent instantiation of this utility factory.
     *
     * @throws UnsupportedOperationException
     *         to indicate that instantiation is not supported
     */
    private FileInspector()
    {
        throw new UnsupportedOperationException("Not intended for instantiation");
    }

    /**
     * Returns a reference to a node containing file attributes. Symbolic links are followed by
     * default.
     *
     * @param name
     *        the path string to the file
     * @return the attribute node
     *
     * @throws IOException
     *         if the file cannot be accessed
     */
    public static AbstractFileNode inspect(String name) throws IOException
    {
        return inspect(Paths.get(name), true);
    }

    /**
     * Returns a reference to a node containing file attributes.
     *
     * @param path
     *        the Path object to be queried
     * @param followSymlink
     *        true to follow symbolic links
     * @return the attribute node (either a PosixNode or a DosNode)
     *
     * @throws IOException
     *         if the file cannot be accessed
     */
    public static AbstractFileNode inspect(Path path, boolean followSymlink) throws IOException
    {
        Set<String> views = path.getFileSystem().supportedFileAttributeViews();

        if (views.contains("posix"))
        {
            return new PosixNode(path, followSymlink);
        }

        else if (views.contains("dos"))
        {
            return new DosNode(path, followSymlink);
        }

        throw new UnsupportedOperationException("Unsupported filesystem: " + views.toString());
    }
}