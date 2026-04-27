package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Map;

/**
 * A Windows-specific implementation of a file node, providing deep access to DOS and NTFS
 * attributes.
 * 
 * <p>
 * This implementation uses both the {@link DosFileAttributes} view for standard flags (Hidden,
 * Read-Only, etc.) and the {@code dos:*} attribute map to retrieve the raw Win32 attribute bitmask
 * for advanced flags like compression, encryption, and sparse files.
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
     *        The path to the file or directory
     * @param followSymlink
     *        whether to follow symbolic links during attribute capture
     * @throws IOException
     *         if the file is inaccessible or the filesystem does not support DOS attributes
     */
    DosNode(Path path, boolean followSymlink) throws IOException
    {
        super(path, followSymlink);

        // Cast parent attributes for standard DOS flags
        this.dosAttribs = (DosFileAttributes) this.attrs;
        this.attrMap = Files.readAttributes(path, "dos:*", this.options);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isArchiveFile()
    {
        return dosAttribs.isArchive();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isHidden()
    {
        return dosAttribs.isHidden();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReadOnly()
    {
        return dosAttribs.isReadOnly();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public boolean isTemporary()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_TEMPORARY) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSparseFile()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_SPARSE_FILE) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isReparsePoint()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_REPARSE_POINT) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompressed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_COMPRESSED) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isOffline()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_OFFLINE) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNotContentIndexed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_NOT_CONTENT_INDEXED) != 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEncrypted()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_ENCRYPTED) != 0;
    }

    /** {@inheritDoc} */
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
     * Appends attribute names to the StringBuilder with proper separation.
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