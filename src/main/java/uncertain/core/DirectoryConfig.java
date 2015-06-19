/*
 * Created on 2009-6-8
 */
package uncertain.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.util.MapHandle;
import uncertain.util.QuickTagParser;
import uncertain.util.resource.ILocatable;

public class DirectoryConfig extends DynamicObject {

	public static final String KEY_PATH_CONFIG = "path-config";

	public static final String KEY_LOG_PATH = "logpath";

	public static final String KEY_CONFIG_PATH = "configpath";

	public static final String KEY_BASE_PATH = "basepath";

	public static final String KEY_TEMP_PATH = "temppath";

	public static DirectoryConfig createDirectoryConfig(CompositeMap dir_config) {
		DirectoryConfig dir = new DirectoryConfig();
		dir.initialize(dir_config);
		return dir;
	}

	/**
	 * merge <code>path_config</code> into current object_context,and translate
	 * ${..} tag using current object_context<br>
	 * 
	 * @see #translateRealPath(String)
	 * 
	 * @param path_config
	 */
	public void merge(CompositeMap path_config) {
		for (Object key : path_config.keySet()) {
			String str = path_config.getString(key);
			str = translateRealPath(str);
			put(key, str);
		}
	}

	public static DirectoryConfig createDirectoryConfig() {
		CompositeMap dir_config = new CompositeMap(KEY_PATH_CONFIG);
		return createDirectoryConfig(dir_config);
	}

	public static void checkIsPathValid(ILocatable locatable, String path) {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory())
			throw BuiltinExceptionFactory.createInvalidPathException(locatable,
					path);

	}

	public String getLogDirectory() {
		return getString(KEY_LOG_PATH);
	}

	public void setLogDirectory(String dir) {
		putString(KEY_LOG_PATH, dir);
	}

	public String getBaseDirectory() {
		return getString(KEY_BASE_PATH);
	}

	public void setBaseDirectory(String base_dir) {
		putString(KEY_BASE_PATH, base_dir);
	}

	public String getConfigDirectory() {
		return getString(KEY_CONFIG_PATH);
	}

	public void setConfigDirectory(String config_dir) {
		putString(KEY_CONFIG_PATH, config_dir);
	}

	public String getTempDirectory() {
		return getString(KEY_TEMP_PATH);
	}

	public void setTempDirectory(String temp_dir) {
		putString(KEY_TEMP_PATH, temp_dir);
	}

	/**
	 * Translate a String containing path tags to real path
	 * 
	 * @param path_with_tag
	 *            A String containing path tag, such as
	 *            "${logDirectory}/dailylog/2012/"
	 * @return
	 */
	public String translateRealPath(String path_with_tag) {
		QuickTagParser parser = new QuickTagParser();
		MapHandle handle = new MapHandle(this.getObjectContext(),
				Character.LOWERCASE_LETTER);
		return parser.parse(path_with_tag, handle);
	}

	/**
	 * Load directory config from a property file
	 * 
	 * @param property_file
	 * @throws IOException
	 */
	public void loadConfigProperties(File property_file) throws IOException {
		Properties props = new Properties();
		FileReader reader = new FileReader(property_file);
		props.load(reader);
		loadConfig(props);
		reader.close();
	}

	/**
	 * Load directory config from a Map
	 * 
	 * @param config_map
	 */
	public void loadConfig(Map config_map) {
		Iterator it = config_map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getKey() == null)
				continue;
			String key = entry.getKey().toString().toLowerCase();
			Object value = entry.getValue();
			this.object_context.put(key, value);
		}
	}

	/**
	 * Check if each value in config map is a valid directory path
	 */
	public void validateConfig() {
		Iterator it = object_context.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getKey() == null)
				continue;
			Object value = entry.getValue();
			if (value == null)
				throw BuiltinExceptionFactory.createInvalidPathException(
						object_context.asLocatable(), "("
								+ entry.getKey().toString() + "=null)");
			String path = value.toString();
			File file = new File(path);
			if (!file.exists() || !file.isDirectory())
				throw BuiltinExceptionFactory.createInvalidPathException(
						object_context.asLocatable(), path);
		}
	}

}
