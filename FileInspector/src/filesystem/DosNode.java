package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Map;

/**
 * A Windows-specific implementation of a file node used to inspect DOS and NTFS attributes.
 * 
 * <p>
 * This implementation uses both the {@link DosFileAttributes} view for standard flags, such as
 * Hidden, Read-Only, etc. and the {@code dos:*} attribute map to retrieve the raw Win32 attribute
 * bitmask for advanced flags like compression, encryption, and sparse files.
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

    /**
     * Package-private constructor used by the {@link FileInspector} factory.
     * 
     * @param path
     *        the path to the file or directory
     * @param followSymlink
     *        whether to follow symbolic links during attribute capture
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
     * Checks if the file is an archive file. Typically used by backup programs to identify files
     * that need to be backed up.
     * 
     * @return true if the archive bit is set
     */
    @Override
    public boolean isArchiveFile()
    {
        return dosAttribs.isArchive();
    }

    /**
     * Checks if the file is hidden.
     * 
     * @return true if the file is marked as hidden by the filesystem
     */
    @Override
    public boolean isHidden()
    {
        return dosAttribs.isHidden();
    }

    /**
     * Checks if the file is read-only.
     * 
     * @return true if the file cannot be written to or deleted
     */
    @Override
    public boolean isReadOnly()
    {
        return dosAttribs.isReadOnly();
    }

    /**
     * Checks if the file is a system file or is used exclusively by the operating system.
     * 
     * @return true if the system bit is set
     */
    @Override
    public boolean isSystemFile()
    {
        return dosAttribs.isSystem();
    }

    /**
     * Retrieves the raw Win32 file attribute constants as an integer mask.
     * 
     * @return the integer bitmask, or 0 if the "attributes" key is missing or invalid
     */
    @Override
    public int getAttributesMask()
    {
        Object mask = attrMap.get("attributes");

        return (mask instanceof Number) ? ((Number) mask).intValue() : 0;
    }

    /**
     * Checks if the file is being used for temporary storage. File systems avoid writing the data
     * back to mass storage if sufficient cache memory is available.
     * 
     * @return true if the FILE_ATTRIBUTE_TEMPORARY bit is set
     */
    @Override
    public boolean isTemporary()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_TEMPORARY) != 0;
    }

    /**
     * Checks if the file is a sparse file. Sparse files are large files that contain mostly zeros,
     * where only non-zero data is physically stored on disk.
     * 
     * @return true if the FILE_ATTRIBUTE_SPARSE_FILE bit is set
     */
    @Override
    public boolean isSparseFile()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_SPARSE_FILE) != 0;
    }

    /**
     * Checks if the file or directory has an associated reparse point, or is a symbolic link
     * junction.
     * 
     * @return true if the FILE_ATTRIBUTE_REPARSE_POINT bit is set
     */
    @Override
    public boolean isReparsePoint()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_REPARSE_POINT) != 0;
    }

    /**
     * Checks if the file or directory is compressed. For a file, this means all data in the file is
     * compressed.
     * 
     * @return true if the FILE_ATTRIBUTE_COMPRESSED bit is set
     */
    @Override
    public boolean isCompressed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_COMPRESSED) != 0;
    }

    /**
     * Checks if the file data is not immediately available. This attribute indicates that the file
     * data is physically moved to offline storage (e.g., Remote Storage).
     * 
     * @return true if the FILE_ATTRIBUTE_OFFLINE bit is set
     */
    @Override
    public boolean isOffline()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_OFFLINE) != 0;
    }

    /**
     * Checks if the file is not to be indexed by the content indexing service.
     * 
     * @return true if the FILE_ATTRIBUTE_NOT_CONTENT_INDEXED bit is set
     */
    @Override
    public boolean isNotContentIndexed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_NOT_CONTENT_INDEXED) != 0;
    }

    /**
     * Checks if the file or directory is encrypted. For a file, this means all data streams in the
     * file are encrypted.
     * 
     * @return true if the FILE_ATTRIBUTE_ENCRYPTED bit is set
     */
    @Override
    public boolean isEncrypted()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_ENCRYPTED) != 0;
    }

    /**
     * Checks if the file is excluded from the data integrity scan. When this attribute is set on a
     * directory, it is the default for new files created therein. (Specific to ReFS volumes).
     * 
     * @return true if the FILE_ATTRIBUTE_NO_SCRUB_DATA bit is set
     */
    @Override
    public boolean isNoScrubData()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_NO_SCRUB_DATA) != 0;
    }

    /**
     * Generates a human-readable string representation of all active Win32 attributes.
     * *
     * <p>
     * Active flags are concatenated with a " | " separator (e.g., "READONLY | HIDDEN |
     * COMPRESSED").
     * </p>
     * 
     * @return a pipe-delimited string of attribute names
     */
    @Override
    public String getAttributesString()
    {
        int mask = getAttributesMask();
        StringBuilder sb = new StringBuilder(128);

        if ((mask & FILE_ATTRIBUTE_READONLY) != 0) appendFlag(sb, "READONLY");
        if ((mask & FILE_ATTRIBUTE_HIDDEN) != 0) appendFlag(sb, "HIDDEN");
        if ((mask & FILE_ATTRIBUTE_SYSTEM) != 0) appendFlag(sb, "SYSTEM");
        if ((mask & FILE_ATTRIBUTE_DIRECTORY) != 0) appendFlag(sb, "DIRECTORY");
        if ((mask & FILE_ATTRIBUTE_ARCHIVE) != 0) appendFlag(sb, "ARCHIVE");
        if ((mask & FILE_ATTRIBUTE_DEVICE) != 0) appendFlag(sb, "DEVICE");
        if ((mask & FILE_ATTRIBUTE_NORMAL) != 0) appendFlag(sb, "NORMAL");
        if ((mask & FILE_ATTRIBUTE_TEMPORARY) != 0) appendFlag(sb, "TEMPORARY");
        if ((mask & FILE_ATTRIBUTE_SPARSE_FILE) != 0) appendFlag(sb, "SPARSE_FILE");
        if ((mask & FILE_ATTRIBUTE_REPARSE_POINT) != 0) appendFlag(sb, "REPARSE_POINT");
        if ((mask & FILE_ATTRIBUTE_COMPRESSED) != 0) appendFlag(sb, "COMPRESSED");
        if ((mask & FILE_ATTRIBUTE_OFFLINE) != 0) appendFlag(sb, "OFFLINE");
        if ((mask & FILE_ATTRIBUTE_NOT_CONTENT_INDEXED) != 0) appendFlag(sb, "NOT_CONTENT_INDEXED");
        if ((mask & FILE_ATTRIBUTE_ENCRYPTED) != 0) appendFlag(sb, "ENCRYPTED");
        if ((mask & FILE_ATTRIBUTE_INTEGRITY_STREAM) != 0) appendFlag(sb, "INTEGRITY_STREAM");
        if ((mask & FILE_ATTRIBUTE_VIRTUAL) != 0) appendFlag(sb, "VIRTUAL");
        if ((mask & FILE_ATTRIBUTE_NO_SCRUB_DATA) != 0) appendFlag(sb, "NO_SCRUB_DATA");
        if ((mask & FILE_ATTRIBUTE_PINNED) != 0) appendFlag(sb, "PINNED");
        if ((mask & FILE_ATTRIBUTE_UNPINNED) != 0) appendFlag(sb, "UNPINNED");
        if ((mask & FILE_ATTRIBUTE_RECALL_ON_DATA_ACCESS) != 0) appendFlag(sb, "RECALL_ON_DATA_ACCESS");

        return sb.toString();
    }

    /**
     * Emulates ExifTool's Windows behaviour by mapping DOS attributes to a 10-character POSIX-style
     * string.
     *
     * @return a string such as "-r--r--r--" (Read-only) or "-rw-rw-rw-" (Standard)
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
     * Generates a formatted diagnostic summary of the DOS/Win32 attributes.
     * 
     * @return a formatted string containing Windows-specific metadata
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
     * Appends attribute names to the StringBuilder with clear separation.
     * 
     * @param sb
     *        the StringBuilder to append to
     * 
     * @param flag
     *        the attribute name
     */
    private void appendFlag(StringBuilder sb, String flag)
    {
        if (sb.length() > 0)
        {
            sb.append(" | ");
        }

        sb.append(flag);
    }
}