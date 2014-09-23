
package ru.fastcards.common;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import ru.fastcards.social.api.User;
import ru.fastcards.social.api.VkApi;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;


public class Recipient implements Comparable<Recipient>, SimpleItem, ISendableItem{
	private static final String TAG = "Recipient";
	
	private String uuid;
	private String name;
	private String nickName;
	private String imageUri;
	
	/**
	 * 0 for girl, 1 for boy
	 */
	private int gender;	
	/**
	 * Defines where user is got from (Constants.ORIGIN_VK, Constants.ORIGIN_CONTACTS)
	 */
	private int origin;
	private long birthday;
	private boolean isSelected;

public Recipient(String id, String name, String modifiedName, String imageUri, int gender, int origin) {
		this.uuid = id;
		this.name = name;
		this.nickName =  modifiedName;
		this.setImageUri(imageUri);
		this.setGender(gender); 
		this.origin = origin;
	}

	/**
	 * Creates new Recipient with automatically generated UUID.
	 */
	public Recipient() {
    	this.uuid = UUID.randomUUID().toString();
	}

	public Recipient(String id) {
		this.uuid = id;
	}
	
	@Override
	public String toString(){
		return name;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return uuid;
	}
	public void setId(String id) {
		this.uuid = id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String modifiedName) {
		this.nickName = modifiedName;
	}
	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getOrigin() {
		return origin;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}	

	public long getBirthday() {
		return birthday;
	}

	public void setBirthday(long birthdate) {
		this.birthday = birthdate;
	}
	
	public boolean hasBirthday() {
		return birthday != 0;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
    public void vkParse(JSONObject o) throws JSONException {
        String first_name = "";
        String last_name = "";
		if(!o.isNull("first_name"))
             first_name = VkApi.unescape(o.getString("first_name"));
        if(!o.isNull("last_name"))
        	last_name = VkApi.unescape(o.getString("last_name"));
        this.name = first_name+" "+last_name;
        
        if(!o.isNull("sex"))
        	this.gender = Integer.parseInt(o.optString("sex"))-1;
//        Log.v("gender", "vkParse "+gender);

        this.imageUri = o.optString("photo_200_orig");
    }
    
    public void fbParse(JSONObject o) throws JSONException {
//        this.fbId = o.getString("id");
        this.name = VkApi.unescape(o.getString("name"));
        if (!o.isNull("birthday")){
        	String date = o.getString("birthday");
//        	Log.d(TAG, "date "+date);
        	if (date.length() == 5)
        		this.birthday = Utils.formatDate(date, "MM/dd");
        	if (date.length() == 10)
        		this.birthday = Utils.formatDate(date, "MM/dd/yyyy");
        }
        this.gender = o.getString("gender").equals("female") ? 0 : 1;
        this.imageUri = o.getJSONObject("picture").getJSONObject("data").getString("url");
//        Log.v(TAG, "fbParse fbId "+o.getString("id")+" name "+name+" sex  "+o.getString("gender")+" imageUri "+imageUri+" birthdate "+birthday);
    }
	/**
	 * Changes recipient gender to opposite and saves it to DB asynchronously.
	 * @param context - context to commumicate with DB;
	 */

	public void changeGender(final Context context) {
		gender = (gender == 0 ? 1 :0); 
		new AsyncTask<Object,Object,Object>(){

			@Override
			protected Object doInBackground(Object... params) {
				DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
				dbHelper.changeRecipient(uuid, gender, null);
				return null;
			}
			
		}.execute();
	}

	public void changeNickName(Context context, String nickname) {
//		Toast.makeText(context, "changeNickName "+nickname, Toast.LENGTH_SHORT).show();
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		setNickName(nickName);
		dbHelper.changeRecipient(this.uuid, -1, nickname);
	}

	@Override
	public int compareTo(Recipient another) {
//		Log.d("compareTo", "compare "+this.name+" "+another.getName()+" "+this.name.compareTo(another.getName()));
		return this.name.compareTo(another.getName());
	}

	@Override
	public boolean isGroup() {
		return false;
	}



}
