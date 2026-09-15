package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Map;

/**
 * A Windows-specific implementation of a file node that provides access to DOS and Windows file
 * attributes.
 *
 * <p>
 * This implementation uses the {@link DosFileAttributes} view for standard DOS attributes such as
 * hidden, read-only, system, and archive, and the {@code dos:*} attribute view to retrieve the raw
 * Windows attribute bitmask for additional attributes such as compression, encryption, and sparse
 * files.
 * </p>
 *
 * @author Trevor Maggs
 * @version 1.0
 * @since 27 April 2026
 */
public final class DosNode extends AbstractFileNode implements DosView
{
    private final DosFileAttributes dosAttribs;
    private final Map<String, Object> attrMap;

    private enum AttributeFlag
    {
        READONLY(FILE_ATTRIBUTE_READONLY),
        HIDDEN(FILE_ATTRIBUTE_HIDDEN),
        SYSTEM(FILE_ATTRIBUTE_SYSTEM),
        DIRECTORY(FILE_ATTRIBUTE_DIRECTORY),
        ARCHIVE(FILE_ATTRIBUTE_ARCHIVE),
        DEVICE(FILE_ATTRIBUTE_DEVICE),
        NORMAL(FILE_ATTRIBUTE_NORMAL),
        TEMPORARY(FILE_ATTRIBUTE_TEMPORARY),
        SPARSE_FILE(FILE_ATTRIBUTE_SPARSE_FILE),
        REPARSE_POINT(FILE_ATTRIBUTE_REPARSE_POINT),
        COMPRESSED(FILE_ATTRIBUTE_COMPRESSED),
        OFFLINE(FILE_ATTRIBUTE_OFFLINE),
        NOT_CONTENT_INDEXED(FILE_ATTRIBUTE_NOT_CONTENT_INDEXED),
        ENCRYPTED(FILE_ATTRIBUTE_ENCRYPTED),
        INTEGRITY_STREAM(FILE_ATTRIBUTE_INTEGRITY_STREAM),
        VIRTUAL(FILE_ATTRIBUTE_VIRTUAL),
        NO_SCRUB_DATA(FILE_ATTRIBUTE_NO_SCRUB_DATA),
        PINNED(FILE_ATTRIBUTE_PINNED),
        UNPINNED(FILE_ATTRIBUTE_UNPINNED),
        RECALL_ON_DATA_ACCESS(FILE_ATTRIBUTE_RECALL_ON_DATA_ACCESS);

        private final int mask;

        AttributeFlag(int mask)
        {
            this.mask = mask;
        }

        static String fromMask(int mask)
        {
            StringBuilder sb = new StringBuilder(128);

            for (AttributeFlag flag : values())
            {
                if ((mask & flag.mask) != 0)
                {
                    appendFlag(sb, flag.name());
                }
            }

            return sb.toString();
        }
    }

    /**
     * Package-private constructor used by the {@link FileInspector} factory.
     *
     * @param path
     *        the path to the file or directory
     * @param followSymlink
     *        whether symbolic links should be followed when capturing file attributes
     *
     * @throws IOException
     *         if the file is inaccessible or the filesystem does not support DOS attributes
     */
    DosNode(Path path, boolean followSymlink) throws IOException
    {
        super(path, followSymlink);

        this.dosAttribs = (DosFileAttributes) this.attrs;
        this.attrMap = Files.readAttributes(path, "dos:*", this.options);
    }

    /**
     * Checks if the file has the Windows archive attribute set. The archive attribute is commonly
     * used by backup software to identify files that have been modified since the last backup.
     *
     * @return {@code true} if the archive attribute is set
     */
    @Override
    public boolean isArchiveFile()
    {
        return dosAttribs.isArchive();
    }

    /**
     * Checks if the file has the Windows hidden attribute set.
     *
     * @return {@code true} if the hidden attribute is set
     */
    @Override
    public boolean isHidden()
    {
        return dosAttribs.isHidden();
    }

    /**
     * Checks if the file has the Windows read-only attribute set.
     *
     * @return {@code true} if the read-only attribute is set
     */
    @Override
    public boolean isReadOnly()
    {
        return dosAttribs.isReadOnly();
    }

    /**
     * Checks if the file or directory has the Windows System attribute set.
     *
     * @return {@code true} if the system attribute is set
     */
    @Override
    public boolean isSystemFile()
    {
        return dosAttribs.isSystem();
    }

    /**
     * Retrieves the raw Windows file attribute constants as an integer bitmask.
     *
     * @return the integer bitmask, or {@code 0} if the {@code "attributes"} entry is missing or is
     *         not a numeric value
     */
    @Override
    public int getAttributesMask()
    {
        Object mask = attrMap.get("attributes");

        return (mask instanceof Number) ? ((Number) mask).intValue() : 0;
    }

