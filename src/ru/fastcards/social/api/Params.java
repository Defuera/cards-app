package ru.fastcards.social.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Params {
    //TreeMap Ã?Â½Ã‘Æ’Ã?Â¶Ã?ÂµÃ?Â½ Ã?Â±Ã‘â€¹Ã?Â» Ã‘â€¡Ã‘â€šÃ?Â¾Ã?Â±Ã‘â€¹ Ã‘ï¿½Ã?Â¾Ã‘â‚¬Ã‘â€šÃ?Â¸Ã‘â‚¬Ã?Â¾Ã?Â²Ã?Â°Ã‘â€šÃ‘Å’ Ã?Â¿Ã?Â°Ã‘â‚¬Ã?Â°Ã?Â¼Ã?ÂµÃ‘â€šÃ‘â‚¬Ã‘â€¹ Ã?Â¿Ã?Â¾ Ã?Â¸Ã?Â¼Ã?ÂµÃ?Â½Ã?Â¸, Ã‘ï¿½Ã?ÂµÃ?Â¹Ã‘â€¡Ã?Â°Ã‘ï¿½ Ã‘ï¿½Ã‘â€šÃ?Â¾ Ã‘Æ’Ã?Â¶Ã?Âµ Ã?Â½Ã?Âµ Ã?Â²Ã?Â°Ã?Â¶Ã?Â½Ã?Â¾, Ã?Â³Ã?Â»Ã?Â°Ã?Â²Ã?Â½Ã?Â¾ Ã?Â¿Ã?Â¾Ã?Â´Ã?Â¿Ã?Â¸Ã‘ï¿½Ã‘â€¹Ã?Â²Ã?Â°Ã‘â€šÃ‘Å’ Ã?Â¸ Ã?Â¿Ã?ÂµÃ‘â‚¬Ã?ÂµÃ?Â´Ã?Â°Ã?Â²Ã?Â°Ã‘â€šÃ‘Å’ Ã?Â¿Ã?Â°Ã‘â‚¬Ã?Â°Ã?Â¼Ã?ÂµÃ‘â€šÃ‘â‚¬Ã‘â€¹ Ã?Â² Ã?Â¾Ã?Â´Ã?Â½Ã?Â¾Ã?Â¼ Ã?Â¸ Ã‘â€šÃ?Â¾Ã‘â€šÃ?Â¼ Ã?Â¶Ã?Âµ Ã?Â¿Ã?Â¾Ã‘â‚¬Ã‘ï¿½Ã?Â´Ã?ÂºÃ?Âµ
    /**
	 * @uml.property  name="args"
	 * @uml.associationEnd  qualifier="param_name:java.lang.String java.lang.String"
	 */
    TreeMap<String, String> args = new TreeMap<String, String>();
    /**
	 * @uml.property  name="method_name"
	 */
    public String method_name;
	private String method;
    
    public Params(String method_name){
        this.method_name=method_name;
    }

    public Params() {
	}

	public void put(String param_name, String param_value) {
        if(param_value==null || param_value.length()==0)
            return;
        args.put(param_name, param_value);
    }
    
    public String get(String param_name) {
            return args.get(param_name);
    }
    
    public TreeMap<String, String> getArgs() {
        return args;
}
    
    public void put(String param_name, Long param_value) {
        if(param_value==null)
            return;
        args.put(param_name, Long.toString(param_value));
    }
    
    public void put(String param_name, Integer param_value) {
        if(param_value==null)
            return;
        args.put(param_name, Integer.toString(param_value));
    }
    
    public void putDouble(String param_name, double param_value) {
        args.put(param_name, Double.toString(param_value));
    }
    
    public String getParamsString() {
        String params="";
        try {
            for(Entry<String, String> entry:args.entrySet()){
                if(params.length()!=0)
                    params+="&";
                params+=(entry.getKey()+"="+URLEncoder.encode(entry.getValue(), "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return params;
    }

}
