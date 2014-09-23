package ru.fastcards.social.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.fastcards.common.Recipient;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import ru.fastcards.utils.WrongResponseCodeException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnPublishListener;
import com.sromku.simple.fb.entities.Feed;

public class FbApi {

	private String acessToken;

	public FbApi() {
		acessToken = SimpleFacebook.getInstance().getAccessToken();
	}

	private final static int MAX_TRIES = 3;
	private static final String TAG = "FbApi";
	private static final String BASE_URL = "https://graph.facebook.com/";
	// TODO: it's not faster, even slower on slow devices. Maybe we should add an option to disable it. It's only good for paid internet connection.
	static boolean enable_compression = true;

	/**
	 * 
	 * @param userId
	 * @param oid
	 * @param aid
	 * @param need_systemF
	 * @param need_covers
	 * @param photo_sizes
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 * @throws KException
	 */
	// https://graph.facebook.com/me/albums
	// public List<Album> getAlbums(long userId) throws MalformedURLException, IOException, JSONException, KException{
	// Params params;
	// if (userId == 0)params = new Params("me/albums");
	// else
	// params = new Params(userId+"/albums");
	//
	// // Log.d(TAG+" getAlbums", userId+" "+params.getParamsString());
	//
	// JSONObject root = sendRequest(params);
	// List<Album> albums=new ArrayList<Album>();
	// JSONArray array=root.optJSONArray("data");
	// if (array == null)
	// return albums;
	// int category_count=array.length();
	// for (int i=0; i<category_count; ++i) {
	// JSONObject o = (JSONObject)array.get(i);
	// Album a = Album.parseFb(o);
	// a.setFbThumbSrc(BASE_URL, acessToken);
	// if (a.getTitle().equals("DELETED"))
	// continue;
	// albums.add(a);
	//
	//
	// }
	// return albums;
	// }

	/**
	 * Ã�â€�Ã�Â»Ã‘ï¿½ Ã�Â¿Ã�Â¾Ã�Â»Ã‘Æ’Ã‘â€¡Ã�ÂµÃ�Â½Ã�Â¸Ã‘ï¿½ Ã‘ï¿½Ã�Â¿Ã�Â¸Ã‘ï¿½Ã�ÂºÃ�Â°
	 * Ã�Â´Ã‘â‚¬Ã‘Æ’Ã�Â·Ã�ÂµÃ�Â¹. Ã�â€™Ã�Â¾Ã�Â·Ã�Â²Ã‘â‚¬Ã�Â°Ã‘â€°Ã�Â°Ã�ÂµÃ‘â€š json.
	 * Ã�ï¿½Ã�Â²Ã�Â°Ã‘â€šÃ�Â°Ã‘â‚¬Ã�ÂºÃ�Â° Ã�Â´Ã‘â‚¬Ã‘Æ’Ã�Â³Ã�Â°
	 * Ã�Â¿Ã�Â¾Ã�Â»Ã‘Æ’Ã‘â€¡Ã�Â°Ã�ÂµÃ‘â€šÃ‘ï¿½Ã‘ï¿½ Ã�Â·Ã�Â°Ã�Â¿Ã‘â‚¬Ã�Â¾Ã‘ï¿½Ã�Â¾Ã�Â¼
	 * Ã¢â€žâ€“2, Ã�Â³Ã�Â´Ã�Âµ id Ã�Â¾Ã�Â±Ã‘Å Ã�ÂµÃ�ÂºÃ‘â€šÃ�Â° Ã¢â‚¬â€œ id Ã�Â´Ã‘â‚¬Ã‘Æ’Ã�Â³Ã�Â°
	 * Ã�Â¸Ã�Â· Ã�Â¿Ã�Â¾Ã�Â»Ã‘Æ’Ã‘â€¡Ã�ÂµÃ�Â½Ã�Â½Ã�Â¾Ã�Â³Ã�Â¾ Ã‘ï¿½
	 * Ã‘ï¿½Ã�ÂµÃ‘â‚¬Ã�Â²Ã�ÂµÃ‘â‚¬Ã�Â° json.
	 * 
	 * @param user_id
	 * @param fields
	 * @param lid
	 * @param captcha_key
	 * @param captcha_sid
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 * @throws KException
	 */

