package ru.fastcards.social.api;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.example.theproject.Constants;

import android.widget.ImageView;

//Fields are optional. Should be null if not populated
public class User implements Serializable, SocialObject {
    private static final long serialVersionUID = 1L;

    public long uid;
    public String first_name;
    public String last_name;

    public String name;
    public String birthdate; //bdate
    public String photo;//the same as photo_rec
    public String photo_big;
    public String photo_medium;
    public String photo_medium_rec;

    public String friends_list_ids = null;
    public int albums_count;
    public int friends_count;
    public int user_photos_count;

	private boolean selected;
    public Integer sex=-1;

 
    public static User vkParse(JSONObject o) throws JSONException {
        User u = new User();
        u.uid = Long.parseLong(o.getString("uid"));
        if(!o.isNull("first_name"))
            u.first_name = VkApi.unescape(o.getString("first_name"));
        if(!o.isNull("last_name"))
            u.last_name = VkApi.unescape(o.getString("last_name"));
        if(!o.isNull("sex"))
            u.sex = Integer.parseInt(o.optString("sex"));
        
//        System.out.println(u.first_name+" "+u.sex);
        
        if(!o.isNull("bdate"))
            u.birthdate = o.optString("bdate");
        
        if(!o.isNull("photo"))
            u.photo = o.optString("photo");
        if(!o.isNull("photo_medium"))
            u.photo_medium = o.optString("photo_medium");
        if(!o.isNull("photo_medium_rec"))
            u.photo_medium_rec = o.optString("photo_medium_rec");
        if(!o.isNull("photo_big"))
            u.photo_big = o.optString("photo_big");

        if(!o.isNull("counters")) {
            JSONObject object = o.optJSONObject("counters");
            if (object != null) {
                u.albums_count = object.optInt("albums");
                u.friends_count = object.optInt("friends");
                u.user_photos_count = object.optInt("user_photos");
            }
        }

        return u;
    }
    
    public static User fbParse(JSONObject o) throws JSONException {
        User u = new User();
        u.uid = Long.parseLong(o.getString("id"));
        u.first_name = VkApi.unescape(o.getString("name"));

        return u;
    }
    
    //uid,first_name,last_name,name,gender,pic_3,birthday
    public static User okParse(JSONObject o) throws JSONException {
        User u = new User();
        u.uid = Long.parseLong(o.getString("uid"));
        System.out.println("User "+u.uid);
        
        if(!o.isNull("first_name"))
            u.first_name = VkApi.unescape(o.getString("first_name"));
        System.out.println("first_name "+u.first_name);
      
        if(!o.isNull("last_name"))
            u.last_name = VkApi.unescape(o.getString("last_name"));
        System.out.println("last_name "+u.last_name);
       
        if(!o.isNull("birthday"))
            u.birthdate = o.optString("birthday");
        System.out.println("birthday "+u.birthdate);
       
        if(!o.isNull("pic_3"))
            u.photo_medium = o.optString("pic_3");
        System.out.println("photo_medium "+u.photo_medium);
        
        
        return u;
    }
    

	public static User instaParse(JSONObject o) throws JSONException {
        User u = new User();
        u.uid = Long.parseLong(o.getString("id"));
        u.first_name = o.optString("full_name");
        u.photo_medium = o.optString("profile_picture");

		return u;
	}

    public static ArrayList<User> parseVkUsers(JSONArray array) throws JSONException {
        ArrayList<User> users=new ArrayList<User>();
        //it may be null if no users returned
        //no users may be returned if we request users that are already removed
        if(array==null)
            return users;
        int category_count=array.length();
        for(int i=0; i<category_count; ++i){
            if(array.get(i)==null || ((array.get(i) instanceof JSONObject)==false))
                continue;
            JSONObject o = (JSONObject)array.get(i);
            User u = User.vkParse(o);
            users.add(u);
        }
        return users;
    }
    


	@Override
	public String getName() {
		String name = "";
		if (first_name != null && !first_name.equals(null)) name += first_name+" ";
		if (last_name != null && !last_name.equals(null)) name += last_name;
		return name;
	}

	@Override
	public String getThumbUrl() {
		return photo_medium;
	}

	@Override
	public String getAdditionalInfo() {
		return "";//Long.toString(albums_count) +" "+ Constants.STRING_ALBUMS;
	}

	@Override
	public long getId() {
		return uid;
	}

	public void setFbPhotoSrc(String baseUrl, String acessToken) {
		this.photo_medium = baseUrl+uid+"/picture?width=100&height=100&access_token="+acessToken;
	}

	@Override
	public void setImageView(ImageView itemImageView) {
	}
	

	@Override
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public void setSelected(boolean isSelected) {
		selected = isSelected;
	}

	@Override
	public int getGender() {
		return sex;
	}
	

}
