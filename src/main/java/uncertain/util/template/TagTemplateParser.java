/*
 * Created on 2007-8-13 ����09:41:55
 */
package uncertain.util.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import uncertain.util.FastStringReader;
import uncertain.util.QuickTagParser;
import uncertain.util.TagParseHandle;

public class TagTemplateParser {

	ITagCreatorRegistry mDefaultRegistry = TagCreatorRegistry.getInstance();

	private static class ParseHandle implements TagParseHandle {

		TextTemplate mTemplate = new TextTemplate();
		StringBuffer mBuf = null;

		ITagCreatorRegistry mRegistry;
		String mSourceName;

		public ParseHandle(ITagCreatorRegistry registry) {
			mRegistry = registry;
		}

		public void setSourceName(String name) {
			mSourceName = name;
			mTemplate.setSourceName(name);
		}

		public String ProcessTag(int index, String tag) {
			if (mBuf != null) {
				mTemplate.addContent(mBuf.toString());
				mBuf.setLength(0);
			}
			String name_space = null;
			String tag_content = tag;
			int id = tag.indexOf(':');
			if (id > 0) {
				name_space = tag.substring(0, id);
				tag_content = tag.substring(id + 1);
			}
			ITagCreator creator = mRegistry.getTagCreator(name_space);
			ITagContent tc = null;
			if (creator != null) {
				tc = creator.createInstance(name_space, tag_content);
				if (tc == null)
					tc = new ErrorTag(mSourceName, 0, 0, "Unknown tag:" + tag);
			} else
				tc = new ErrorTag(mSourceName, 0, 0, "Unknown tag:" + tag);

			mTemplate.addContent(tc);
			return null;
		}

		public int ProcessCharacter(int index, char ch) {
			if (mBuf == null)
				mBuf = new StringBuffer();
			mBuf.append(ch);
			return -1;
		}

		/*
		 * public StringBuffer getBuffer(){ return mBuf; }
		 */

		public void finish() {
			if (mBuf != null)
				if (mBuf.length() > 0)
					mTemplate.addContent(mBuf.toString());
		}
	};

	public void setDefaultRegistry(ITagCreatorRegistry reg) {
		mDefaultRegistry = reg;
	}

	public TextTemplate buildTemplate(InputStream is) throws IOException {
		return buildTemplate(is, mDefaultRegistry);
	}

	public TextTemplate buildTemplate(InputStream is, ITagCreatorRegistry reg)
			throws IOException {
		return buildTemplate(null, is, reg);
	}

	public TextTemplate buildTemplate(String source_name, InputStream is,
			ITagCreatorRegistry reg) throws IOException {
		return buildTemplate(source_name, new InputStreamReader(is), reg);
	}

	public TextTemplate buildTemplate(Reader reader) throws IOException {
		return buildTemplate(null, reader, mDefaultRegistry);
	}

	public TextTemplate buildTemplate(Reader reader,
			ITagCreatorRegistry registry) throws IOException {
		return buildTemplate(null, reader, registry);
	}

	public TextTemplate buildTemplate(File template_file,
			ITagCreatorRegistry registry) throws IOException {
		FileInputStream fis = new FileInputStream(template_file);
		InputStreamReader reader = new InputStreamReader(fis, "utf-8");
		try {
			return buildTemplate(template_file.getPath(), reader, registry);
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	public TextTemplate buildTemplate(File template_file) throws IOException {
		return buildTemplate(template_file, mDefaultRegistry);
	}

	public TextTemplate buildTemplate(String source_file_name, Reader reader,
			ITagCreatorRegistry registry) throws IOException {
		QuickTagParser parser = new QuickTagParser();
		ParseHandle handle = new ParseHandle(registry);
		handle.setSourceName(source_file_name);
		parser.parse(reader, handle);
		parser.clear();
		handle.finish();
		return handle.mTemplate;
	}

	public TextTemplate buildTemplate(String content) {
		return buildTemplate(content,mDefaultRegistry);
	}

	public TextTemplate buildTemplate(String content,
			ITagCreatorRegistry tagCreatorRegistry) {
		QuickTagParser parser = new QuickTagParser();
		ParseHandle handle = new ParseHandle(tagCreatorRegistry);
		handle.setSourceName(null);
		parser.parse(content, handle);
		parser.clear();
		handle.finish();
		return handle.mTemplate;
	}

}
