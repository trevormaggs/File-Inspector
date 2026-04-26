package filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Map;

public final class DosNode extends AbstractFileNode implements DosView
{
    private final DosFileAttributes dosAttribs;
    private final Map<String, Object> attrMap;

    public DosNode(Path path, boolean followSymlinks) throws IOException
    {
        this(path, followSymlinks, (followSymlinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS}));
    }

    private DosNode(Path path, boolean follow, LinkOption[] options) throws IOException
    {
        this(path, Files.readAttributes(path, DosFileAttributes.class, options), options);
    }

    private DosNode(Path path, DosFileAttributes attrs, LinkOption[] options) throws IOException
    {
        super(path, attrs);

        this.dosAttribs = attrs;
        this.attrMap = Files.readAttributes(path, "dos:*", options);
    }

    @Override
    public boolean isArchiveFile()
    {
        return dosAttribs.isArchive();
    }

    @Override
    public boolean isHidden()
    {
        return dosAttribs.isHidden();
    }

    @Override
    public boolean isReadOnly()
    {
        return dosAttribs.isReadOnly();
    }

    @Override
    public boolean isSystemFile()
    {
        return dosAttribs.isSystem();
    }

    @Override
    public int getAttributesMask()
    {
        Object mask = attrMap.get("attributes");

        return (mask instanceof Number) ? ((Number) mask).intValue() : 0;
    }

    @Override
    public boolean isTemporary()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_TEMPORARY) != 0;
    }

    @Override
    public boolean isSparseFile()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_SPARSE_FILE) != 0;
    }

    @Override
    public boolean isReparsePoint()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_REPARSE_POINT) != 0;
    }

    @Override
    public boolean isCompressed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_COMPRESSED) != 0;
    }

    @Override
    public boolean isOffline()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_OFFLINE) != 0;
    }

    @Override
    public boolean isNotContentIndexed()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_NOT_CONTENT_INDEXED) != 0;
    }

    @Override
    public boolean isEncrypted()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_ENCRYPTED) != 0;
    }

    @Override
    public boolean isNoScrubData()
    {
        return (getAttributesMask() & FILE_ATTRIBUTE_NO_SCRUB_DATA) != 0;
    }
}