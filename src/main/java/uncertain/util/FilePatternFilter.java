/*
 * Created on 2005-8-1
 */
package uncertain.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;


/**
 * Filter file name by specified pattern
 * @author Zhou Fan
 * 
 */
public class FilePatternFilter implements FilenameFilter {
    
    Pattern pattern = null;
    
    public FilePatternFilter(String pattern_str){
        pattern = Pattern.compile(pattern_str);
    }

    /** @return true if file name matches specified pattern
     *  @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    public boolean accept(File dir, String name) {
        if(pattern==null) return false;
        return pattern.matcher(name).matches();
    }

}
