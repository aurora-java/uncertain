/*
 * Created on 2007-6-9
 */
package org.json;

public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        JSONObject o = new JSONObject();
        o.put("name", "Scott");
        o.put("password", "tiger");

        
        JSONArray array = new JSONArray();
        array.put(o);
        array.put(12);
        array.put("test");
        
        JSONObject o1 = new JSONObject();
        o1.put("array", array);
        
        System.out.println(XML.toString(o1));        
    }

}
