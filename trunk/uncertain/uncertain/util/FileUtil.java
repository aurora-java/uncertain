/*
 * Created on 2011-9-6 上午10:45:49
 * $Id$
 */
package uncertain.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

    public static void deleteDirectory(File dir_to_del) throws IOException {
        if (!dir_to_del.exists())
            throw new FileNotFoundException(dir_to_del.getPath());
        if (!dir_to_del.isDirectory())
            throw new IllegalArgumentException(dir_to_del.getCanonicalPath()+" is not directory");
        String[] list = dir_to_del.list();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(dir_to_del, list[i]);

                if (entry.isDirectory()) {
                    deleteDirectory(entry);
                } else {
                    if (!entry.delete())
                        throw new IOException("Can't delete file "
                                + entry.getCanonicalPath());
                }
            }
        }

        if (!dir_to_del.delete())
            throw new IOException("Can't delete file "
                    + dir_to_del.getCanonicalPath());

    }

    public static void deleteDirectory(String dir_to_del) throws IOException {
        File file = new File(dir_to_del);
        deleteDirectory(file);
    }


}
