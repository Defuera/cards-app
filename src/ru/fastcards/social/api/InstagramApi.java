package ru.fastcards.social.api;
//package com.example.social.api;
//
//import java.io.BufferedInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.zip.GZIPInputStream;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.util.Log;
//
//import com.example.social.utils.Utils;
//import com.example.social.utils.WrongResponseCodeException;
//import com.example.theproject.Account;
//
//public class InstagramApi {
//	
//    private final static int MAX_TRIES=3;
//	private static final String TAG = "InstaApi";
//	private static final String BASE_URL = "https://api.instagram.com/v1/";
//    //TODO: it's not faster, even slower on slow devices. Maybe we should add an option to disable it. It's only good for paid internet connection.
//    static boolean enable_compression=true;
//	
//	private String acessToken;
//	
//	public InstagramApi(){
//		acessToken = Account.getAccount().getInstagramToken();
//	}
//
//	
//    public ArrayList<Photo> getPhotos(Long userId) throws MalformedURLException, IOException, JSONException, KException{
////    	Params params = new Params("users/self/media/recent/");
//    	
//    	Params params;
//    	if (userId == 0)params = new Params("users/self/media/recent/");
//    	else
//    		params = new Params("users/"+userId+"/media/recent/");
//    	
//    	params.put("count", "-1");
//        JSONObject root = sendRequest(params);
//        JSONArray array = root.optJSONArray("data");
//        System.out.println("JSONArray array "+array);
//        if (array == null)
//            return new ArrayList<Photo>(); 
//        ArrayList<Photo> photos = parsePhotos(array);
//        return photos;
//    }
//    
//    private ArrayList<Photo> parsePhotos(JSONArray array) throws JSONException {
//        ArrayList<Photo> photos=new ArrayList<Photo>(); 
//        int category_count=array.length(); 
//        for(int i=0; i<category_count; ++i){
//            //in getUserPhotos first element is integer
//            if(array.get(i) instanceof JSONObject == false)
//                continue;
//            JSONObject o = (JSONObject)array.get(i);
//            Photo p = Photo.instaParse(o);
//            photos.add(p);
//        }
//        return photos;
//    }
//
//    
//    //https://api.instagram.com/v1/users/3/follows
//    public ArrayList<User> getFriends() throws MalformedURLException, IOException, JSONException, KException{
//        Params params = new Params("users/3/follows");
//
////    	params.put("count", "-1");
//        JSONObject root = sendRequest(params);
//        ArrayList<User> users=new ArrayList<User>();
//        JSONArray array=root.optJSONArray("data");
//        //if there are no friends "response" will not be array
//        if(array==null)
//            return users;
//        int category_count=array.length();
//        for(int i=0; i<category_count; ++i){
//            JSONObject o = (JSONObject)array.get(i);
//            User u = User.instaParse(o);
//            users.add(u);
//        }
//        return users;
//    }
//    
//
//    private JSONObject sendRequest(Params params) throws IOException, MalformedURLException, JSONException, KException {
//        return sendRequest(params, false);
//    }
//    
//    private JSONObject sendRequest(Params params, boolean is_post) throws IOException, MalformedURLException, JSONException, KException {
//        String url = getSignedUrl(params, is_post);
//        Log.d(TAG, "sendRequest ");
//        String body="";
//        if(is_post)
//            body=params.getParamsString();
//        Log.i(TAG, "url="+url);
//        if(body.length()!=0)
//            Log.i(TAG, "body="+body);
//        String response="";
//        for(int i=1;i<=MAX_TRIES;++i){
//            try{
//                if(i!=1)
//                    Log.i(TAG, "try "+i);
//                response = sendRequestInternal(url, body, is_post);
//                break;
//            }catch(javax.net.ssl.SSLException e){
////            	e.printStackTrace();
//                processNetworkException(i, e);
//            }catch(java.net.SocketException e){
////            	e.printStackTrace();
//                processNetworkException(i, e);
//            }
//        }
////        Log.i(TAG, "response="+response);
//        JSONObject root=new JSONObject(response);
////        checkError(root, url);
//        return root;
//    }
//    
//    private void processNetworkException(int i, IOException ex) throws IOException {
//        ex.printStackTrace();
//        if(i==MAX_TRIES)
//            throw ex;
//    }
//    
//    private String getSignedUrl(Params params, boolean is_post) {
//        params.put("access_token", acessToken);
//        
//        String args = "";
//        if(!is_post)
//            args=params.getParamsString();
//        
//        return BASE_URL+params.method_name+"?"+args;
//    }
//    
//    private String sendRequestInternal(String url, String body, boolean is_post) throws IOException, MalformedURLException, WrongResponseCodeException {
//        HttpURLConnection connection=null;
//        try{
//            connection = (HttpURLConnection)new URL(url).openConnection();
//            connection.setConnectTimeout(30000);
//            connection.setReadTimeout(30000);
//            connection.setUseCaches(false);
//            connection.setDoOutput(is_post);
//            connection.setDoInput(true);
//            connection.setRequestMethod(is_post?"POST":"GET");
//            if(enable_compression)
//                connection.setRequestProperty("Accept-Encoding", "gzip");
//            if(is_post)
//                connection.getOutputStream().write(body.getBytes("UTF-8"));
//            int code=connection.getResponseCode();
//            Log.i(TAG, "code="+code+" from url "+url);
//            //It may happen due to keep-alive problem http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
//            if (code==-1)
//                throw new WrongResponseCodeException("Network error");
//            //Ã?Â¼Ã?Â¾Ã?Â¶Ã?ÂµÃ‘â€š Ã‘ï¿½Ã‘â€šÃ?Â¾Ã?Â¸Ã‘â€š Ã?Â¿Ã‘â‚¬Ã?Â¾Ã?Â²Ã?ÂµÃ‘â‚¬Ã?Â¸Ã‘â€šÃ‘Å’ Ã?Â½Ã?Â° Ã?ÂºÃ?Â¾Ã?Â´ 200
//            //on error can also read error stream from connection.
//            InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
//            String enc=connection.getHeaderField("Content-Encoding");
//            if(enc!=null && enc.equalsIgnoreCase("gzip"))
//                is = new GZIPInputStream(is);
//            String response=Utils.convertStreamToString(is);
//            
//            return response;
//        }
//        finally{
//            if(connection!=null)
//                connection.disconnect();
//        }
//    }
//
//
//	
//}
