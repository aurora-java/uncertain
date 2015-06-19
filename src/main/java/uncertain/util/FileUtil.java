/*
 * Created on 2011-9-6 上午10:45:49
 * $Id$
 */
package uncertain.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

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

    public static List<File> getSortedList(File[] files) {
        List<File> lst = new LinkedList<File>();
        for (int i = 0; i < files.length; i++)
            lst.add(files[i]);
        Collections.sort(lst, new Comparator() {
    
            public int compare(Object o1, Object o2) {
                return ((File) o1).getAbsolutePath().compareTo(
                        ((File) o2).getAbsolutePath());
            }
    
            public boolean equals(Object obj) {
                return obj == this;
            }
    
        });
        return lst;
    }


}
