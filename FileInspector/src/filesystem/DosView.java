package filesystem;

/**
 * Provides a comprehensive view of Windows-specific file attributes. This interface exposes
 * standard DOS attributes as well as extended attributes retrieved from the raw Win32 bitmask.
 * 
 * <p>
 * Implementations should use the bitwise constants defined below to initialise the state of the
 * file attributes.
 * </p>
 * 
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/windows/win32/fileio/file-attribute-constants">Win32 File
 *      Attribute Constants</a>
 * @see <a href=
 *      "https://learn.microsoft.com/en-us/windows/win32/api/minwinbase/ns-minwinbase-win32_find_dataw">WIN32_FIND_DATAW
 *      structure</a>
 */
public interface DosView
{
    // Low Byte (0x01 to 0x80)
    int FILE_ATTRIBUTE_READONLY = 0x01;
    int FILE_ATTRIBUTE_HIDDEN = 0x02;
    int FILE_ATTRIBUTE_SYSTEM = 0x04;
    int FILE_ATTRIBUTE_DIRECTORY = 0x10;
    int FILE_ATTRIBUTE_ARCHIVE = 0x20;
    int FILE_ATTRIBUTE_DEVICE = 0x40;
    int FILE_ATTRIBUTE_NORMAL = 0x80;

    // Second Byte (0x100 to 0x8000)
    int FILE_ATTRIBUTE_TEMPORARY = 0x100;
    int FILE_ATTRIBUTE_SPARSE_FILE = 0x200;
    int FILE_ATTRIBUTE_REPARSE_POINT = 0x400;
    int FILE_ATTRIBUTE_COMPRESSED = 0x800;
    int FILE_ATTRIBUTE_OFFLINE = 0x1000;
    int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 0x2000;
    int FILE_ATTRIBUTE_ENCRYPTED = 0x4000;
    int FILE_ATTRIBUTE_INTEGRITY_STREAM = 0x8000;

    // High Bytes (0x10000 and above)
    int FILE_ATTRIBUTE_VIRTUAL = 0x10000;
    int FILE_ATTRIBUTE_NO_SCRUB_DATA = 0x20000;
    int FILE_ATTRIBUTE_RECALL_ON_OPEN = 0x40000;
    int FILE_ATTRIBUTE_PINNED = 0x80000;
    int FILE_ATTRIBUTE_UNPINNED = 0x100000;
    int FILE_ATTRIBUTE_RECALL_ON_DATA_ACCESS = 0x400000;

    boolean isArchiveFile();
    boolean isHidden();
    boolean isReadOnly();
    boolean isSystemFile();
    int getAttributesMask();
    boolean isTemporary();
    boolean isSparseFile();
    boolean isReparsePoint();
    boolean isCompressed();
    boolean isOffline();
    boolean isNotContentIndexed();
    boolean isEncrypted();
    boolean isNoScrubData();
    String getAttributesString();
}