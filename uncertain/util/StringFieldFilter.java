/*
 * Created on 2007-12-7
 */
package uncertain.util;

import uncertain.composite.CompositeMap;

public class StringFieldFilter implements IRecordFilter {
    
    Object          mKey;
    String          mSearchText;
    boolean         mNoSearchText = false;
    boolean         mSearchFromStart = false;
    boolean         mCaseSensitive = false;
    

    /**
     * @param key
     * @param searchFromStart
     */
    public StringFieldFilter(Object key, boolean searchFromStart, boolean caseSensitive) {
        mKey = key;
        mSearchFromStart = searchFromStart;
        mCaseSensitive = caseSensitive;
    }


    public boolean accepts(CompositeMap record) {
        if(mNoSearchText) 
            return true;
        Object value = record.get(mKey);
        if(value==null)
            return mNoSearchText;
        String text = value.toString();
        if(!mCaseSensitive)
            text = text.toUpperCase();
        if(mSearchFromStart)
            return text.startsWith(mSearchText);
        else
            return text.indexOf(mSearchText)>=0;        
    }


    /**
     * @return the mSearchText
     */
    public String getSearchText() {
        return mSearchText;
    }


    /**
     * @param searchText the mSearchText to set
     */
    public void setSearchText(String searchText) {
        mNoSearchText = false;
        mSearchText = searchText;
        if(mSearchText==null)
            mNoSearchText = true;
        else{
            if( mSearchText.length()==0 )
                mNoSearchText = true;
            if( !mCaseSensitive)
                mSearchText = mSearchText.toUpperCase();
        }
            
    }

}
