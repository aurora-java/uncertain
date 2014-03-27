/*
 * Created on 2008-7-10
 */
package uncertain.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class QuickTagParser {

	TagProcessor processor;

	/**
	 * @param processor
	 */
	public QuickTagParser(TagProcessor processor) {
		this.processor = processor;
	}

	public QuickTagParser() {
		processor = new UnixShellTagProcessor();
	}

	void appendParsedTag(int index, TagProcessor processor, StringBuilder buf,
			TagParseHandle handle) {
		String str = null;
		String tag = processor.getTagString();
		if (tag != null)
			if (tag.length() > 0)
				str = handle.ProcessTag(index, tag);
		if (str != null)
			buf.append(str);
	}

	void appendNormalChar(int index, TagParseHandle handle, StringBuilder buf,
			char ch) {
		int n = handle.ProcessCharacter(index, ch);
		if (n >= 0)
			buf.append((char) n);
	}

	void appendNormalStrings(int index, TagParseHandle handle,
			StringBuilder buf, StringBuilder content) {
		for (int i = 0; i < content.length(); i++) {
			appendNormalChar(index + i, handle, buf, content.charAt(i));
		}
	}

	@Deprecated
	public String parse(Reader reader, TagParseHandle handle)
			throws IOException {

		int index = 0;
		int tag_begin = 0;
		char tag_chr = processor.getStartingEscapeChar();

		StringBuilder result = new StringBuilder();
		StringBuilder pending_char = new StringBuilder();
		int ch;

		processor.setEscapeState(false);

		while ((ch = reader.read()) != -1) {
			char chr = (char) ch;
			if (!processor.isEscapeState()) {
				if (tag_chr == chr) {
					pending_char.append(chr);
					processor.setEscapeState(true);
					tag_begin = index;
				} else {
					appendNormalChar(index, handle, result, chr);
				}
			} else {
				if (tag_chr == chr) {
					// result.append(pending_char);
					appendNormalStrings(index, handle, result, pending_char);
					pending_char.setLength(0);
					pending_char.append(chr);
					continue;
				}
				pending_char.append(chr);
				int rst = processor.accept(chr);
				if (rst == TagProcessor.RESULT_NORMAL_CHAR
						|| rst == TagProcessor.RESULT_ESCAPE_END_CHAR) {
					processor.setEscapeState(false);
					appendParsedTag(tag_begin, processor, result, handle);
					pending_char.setLength(0);
				} else if (rst == TagProcessor.RESULT_WRONG_CHAR) {
					processor.setEscapeState(false);
					// result.append(pending_char);
					appendNormalStrings(index, handle, result, pending_char);
					pending_char.setLength(0);
				}
			}
			index++;
		}

		if (processor.isEscapeState())
			appendParsedTag(tag_begin, processor, result, handle);

		if (pending_char.length() > 0)
			// result.append(pending_char);
			appendNormalStrings(index - pending_char.length(), handle, result,
					pending_char);

		return result.toString();

	}

	public void clear() {
		processor.clear();
		processor = null;
	}

	/**
	 * this method is more effective than {@link #parse(Reader, TagParseHandle)}
	 * 
	 * @param str
	 * @param handle
	 * @return
	 */
	public String parse(String str, TagParseHandle handle) {
		if (str == null)
			return null;
		int index = 0;
		int tag_begin = 0;
		char tag_chr = processor.getStartingEscapeChar();

		StringBuilder result = new StringBuilder(str.length());// almost never
																// needs
																// expands.
		StringBuilder pending_char = new StringBuilder();

		processor.setEscapeState(false);

		for (int i = 0, len = str.length(); i < len; i++) {
			char chr = str.charAt(i);
			if (!processor.isEscapeState()) {
				if (tag_chr == chr) {
					pending_char.append(chr);
					processor.setEscapeState(true);
					tag_begin = index;
				} else {
					appendNormalChar(index, handle, result, chr);
				}
			} else {
				if (tag_chr == chr) {
					// result.append(pending_char);
					appendNormalStrings(index, handle, result, pending_char);
					pending_char.setLength(0);
					pending_char.append(chr);
					continue;
				}
				pending_char.append(chr);
				int rst = processor.accept(chr);
				if (rst == TagProcessor.RESULT_NORMAL_CHAR
						|| rst == TagProcessor.RESULT_ESCAPE_END_CHAR) {
					processor.setEscapeState(false);
					appendParsedTag(tag_begin, processor, result, handle);
					pending_char.setLength(0);
				} else if (rst == TagProcessor.RESULT_WRONG_CHAR) {
					processor.setEscapeState(false);
					// result.append(pending_char);
					appendNormalStrings(index, handle, result, pending_char);
					pending_char.setLength(0);
				}
			}
			index++;
		}

		if (processor.isEscapeState())
			appendParsedTag(tag_begin, processor, result, handle);

		if (pending_char.length() > 0)
			// result.append(pending_char);
			appendNormalStrings(index - pending_char.length(), handle, result,
					pending_char);

		return result.toString();
	}
}
