/*
 * StringSplitter.java
 *
 * Created on 2001��12��13��, ����3:43
 */

package uncertain.util;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class StringSplitter  {
    
    public static String[] split2( String str, String separator){
        StringTokenizer t = new StringTokenizer(str, separator);        
        /*
        String[] s = new String[t.countTokens()];
        for(int i=0; i<s.length; i++)
            s[i] = t.nextToken();
        return s;
        */
        LinkedList lst = new LinkedList();
        while(t.hasMoreElements())
            lst.add(t.nextToken());
        return (String[])lst.toArray(new String[lst.size()]);

    }
 
        
    public static void split( String str, int start_id, char spt_char, boolean compare_by_char, StringSplitHandle handle){
        if( str == null) return;
        int id = start_id;
        int lth = str.length();
        StringBuilder buf = new StringBuilder();
        boolean in_sequence = false;
        
        for(; id<lth; id++){
            char ch = str.charAt(id);
            if( ch == spt_char){
                if( compare_by_char){
                    handle.processString( buf.toString());
                    buf.setLength(0);
                }else{
                    if( !in_sequence){
                     handle.processString( buf.toString());
                     buf.setLength(0);
                     in_sequence = true;
                    }
                }
            }
            else{ 
                if( !compare_by_char && in_sequence){
                    in_sequence = false;
                }
                buf.append( ch);
            }
        }
        if( buf.length()>0) handle.processString( buf.toString());
    }

    /** split string to a list
     * @param str String to split
     * @param spt_char character that divides string
     * @param compare_by_char if set to false, split("A   B",' ') produces same result as split("A B",' ')
     */
    public static List split( String str,  char spt_char, boolean compare_by_char){
        if( str == null) return null;
        CollectionHandle handle = CollectionHandle.newInstance();
        split( str, 0, spt_char, compare_by_char,  handle);
        return handle.getStrings();
    }
    
    public static String[] splitToArray(String str,  char spt_char, boolean compare_by_char){
        List lst =  split(str,spt_char, compare_by_char);
        if(lst==null) 
            return null;
        else
            return (String[])lst.toArray(new String[lst.size()]);
    }
    
    public static String concatenate( String[] array, String separator_char){
        StringBuilder buf = new StringBuilder();
        for(int i=0; i<array.length; i++){
            if(i>0) buf.append(separator_char);
            buf.append(array[i]);
        }
        return buf.toString();
    }
    
    public static void main(String[] args){
        /*
        String[] a = new String[3];
        a[0] = "a"; a[1]="b"; a[2] ="c";
        System.out.println(concatenate(a,","));
        */
        long tick = System.currentTimeMillis();
        for(long i=0; i<300000L; i++){
            //String a = "a,b,c,d,e,f, g, d, h";
            //List l = split(a,',',true);
            //String[] b = split2(a,",");
            //String[] b = splitToArray(a,',',true);
        }
        tick = System.currentTimeMillis() - tick;
        System.out.println(tick);
    }

}