	// https://graph.facebook.com/me/friends
	public ArrayList<User> getFriends() throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("me/friends");
		JSONObject root = sendRequest(params);
		ArrayList<User> users = new ArrayList<User>();
		JSONArray array = root.optJSONArray("data");
		// if there are no friends "response" will not be array
		if (array == null)
			return users;
		int category_count = array.length();
		for (int i = 0; i < category_count; ++i) {
			JSONObject o = (JSONObject) array.get(i);
			User u = User.fbParse(o);
			u.setFbPhotoSrc(BASE_URL, acessToken);
			users.add(u);
		}
		return users;
	}

	public List<Recipient> importFriendsToDataBase(Context context) throws MalformedURLException, IOException, JSONException, KException {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		
//		List<Recipient> oldRecList = dbHelper.getRecipientsListByComunicationType(Constants.COMUNICATION_TYPE_FB);
		
		Params params = new Params("me/friends");
		String fields = "id,name,gender,picture.type(square),birthday,email";
		params.put("fields", fields);

		JSONObject root = sendRequest(params);

		List<Recipient> recipients = new ArrayList<Recipient>();
		JSONArray array = root.optJSONArray("data");

		if (array == null)
			return recipients;
		int category_count = array.length();

		for (int i = 0; i < category_count; ++i) {
			JSONObject o = (JSONObject) array.get(i);
			Recipient recipient = new Recipient();
			recipient.fbParse(o);

			recipients.add(recipient);

//			if (recipient.getName().equals(object))
			dbHelper.saveRecipient(recipient);
			dbHelper.saveComunication(recipient.getId(), Constants.COMUNICATION_TYPE_FB, o.getString("id"), true);

			if (recipient.hasBirthday()) {
				Log.i(TAG, "saveEvent BIRTHDAY user " + " " + recipient.getName() + "bday " + recipient.getBirthday());
				String eventId = UUID.randomUUID().toString();
				dbHelper.saveEvent(eventId, recipient.getName(), recipient.getBirthday(), 
						Constants.EVENT_CATEGORY_ID_BIRTHDAY, Constants.EVENT_REPEAT_FALSE, Constants.EVENT_TYPE_BIRTHDAY);
				dbHelper.saveEventContact(recipient.getId(), eventId, false);
			}

			Log.v(TAG, "import Fb friend " + recipient.getName() + " bday " + recipient.getBirthday() + " sex " + recipient.getGender());
		}
		return recipients;

	}

	private JSONObject sendRequest(Params params) throws IOException, MalformedURLException, JSONException, KException {
		return sendRequest(params, false);
	}

	private JSONObject sendRequest(Params params, boolean is_post) throws IOException, MalformedURLException, JSONException, KException {
		String url = getSignedUrl(params, is_post);
		Log.d(TAG, "sendRequest ");
		String body = "";
		if (is_post)
			body = params.getParamsString();
		Log.i(TAG, "url=" + url);
		if (body.length() != 0)
			Log.i(TAG, "body=" + body);
		String response = "";
		for (int i = 1; i <= MAX_TRIES; ++i) {
			try {
				if (i != 1)
					Log.i(TAG, "try " + i);
				response = sendRequestInternal(url, body, is_post);

				break;
			} catch (javax.net.ssl.SSLException e) {
				// e.printStackTrace();
				processNetworkException(i, e);
			} catch (java.net.SocketException e) {
				// e.printStackTrace();
				processNetworkException(i, e);
			}
		}
		Log.i(TAG, "response=" + response);
		JSONObject root = new JSONObject(response);
		// checkError(root, url);
		return root;
	}

	private void processNetworkException(int i, IOException ex) throws IOException {
		ex.printStackTrace();
		if (i == MAX_TRIES)
			throw ex;
	}

	private String getSignedUrl(Params params, boolean is_post) {
		params.put("access_token", acessToken);

		String args = "";
		if (!is_post)
			args = params.getParamsString();

		return BASE_URL + params.method_name + "?" + args;
	}

	private String sendRequestInternal(String url, String body, boolean is_post) throws IOException, MalformedURLException, WrongResponseCodeException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setUseCaches(false);
			connection.setDoOutput(is_post);
			connection.setDoInput(true);
			connection.setRequestMethod(is_post ? "POST" : "GET");
			if (enable_compression)
				connection.setRequestProperty("Accept-Encoding", "gzip");
			if (is_post)
				connection.getOutputStream().write(body.getBytes("UTF-8"));
			int code = connection.getResponseCode();
			Log.i(TAG, "code=" + code + " from url " + url);
			// It may happen due to keep-alive problem http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
			if (code == -1)
				throw new WrongResponseCodeException("Network error");
			// Ã�Â¼Ã�Â¾Ã�Â¶Ã�ÂµÃ‘â€š Ã‘ï¿½Ã‘â€šÃ�Â¾Ã�Â¸Ã‘â€š
			// Ã�Â¿Ã‘â‚¬Ã�Â¾Ã�Â²Ã�ÂµÃ‘â‚¬Ã�Â¸Ã‘â€šÃ‘Å’ Ã�Â½Ã�Â° Ã�ÂºÃ�Â¾Ã�Â´ 200
			// on error can also read error stream from connection.
			InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
			String enc = connection.getHeaderField("Content-Encoding");
			if (enc != null && enc.equalsIgnoreCase("gzip"))
				is = new GZIPInputStream(is);
			String response = Utils.convertStreamToString(is);

			Log.v(TAG, "response= " + response);
			return response;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	public void postCartToFriendsWall(long friendId, Bitmap decodeResource) {
		// build feed
		Feed feed = new Feed.Builder()
				.setMessage("Clone it out...")
				.setName("Simple Facebook for Android")
				.setCaption("Code less, do the same.")
				.setDescription(
						"The Simple Facebook library project makes the life much easier by coding less code for being able to login, publish feeds and open graph stories, invite friends and more.")
				.setPicture("https://raw.github.com/sromku/android-simple-facebook/master/Refs/android_facebook_sdk_logo.png").setLink("https://github.com/sromku/android-simple-facebook").build();
		// publish the feed
		SimpleFacebook.getInstance().publish(feed, onPublishListener);
	}

	// create publish listener
	private OnPublishListener onPublishListener = new SimpleFacebook.OnPublishListener() {

		@Override
		public void onFail(String reason) {
			// insure that you are logged in before publishing
			Log.w(TAG, reason);
		}

		@Override
		public void onException(Throwable throwable) {
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			// show progress bar or something to the user while publishing
			Log.i(TAG, "In progress");
		}

		@Override
		public void onComplete(String postId) {
			Log.i(TAG, "Published successfully. The new post id = " + postId);
		}
	};

	public void tagPerson(String photoId, String friendId) {
		Params params = new Params(photoId + "/tags");
		params.put("to", friendId);

		try {
			sendRequest(params, true);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
