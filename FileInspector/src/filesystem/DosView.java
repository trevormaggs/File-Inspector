package filesystem;

/**
 * Provides a comprehensive view of Windows-specific file attributes. This interface exposes the
 * standard DOS attributes as well as extended attributes retrieved from the raw Win32 bitmask.
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/windows/win32/fileio/file-attribute-constants">Win32 File Attribute Constants</a>
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/windows/win32/api/minwinbase/ns-minwinbase-win32_find_dataw">WIN32_FIND_DATAW structure</a>
 */
public interface DosView
{
    int FILE_ATTRIBUTE_READONLY = 0x00000001;
    int FILE_ATTRIBUTE_HIDDEN = 0x00000002;
    int FILE_ATTRIBUTE_SYSTEM = 0x00000004;
    int FILE_ATTRIBUTE_DIRECTORY = 0x00000010;
    int FILE_ATTRIBUTE_ARCHIVE = 0x00000020;
    int FILE_ATTRIBUTE_DEVICE = 0x00000040;
    int FILE_ATTRIBUTE_NORMAL = 0x00000080;
    int FILE_ATTRIBUTE_TEMPORARY = 0x00000100;
    int FILE_ATTRIBUTE_SPARSE_FILE = 0x00000200;
    int FILE_ATTRIBUTE_REPARSE_POINT = 0x00000400;
    int FILE_ATTRIBUTE_COMPRESSED = 0x00000800;
    int FILE_ATTRIBUTE_OFFLINE = 0x00001000;
    int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 0x00002000;
    int FILE_ATTRIBUTE_ENCRYPTED = 0x00004000;
    int FILE_ATTRIBUTE_INTEGRITY_STREAM = 0x00008000;
    int FILE_ATTRIBUTE_VIRTUAL = 0x00010000;
    int FILE_ATTRIBUTE_NO_SCRUB_DATA = 0x00020000;
    int FILE_ATTRIBUTE_PINNED = 0x00080000;
    int FILE_ATTRIBUTE_UNPINNED = 0x00100000;
    int FILE_ATTRIBUTE_RECALL_ON_OPEN = 0x00040000;
    int FILE_ATTRIBUTE_RECALL_ON_DATA_ACCESS = 0x00400000;

    /**
     * Checks if the file is an archive file. Typically used by backup programs to identify files
     * that need to be backed up.
     * 
     * @return true if the archive bit is set
     */
    boolean isArchiveFile();

    /**
     * Checks if the file is hidden.
     * 
     * @return true if the file is marked as hidden by the filesystem
     */
    boolean isHidden();

    /**
     * Checks if the file is read-only.
     * 
     * @return true if the file cannot be written to or deleted
     */
    boolean isReadOnly();

    /**
     * Checks if the file is a system file or is used exclusively by the operating system.
     * 
     * @return true if the system bit is set
     */
    boolean isSystemFile();

    /**
     * Returns the raw 32-bit integer bitmask containing all Windows file attributes.
     * 
     * This represents the {@code dwFileAttributes} value from the Win32 API.
     * 
     * @return the raw integer bitmask
     */
    int getAttributesMask();

    /**
     * Checks if the file is being used for temporary storage. File systems avoid writing the data
     * back to mass storage if sufficient cache memory is available.
     * 
     * @return true if the FILE_ATTRIBUTE_TEMPORARY bit is set
     */
    boolean isTemporary();

    /**
     * Checks if the file is a sparse file. Sparse files are large files that contain mostly zeros,
     * where only non-zero data is physically stored on disk.
     * 
     * @return true if the FILE_ATTRIBUTE_SPARSE_FILE bit is set
     */
    boolean isSparseFile();

    /**
     * Checks if the file or directory has an associated reparse point, or is a symbolic link /
     * junction.
     * 
     * @return true if the FILE_ATTRIBUTE_REPARSE_POINT bit is set
     */
    boolean isReparsePoint();

    /**
     * Checks if the file or directory is compressed. For a file, this means all data in the file is
     * compressed.
     * 
     * @return true if the FILE_ATTRIBUTE_COMPRESSED bit is set
     */
    boolean isCompressed();

    /**
     * Checks if the file data is not immediately available. This attribute indicates that the file
     * data is physically moved to offline storage (e.g., Remote Storage).
     * 
     * @return true if the FILE_ATTRIBUTE_OFFLINE bit is set
     */
    boolean isOffline();

    /**
     * Checks if the file is not to be indexed by the content indexing service.
     * 
     * @return true if the FILE_ATTRIBUTE_NOT_CONTENT_INDEXED bit is set
     */
    boolean isNotContentIndexed();

    /**
     * Checks if the file or directory is encrypted. For a file, this means all data streams in the
     * file are encrypted.
     * 
     * @return true if the FILE_ATTRIBUTE_ENCRYPTED bit is set
     */
    boolean isEncrypted();

    /**
     * Checks if the file is excluded from the data integrity scan. When this attribute is set on a
     * directory, it is the default for new files created therein. (Specific to ReFS volumes).
     * 
     * @return true if the FILE_ATTRIBUTE_NO_SCRUB_DATA bit is set
     */
    boolean isNoScrubData();
    
    String getAttributesString();
}