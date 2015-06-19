/*
 * Created on 2011-9-15 上午11:15:39
 * $Id$
 */
package uncertain.proc.trace;

import java.util.LinkedList;

import uncertain.composite.CompositeMap;
import uncertain.proc.IEntry;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.Location;

public class TraceElement {

    long enterTime = System.currentTimeMillis();
    long exitTime;
    String sourceName;
    String sourceFile;
    Location sourceLocation;
    LinkedList<TraceElement> childs;
    TraceElement parent;

    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * @param sourceName name of current entry
     * @param sourceLocation source location of current entry
     */
    public TraceElement(String sourceName, ILocatable sourceLocation) {
        this.sourceName = sourceName;
        if (sourceLocation != null) {
            this.sourceFile = sourceLocation.getOriginSource();
            this.sourceLocation = sourceLocation.getOriginLocation();
        }
    }

    public TraceElement(IEntry entry) {
        this(entry.getName(),
                (entry instanceof ILocatable) ? (ILocatable) entry : null);
    }
    
    public TraceElement( String name ){
       this.sourceName = name;
    }
    /*
    public TraceElement( String sourceName, String sourceFile, Location location ){
        this.enterTime = System.currentTimeMillis();        
    }
     */
    public long getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    public long getExitTime() {
        return exitTime;
    }

    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public TraceElement getParent() {
        return parent;
    }

    public void setParent(TraceElement parent) {
        this.parent = parent;
    }

    public void addChild(TraceElement trace) {
        if(childs==null)
            childs = new LinkedList<TraceElement>();
        childs.add(trace);
    }

    private void toSingleLine(StringBuffer buf) {
        buf.append("at ").append(sourceName);
        if (sourceFile != null) {
            buf.append("(").append(sourceFile);
            if (sourceLocation != null)
                buf.append(":").append(sourceLocation.getStartLine());
            buf.append(")");
        }
    }

    public String toStackTrace() {
        StringBuffer buf = new StringBuffer();
        toSingleLine(buf);
        if (parent != null) {
            buf.append(LINE_SEPARATOR);
            buf.append(parent.toStackTrace());
        }
        return buf.toString();
    }

    public long getDuration() {
        if (exitTime < enterTime)
            return 0;
        return exitTime - enterTime;
    }
    
    private void populateTo( CompositeMap entry ){
        entry.put("name", this.getSourceName());
        entry.put("enter_time",  new java.sql.Timestamp(this.getEnterTime()));
        entry.put("exit_time", new java.sql.Timestamp(this.getExitTime()));
        if(this.sourceFile!=null)
            entry.put("source", sourceFile);
        if(this.sourceLocation!=null)
            entry.put("line", sourceLocation.getStartLine());
        entry.put("duration", this.getDuration());
    }
    
    public CompositeMap asCompositeMap(){
        CompositeMap entry = new CompositeMap("entry");
        populateTo(entry);
        if(childs!=null){
            for(TraceElement t:childs){
                entry.addChild( t.asCompositeMap());
            }
        }
        return entry;
    }
    
    public CompositeMap asCompositeMap( boolean leaf_node_only, boolean ignore_duration_zero ){
        CompositeMap root = new CompositeMap("entry");
        if(!leaf_node_only)
            populateTo(root);
        if(childs!=null){
            for(TraceElement t:childs){
                t.addToCompositeMap(root, leaf_node_only, ignore_duration_zero);
            }
        }
        return root;
        
    }
    
    private void addToCompositeMap( CompositeMap root, boolean leaf_node_only, boolean ignore_duration_zero ){
        boolean should_add_this = true;
        if(leaf_node_only)
            if(childs!=null)
                should_add_this = false;
        if(ignore_duration_zero)
            if(getDuration()==0)
                should_add_this = false;
        CompositeMap to_add = root;
        if(should_add_this){
            CompositeMap entry = new CompositeMap("entry");
            populateTo(entry);
            root.addChild(entry);
            to_add = entry;
        }
        if(childs!=null){
            for(TraceElement t:childs){
                t.addToCompositeMap(to_add, leaf_node_only, ignore_duration_zero);
            }
        }
    }      
    
    public void clear(){
        if(childs!=null)
            for(TraceElement e:childs)
                e.clear();
    }

}