    /**
     * Checks if the file has the Windows temporary attribute set.
     *
     * <p>
     * This attribute indicates that the file is being used for temporary storage and that the
     * operating system may avoid writing the file's data to permanent storage when sufficient cache
     * memory is available.
     * </p>
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_TEMPORARY} bit is set
     */
    @Override
    public boolean isTemporary()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_TEMPORARY) != 0;
    }

    /**
     * Checks if the file is a sparse file.
     *
     * <p>
     * A sparse file contains logically allocated regions that may not require corresponding
     * physical disk storage, allowing storage space to be saved for regions containing zeros.
     * </p>
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_SPARSE_FILE} bit is set
     */
    @Override
    public boolean isSparseFile()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_SPARSE_FILE) != 0;
    }

    /**
     * Checks if the file or directory has the Windows reparse-point attribute set.
     *
     * <p>
     * Reparse points are used by Windows for features such as symbolic links, junctions, and other
     * filesystem-specific functionality.
     * </p>
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_REPARSE_POINT} bit is set
     */
    @Override
    public boolean isReparsePoint()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_REPARSE_POINT) != 0;
    }

    /**
     * Checks if the file has the Windows compressed attribute set.
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_COMPRESSED} bit is set
     */
    @Override
    public boolean isCompressed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_COMPRESSED) != 0;
    }

    /**
     * Checks if the file data is not immediately available.
     *
     * <p>
     * This attribute indicates that the file data has been physically moved to offline storage,
     * such as Remote Storage.
     * </p>
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_OFFLINE} bit is set
     */
    @Override
    public boolean isOffline()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_OFFLINE) != 0;
    }

    /**
     * Checks if the file or directory is excluded from content indexing.
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_NOT_CONTENT_INDEXED} bit is set
     */
    @Override
    public boolean isNotContentIndexed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_NOT_CONTENT_INDEXED) != 0;
    }

    /**
     * Checks if the file or directory has the Windows encrypted attribute set.
     *
     * <p>
     * For a file, this indicates that the file's data streams are encrypted.
     * </p>
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_ENCRYPTED} bit is set
     */
    @Override
    public boolean isEncrypted()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_ENCRYPTED) != 0;
    }

    /**
     * Checks if the file or directory is excluded from data integrity scanning.
     *
     * <p>
     * When this attribute is set on a directory, it becomes the default for new files created
     * within that directory. This attribute is specific to ReFS volumes.
     * </p>
     *
     * @return {@code true} if the {@code FILE_ATTRIBUTE_NO_SCRUB_DATA} bit is set
     */
    @Override
    public boolean isNoScrubData()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_NO_SCRUB_DATA) != 0;
    }

    /**
     * Generates a human-readable string representation of all active Windows file attributes.
     *
     * <p>
     * Active flags are concatenated using a {@code " | "} separator, for example
     * {@code "READONLY | HIDDEN | COMPRESSED"}.
     * </p>
     *
     * @return a pipe-delimited string containing the names of the active attributes
     */
    @Override
    public String getAttributesString()
    {
        return AttributeFlag.fromMask(getAttributesMask());
    }

    /**
     * Generates a POSIX-style permission string based on the Windows DOS read-only attribute.
     *
     * The returned string emulates the output produced by ExifTool on Windows. It does not
     * represent actual POSIX filesystem permissions. The owner, group, and other permission bits
     * are all derived from the same read-only state.
     *
     * @return a 10-character POSIX-style permission string, such as {@code "-r--r--r--"} for a
     *         read-only file or {@code "-rw-rw-rw-"} for a writable file
     */
    @Override
    public String getPermissionsString()
    {
        StringBuilder sb = new StringBuilder(10);
        char typeChar = isDirectory() ? 'd' : '-';
        String r = "r";
        String w = (!dosAttribs.isReadOnly() ? "w" : "-");
        String x = "-";
        String triplet = r + w + x;

        sb.append(typeChar);
        sb.append(triplet); // User
        sb.append(triplet); // Group
        sb.append(triplet); // Other

        return sb.toString();
    }

    /**
     * Generates a formatted diagnostic summary of the DOS and Windows file attributes.
     *
     * @return a formatted string containing Windows-specific file metadata
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        int mask = getAttributesMask();
        String activeFlags = getAttributesString();
        String sectionHeader = isDirectory() ? "DOS Directory Attributes" : "DOS File Attributes";
        String nameLabel = isDirectory() ? "[Directory Name]" : "[File Name]";

        sb.append(String.format("[%s]%n", sectionHeader));
        sb.append(String.format("  %-20s %s%n", nameLabel, getName()));
        sb.append(String.format("  %-20s %s%n", "[Real Path]", toRealPathString()));
        sb.append(String.format("  %-20s %s%n", "[Directory]", isDirectory()));
        sb.append(String.format("  %-20s %s%n", "[Regular File]", isRegularFile()));
        sb.append(String.format("  %-20s %s%n", "[Symlink]", isSymLink()));
        sb.append(String.format("  %-20s %s bytes%n", "[File Size]", size()));

        // --- Core DOS Attributes
        sb.append(String.format("  %-20s %b%n", "[Read-only]", isReadOnly()));
        sb.append(String.format("  %-20s %b%n", "[Hidden]", isHidden()));
        sb.append(String.format("  %-20s %b%n", "[System]", isSystemFile()));
        sb.append(String.format("  %-20s %b%n", "[Archive]", isArchiveFile()));

        // --- DOS/Windows Specific Sections ---
        sb.append(String.format("  %-20s %s%n", "[File Permissions]", getPermissionsString()));
        sb.append(String.format("  %-20s 0x%08X (%d)%n", "[Attributes Mask]", mask, mask));
        sb.append(String.format("  %-20s %s%n", "[Active Flags]", activeFlags.isEmpty() ? "NORMAL" : activeFlags));

        return sb.toString();
    }

    /**
     * Appends a flag to the specified {@link StringBuilder}, inserting a separator if the builder
     * already contains text.
     *
     * @param sb
     *        the builder to which the flag is appended
     * @param flag
     *        the flag to append
     */
    private static void appendFlag(StringBuilder sb, String flag)
    {
        if (sb.length() > 0)
        {
            sb.append(" | ");
        }

        sb.append(flag);
    }
}