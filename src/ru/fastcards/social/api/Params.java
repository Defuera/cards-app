package ru.fastcards.social.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Params {
    //TreeMap �?½Ñƒ�?¶�?µ�?½ �?±Ñ‹�?» Ñ‡Ñ‚�?¾�?±Ñ‹ Ñ��?¾Ñ€Ñ‚�?¸Ñ€�?¾�?²�?°Ñ‚ÑŒ �?¿�?°Ñ€�?°�?¼�?µÑ‚Ñ€Ñ‹ �?¿�?¾ �?¸�?¼�?µ�?½�?¸, Ñ��?µ�?¹Ñ‡�?°Ñ� Ñ�Ñ‚�?¾ Ñƒ�?¶�?µ �?½�?µ �?²�?°�?¶�?½�?¾, �?³�?»�?°�?²�?½�?¾ �?¿�?¾�?´�?¿�?¸Ñ�Ñ‹�?²�?°Ñ‚ÑŒ �?¸ �?¿�?µÑ€�?µ�?´�?°�?²�?°Ñ‚ÑŒ �?¿�?°Ñ€�?°�?¼�?µÑ‚Ñ€Ñ‹ �?² �?¾�?´�?½�?¾�?¼ �?¸ Ñ‚�?¾Ñ‚�?¼ �?¶�?µ �?¿�?¾Ñ€Ñ��?´�?º�?µ
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
