/*
 * Created on 2009-7-17
 */
package uncertain.schema;

public class Category extends AbstractQualifiedNamed {
    
    String      mParentCategory;
    String      mPrompt;

    public String getParentCategory() {
        return mParentCategory;
    }

    public void setParentCategory(String parent) {
        this.mParentCategory = parent;
    }

    public String getPrompt() {
        return mPrompt;
    }

    public void setPrompt(String prompt) {
        this.mPrompt = prompt;
    }

}
