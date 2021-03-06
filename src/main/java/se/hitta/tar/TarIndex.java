package se.hitta.tar;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Iterators;

/**
 * This class will build an index of a provided tar archive.
 */
public class TarIndex implements Iterable<TarHeader>
{
    private final Map<String, TarHeader> headers = new HashMap<String, TarHeader>();
    public final int size;
    private final File tarFile;
    private final Date lastModified;
    
    public TarIndex(File tarFile) throws IOException
    {
        this.tarFile = tarFile;
        this.lastModified = new Date(tarFile.lastModified());

        TarHeaderIterator tarHeaderIterator = new TarHeaderIterator(tarFile);

        try
        {
            while (tarHeaderIterator.hasNext())
            {
                TarHeader header = tarHeaderIterator.next();
                if(header.getLinkFlag() == TarHeader.LF_NORMAL)
                {
                    headers.put(header.getName(), header);                    
                }
            }
        }
        finally{
            tarHeaderIterator.close();
        }
        this.size = headers.size();
    }
    
    /**
     * @param key The path of the file, as saved in the tar archive (i.e. including directories)
     * @return a {@link TarHeader} for the given key, or null if there is no indexed {@link TarHeader} for the given key/path
     */
    public TarHeader get(String key)
    {
        return headers.get(key);
    }

    /**
     * @return the tar archive this index was initialized with
     */
    public File getTarFile()
    {
        return tarFile;
    }
    
    /**
     * @return the "last modified" property of the tar archive this index was initialized with
     */
    public Date getLastModified()
    {
        return lastModified;
    }

    @Override
    public Iterator<TarHeader> iterator()
    {
        return Iterators.unmodifiableIterator(this.headers.values().iterator());
    }
}
