package ru.fastcards.social.api;
//package com.example.social.api;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.example.utils.GenderNotSupportedException;
//
////import com.example.theproject.Constants;
//
//import android.util.Log;
//import android.widget.ImageView;
//
//public class Album implements SocialObject{
//
//    private static final String TAG = "Album";
//	private long albumId;
//    private long thumb_id;
//    private long owner_id;
//    private String title;
//    private long size;
//    private String thumb_src;
//	private boolean selected;
//
//    public static Album parseVk(JSONObject o) throws JSONException {
//        Album a = new Album();
//        a.setTitle(VkApi.unescape(o.optString("title")));
//        a.albumId = Long.parseLong(o.getString("aid"));
//        a.setOwnerId(Long.parseLong(o.getString("owner_id")));
//        String description = o.optString("description");
//        String thumb_id = o.optString("thumb_id");
//        if (thumb_id != null && !thumb_id.equals("") && !thumb_id.equals("null"))
//            a.thumb_id = Long.parseLong(thumb_id);
//        a.size = o.optLong("size");
//        a.thumb_src = o.optString("thumb_src");
//        
//         return a;
//    }
//    
//    public static Album parseFb(JSONObject o) throws JSONException {
//        Album a = new Album();
//        a.setTitle(VkApi.unescape(o.optString("name")));
//        a.albumId = Long.parseLong(o.getString("id"));
//        a.setOwnerId(o.getJSONObject("from").getLong("id"));
//        String description = o.optString("description");
//        String thumb_id = o.optString("cover_photo");
//        if (thumb_id != null && !thumb_id.equals("") && !thumb_id.equals("null"))
//            a.thumb_id = Long.parseLong(thumb_id);
//        a.size = o.optLong("count");
//       
//        
////        Log.d(TAG,"Album "+a.getName()+" size "+a.size+" id "+a.albumId);
//        
//        return a;
//    }
//
//	@Override
//	public String getName() {
//		return getTitle();
//	}
//
//	@Override
//	public String getThumbUrl() {
//		return thumb_src;
//	}
//
//	@Override
//	public String getAdditionalInfo() {
//		return "";//Long.toString(size) + " " +Constants.STRING_PHOTOS;
//	}
//
//	@Override
//	public long getId() {
//		return albumId;
//	}
//
//	public void setFbThumbSrc(String baseUrl, String acessToken) {
//		this.thumb_src = baseUrl+thumb_id+"/picture?width=100&height=100&access_token="+acessToken;
//	}
//
//	@Override
//	public void setImageView(ImageView itemImageView) {
//		
//	}
//
//	public long getOwnerId() {
//		return owner_id;
//	}
//
//	public void setOwnerId(long owner_id) {
//		this.owner_id = owner_id;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	@Override
//	public boolean isSelected() {
//		return selected;
//	}
//
//	@Override
//	public void setSelected(boolean isSelected) {	
//		selected = isSelected;
//	}
//
//	@Override
//	public int getGender() {
//		try {
//			throw new GenderNotSupportedException();
//		} catch (GenderNotSupportedException e) {
//			e.printStackTrace();
//		}
//		return 0;
//	}
//	
//	
//}
