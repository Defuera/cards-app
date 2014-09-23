package ru.fastcards.social.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Counters {
    /**
	 * @uml.property  name="friends"
	 */
    public int friends;
    /**
	 * @uml.property  name="messages"
	 */
    public int messages;
    /**
	 * @uml.property  name="notifications"
	 */
    public int notifications;//new replies notifications
    
    public static Counters parse(JSONObject o) throws JSONException {
        Counters a = new Counters();
        if(o==null)
            return a;
        a.friends = o.optInt("friends");
        a.messages = o.optInt("messages");
        a.notifications = o.optInt("notifications");
        return a;
    }

}
