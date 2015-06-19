package uncertain.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

public class VirtualFileProcessor {
	final static String VFS_CLASS_NAME_KEY = "org.jboss.vfs.VirtualFile";
	final static String VFS_CLASS_METHOD_NAME_KEY = "getPhysicalFile";

	public static File getPhysicalFile(URL url) {
		URLConnection conn;
		Object vf;
		File contentsFile = null;
		try {
			conn = url.openConnection();
			vf = conn.getContent();
			String className = vf.getClass().getName();
			if (VFS_CLASS_NAME_KEY.equalsIgnoreCase(className)) {
				Class vfsClass = Class.forName(VFS_CLASS_NAME_KEY);
				Method m = vfsClass.getMethod(VFS_CLASS_METHOD_NAME_KEY, null);
				contentsFile = (File) m.invoke(vfsClass.cast(vf), null);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return contentsFile;
	}
}
