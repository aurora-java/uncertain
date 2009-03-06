/*
 * TagParseHandle.java
 *
 * Created on 2001年12月13日, 下午3:39
 */

package uncertain.util;

/**
 * 
 */
/**
 *
 * @author  Zhou Fan
 * @version 
 */
public interface TagParseHandle {
  
  /** called by parser when encounters a tag 
   *  @param index position of the tag in input sequence
   *  @param tag the tag encountered
   *  @return processed tag
   */
  public String  ProcessTag(int index, String tag);
  
  
/**
 * called by parser when encounters a normal character 
 * @param index position of the character in input sequence
 * @param ch the character
 * @return processed character(casted to int), usually the same as ch.
 *  If the return value is -1, the character will not be appended to result.
 */
  public int ProcessCharacter( int index, char ch);
  
}
