package ru.fastcards.social.api;
//package com.example.social.api;
//
//import java.io.Serializable;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.example.utils.GenderNotSupportedException;
//
//import android.graphics.Bitmap;
//import android.widget.ImageView;
//
//public class Photo implements Serializable, SocialObject{
//    private static final long serialVersionUID = 1L;
//
//    public long pid;
//    public long aid;
//    public String owner_id;
//    public String src;
//    public String src_small;//not used for now because in newsfeed it's empty
//    public String src_big;
//    public String src_xbig;
//    public String src_xxbig;
//    public String src_xxxbig;
//    public String phototext;
//    public long created;
////    public Integer like_count;
//    public Boolean user_likes;
//    public Integer comments_count;
//    public Integer tags_count;
//    public Boolean can_comment;
//    public int width;//0 means value is unknown
//    public int height;//0 means value is unknown
//    public String access_key;
//    private boolean selected;
//    private ImageView imageView;
//    private Bitmap bitmap;
//	public String fileName;
//
//    public static Photo vkParse(JSONObject o) throws NumberFormatException, JSONException{
//        Photo p = new Photo();
//        p.pid = o.getLong("pid");
//        p.aid = o.optLong("aid");
//        p.owner_id = o.getString("owner_id");
//        p.src = o.optString("src");
//        p.src_small = o.optString("src_small");
//        p.src_big = o.optString("src_big");
//        p.src_xbig = o.optString("src_xbig");
//        p.src_xxbig = o.optString("src_xxbig");
//        p.src_xxxbig = o.optString("src_xxxbig");
//        p.phototext = VkApi.unescape(o.optString("text"));
//        p.created = o.optLong("created");
//        p.selected = false;
//
//        if (o.has("comments")) {
//            JSONObject jcomments = o.getJSONObject("comments");
//            p.comments_count = jcomments.optInt("count");
//        }
//        if (o.has("tags")) {
//            JSONObject jtags = o.getJSONObject("tags");
//            p.tags_count = jtags.optInt("count");
//        }
//        if (o.has("can_comment"))
//            p.can_comment = o.optInt("can_comment")==1;
//        p.width = o.optInt("width");
//        p.height = o.optInt("height");
//        p.access_key=o.optString("access_key");
//        return p;
//    }
//
//    public static Photo fbParse(JSONObject o) throws NumberFormatException, JSONException{
//        Photo p = new Photo();
//        p.selected = false;     
//        p.pid = (int) Long.parseLong(o.getString("id").replace("-", ""));
//        p.owner_id =  o.getJSONObject("from").getString("id");
//        p.src = o.optString("source");
//        p.src_small = o.optString("picture");
//        p.src_big = o.optString("source");
//        p.created = o.optLong("created_time");  
//        p.width = o.optInt("width");
//        p.height = o.optInt("height");
//        System.out.println(o.getLong("id")+" "+p.pid);
//        return p;
//    }
//    
//	public static Photo instaParse(JSONObject o) throws NumberFormatException, JSONException{
//        Photo p = new Photo();
//        p.selected = false;
//        p.owner_id =  o.getJSONObject("user").getString("id");
//        p.pid = (int) Long.parseLong(o.getString("id").substring(0, 18));               
//        p.src = o.getJSONObject("images").getJSONObject("low_resolution").getString("url");
//        p.src_small = o.getJSONObject("images").getJSONObject("thumbnail").getString("url");
//        p.src_big = o.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
//        p.created = o.optLong("created_time");
//		return p; 
//	}
//    
//    public Photo(){
//    }
//
//    public Photo(Long id, String owner_id, String src, String src_big){
//        this.pid=id;
//        this.owner_id=owner_id;
//        this.src=src;
//        this.src_big=src_big;
//    }
//
//    public static Photo parseCounts(JSONObject o) throws NumberFormatException, JSONException{
//        Photo p = new Photo();
//        JSONArray pid_array = o.optJSONArray("pid");
//        if (pid_array != null && pid_array.length() > 0) {
//            p.pid = pid_array.getLong(0);
//        }
//        JSONArray comments_array = o.optJSONArray("comments");
//        if (comments_array != null && comments_array.length() > 0) {
//            JSONObject jcomments = comments_array.getJSONObject(0);
//            p.comments_count = jcomments.optInt("count");
//        }
//        JSONArray tags_array = o.optJSONArray("tags");
//        if (tags_array != null && tags_array.length() > 0) {
//            JSONObject jtags = tags_array.getJSONObject(0);
//            p.tags_count = jtags.optInt("count");
//        }
//        JSONArray can_comment_array = o.optJSONArray("can_comment");
//        if (can_comment_array != null && can_comment_array.length() > 0) {
//            p.can_comment = can_comment_array.getInt(0)==1;
//        }
//        return p;
//    }
//
//
//	public boolean isSelected() {
//		return selected;
//	}
//
//	public void setSelected(boolean isSelected) {
//		this.selected = isSelected;
//	}
//	public ImageView getImageView() {
//		return imageView;
//	}
//
//
//	public void setImageView(ImageView imageView) {
//		this.imageView = imageView;
//	}
//
//
//	public Bitmap getBitmap() {
//		return bitmap;
//	}
//
//
//	public void setBitmap(Bitmap bitmap) {
//		this.bitmap = bitmap;
//	}
//
//	@Override
//	public String getName() {
//		return null;
//	}
//
//	@Override
//	public String getThumbUrl() {
//		return src;
//	}
//
//	@Override
//	public String getAdditionalInfo() {
//		return "";
//	}
//
//	@Override
//	public long getId() {
////		if 
//		return pid;
//	}
//
//
//	
//	//Create method getMaxPhoto in Photo
//	public String getMaxResolutionPhotoUrl() {
//		if (src_xxxbig != null && !src_xxxbig.equals("")){
//			return src_xxxbig;
//		}
//		if (src_xbig != null && !src_xxbig.equals("")) {
//			return src_xxbig;
//		}
//		if (src_xbig != null && !src_xbig.equals("")){
//			return src_xbig;
//		}
//		if (src_big != null && !src_big.equals("")){
//			return src_big;
//		}
//		if (src_small != null && !src_small.equals("")){
//			return src_small;
//		}		
//		if (src != null && !src.equals("")){
//			return src;
//		}	
//		return null;
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
