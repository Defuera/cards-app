package ru.fastcards.social.api;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoTag {
    
    /**
	 * @uml.property  name="owner_id"
	 */
    public Long owner_id;
    /**
	 * @uml.property  name="pid"
	 */
    public long pid;
    
    /**
	 * @uml.property  name="uid"
	 */
    public long uid;
    /**
	 * @uml.property  name="tag_id"
	 */
    public long tag_id;
    /**
	 * @uml.property  name="placer_id"
	 */
    public long placer_id;
    /**
	 * @uml.property  name="tagged_name"
	 */
    public String tagged_name;
    /**
	 * @uml.property  name="date"
	 */
    public long date;
    /**
	 * @uml.property  name="x"
	 */
    public double x;
    /**
	 * @uml.property  name="y"
	 */
    public double y;
    /**
	 * @uml.property  name="x2"
	 */
    public double x2;
    /**
	 * @uml.property  name="y2"
	 */
    public double y2;
    /**
	 * @uml.property  name="viewed"
	 */
    public int viewed;
    
    public static PhotoTag parse(JSONObject o) throws NumberFormatException, JSONException {
        PhotoTag t = new PhotoTag();
        t.uid = o.getLong("uid");
        t.tag_id = o.optLong("tag_id");
        t.placer_id = o.optLong("placer_id");
        t.tagged_name = VkApi.unescape(o.optString("tagged_name"));
        t.date = o.optLong("date");
        t.x = o.optDouble("x");
        t.x2 = o.optDouble("x2");
        t.y = o.optDouble("y");
        t.y2 = o.optDouble("y2");
        t.viewed = o.optInt("viewed");
        return t;
    }
    
    public PhotoTag() { 
        
    }
    
    public PhotoTag(Long owner_id, long pid, long uid, double x, double y, double x2, double y2) { 
        this.owner_id = owner_id;
        this.pid = pid;
        this.uid = uid;
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
    }
}
