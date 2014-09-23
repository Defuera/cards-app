package ru.fastcards.social.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.fastcards.common.Recipient;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import ru.fastcards.utils.WrongResponseCodeException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class VkApi {
	static final String TAG = "VkApi";
	private String access_token;
	private String api_id;

	public static final String BASE_URL = "https://api.vk.com/method/";

	public VkApi(String access_token, String api_id) {
		this.access_token = access_token;
		this.api_id = api_id;
	}

	// TODO: it's not faster, even slower on slow devices. Maybe we should add
	// an option to disable it. It's only good for paid internet connection.
	static boolean enable_compression = true;

	/*** utils methods ***/
	private void checkError(JSONObject root, String url) throws JSONException, KException {
		if (!root.isNull("error")) {
			JSONObject error = root.getJSONObject("error");
			int code = error.getInt("error_code");
			String message = error.getString("error_msg");
			KException e = new KException(code, message, url);
			if (code == 14) {
				e.captcha_img = error.optString("captcha_img");
				e.captcha_sid = error.optString("captcha_sid");
			}
			throw e;
		}
		if (!root.isNull("execute_errors")) {
			JSONArray errors = root.getJSONArray("execute_errors");
			if (errors.length() == 0)
				return;
			// only first error is processed if there are multiple
			JSONObject error = errors.getJSONObject(0);
			int code = error.getInt("error_code");
			String message = error.getString("error_msg");
			KException e = new KException(code, message, url);
			if (code == 14) {
				e.captcha_img = error.optString("captcha_img");
				e.captcha_sid = error.optString("captcha_sid");
			}
			throw e;
		}
	}

	private JSONObject sendRequest(Params params) throws IOException, MalformedURLException, JSONException, KException {
		return sendRequest(params, false);
	}

	private final static int MAX_TRIES = 3;

	private JSONObject sendRequest(Params params, boolean is_post) throws IOException, MalformedURLException, JSONException, KException {
		String url = getSignedUrl(params, is_post);
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
			} catch (javax.net.ssl.SSLException ex) {
				processNetworkException(i, ex);
			} catch (java.net.SocketException ex) {
				processNetworkException(i, ex);
			}
		}
		Log.i(TAG, "response=" + response);
		JSONObject root = new JSONObject(response);
		checkError(root, url);
		return root;
	}

	private void processNetworkException(int i, IOException ex) throws IOException {
		ex.printStackTrace();
		if (i == MAX_TRIES)
			throw ex;
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
			Log.i(TAG, "code=" + code);
			// It may happen due to keep-alive problem
			// http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
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
			return response;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	private String getSignedUrl(Params params, boolean is_post) {
		params.put("access_token", access_token);

		String args = "";
		if (!is_post)
			args = params.getParamsString();

		return BASE_URL + params.method_name + "?" + args;
	}

	public static String unescape(String text) {
		if (text == null)
			return null;
		return text.replace("&amp;", "&").replace("&quot;", "\"").replace("<br>", "\n").replace("&gt;", ">").replace("&lt;", "<").replace("&#39;", "'").replace("<br/>", "\n").replace("&ndash;", "-")
				.replace("&#33;", "!").trim();
	}

	/*** API methods ***/

	<T> String arrayToString(Collection<T> items) {
		if (items == null)
			return null;
		String str_cids = "";
		for (Object item : items) {
			if (str_cids.length() != 0)
				str_cids += ',';
			str_cids += item;
		}
		return str_cids;
	}

	/*** methods for friends ***/
	// http://vk.com/dev/friends.get
	/**
	 * http://vk.com/dev/friends.get
	 * 
	 * @param user_id
	 *            - User ID. By default, current user ID. int (number)
	 * @param fields
	 *            - Profile fields to return. Sample values: uid, first_name, last_name, nickname, sex, bdate (birthdate), city, country, timezone, photo, photo_medium, photo_big, domain, has_mobile,
	 *            rate, contacts, education. List comma-separated strings
	 * @param lid
	 *            ID - of the friend list got with friends.getLists method to receive friends from. This parameter is taken into account only when uid parameter is equal to current user ID.
	 * @param captcha_key
	 * @param captcha_sid
	 * @return ArrayList"<"Recipient">" - friends for specified user.
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 * @throws KException
	 */
	// public ArrayList<Recipient> getFriends(Long user_id, String fields, Integer lid, String captcha_key, String captcha_sid)
	// throws MalformedURLException, IOException, JSONException,
	// KException {
	// Params params = new Params("friends.get");
	// if (fields == null)
	// fields = "first_name,last_name,photo_medium,online,mobile_phone,contacts";
	// params.put("fields", fields);
	// params.put("uid", user_id);
	// params.put("lid", lid);
	//
	//
	// if (lid == null)
	// params.put("order", "hints");
	//
	// addCaptchaParams(captcha_key, captcha_sid, params);
	// JSONObject root = sendRequest(params);
	// ArrayList<Recipient> users = new ArrayList<Recipient>();
	// JSONArray array = root.optJSONArray("response");
	// // if there are no friends "response" will not be array
	// if (array == null)
	// return users;
	// int category_count = array.length();
	// for (int i = 0; i < category_count; ++i) {
	// JSONObject o = (JSONObject) array.get(i);
	// Recipient u = new Recipient();
	// u.vkParse(o);
	// Log.d(TAG, "added user "+u.getVkId()+" "+u.getName()+" phone "+u.getPhoneNumber());
	//
	// users.add(u);
	// }
	// return users;
	// }

	public List<Recipient> importFriendsToDataBase(String user_id, String fields, Context context) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("friends.get");
		if (fields == null)
			fields = "first_name,last_name,mobile_phone,contacts,sex,photo_200_orig,bdate";
		params.put("fields", fields);
		params.put("uid", user_id);
		params.put("order", "hints");

		JSONObject root = sendRequest(params);
		ArrayList<Recipient> users = new ArrayList<Recipient>();
		JSONArray array = root.optJSONArray("response");
		// if there are no friends "response" will not be array
		if (array == null)
			return users;
		// int category_count = array.length();

		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		for (int i = 0; i < array.length(); ++i) {
			JSONObject o = (JSONObject) array.get(i);
			Recipient recipient = new Recipient();
			recipient.vkParse(o);
			String vkId = o.getString("uid");
			if (!dbHelper.isRecipientExists(vkId)) {
				dbHelper.saveComunication(recipient.getId(), Constants.COMUNICATION_TYPE_VK, vkId, true);
				if (!o.isNull("mobile_phone")) {
					String phone = parsePhoneNumber(o.optString("mobile_phone"));
					if (phone != null)
						dbHelper.saveComunication(recipient.getId(), Constants.COMUNICATION_TYPE_PHONE, phone, false);
				}

				String bdayString = o.optString("bdate");
				if (bdayString != null && !bdayString.equals("")) {
					long bday = parseBirthday(bdayString);
//					Log.i(TAG, "saveEvent BIRTHDAY user " + " " + recipient.getName() + "bday " + bday);
					recipient.setBirthday(bday);
					String eventId = UUID.randomUUID().toString();
					dbHelper.saveEvent(eventId, recipient.getName(), recipient.getBirthday(), Constants.EVENT_CATEGORY_ID_BIRTHDAY, Constants.EVENT_REPEAT_FALSE, Constants.EVENT_TYPE_BIRTHDAY);
					dbHelper.saveEventContact(recipient.getId(), eventId, false);

				}

				// Log.d(TAG, "importFriendsToDataBase user " + " " + recipient.getName() + " gender " + recipient.getGender());
				users.add(recipient);

				dbHelper.saveRecipient(recipient);
			}

		}
		return users;
	}

	private long parseBirthday(String bday) {
		try {
			String[] date = bday.split("\\.");
//			Log.v(TAG, "parseBday " + bday + " " + Arrays.toString(date));
			return Utils.formatDate(date[0] + "-" + date[1] + (date.length == 3 ? "-" + date[2] : "-1984"), "dd-MM-yyyy");
		} catch (Exception e) {
			Log.e(TAG, "parseBday " + bday);
			e.printStackTrace();
		}
		return -1;
	}

	private String parsePhoneNumber(String phone) {
		// Log.d("parsePhoneNumber", "before: "+phone);
		phone = phone.replaceAll("[^\\d.]", "");
		if (phone.length() < 11)
			phone = null;
		// Log.d("parsePhoneNumber", "after: "+phone);
		return phone;
	}

	private void addCaptchaParams(String captcha_key, String captcha_sid, Params params) {
		params.put("captcha_sid", captcha_sid);
		params.put("captcha_key", captcha_key);
	}

	// http://vk.com/dev/photos.getUploadServer
	public String photosGetUploadServer(long album_id, Long group_id) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("photos.getUploadServer");
		params.put("aid", album_id);
		params.put("gid", group_id);
		JSONObject root = sendRequest(params);
		JSONObject response = root.getJSONObject("response");
		return response.getString("upload_url");
	}

	// http://vk.com/dev/photos.getWallUploadServer
	public String photosGetWallUploadServer(String user_id, Long group_id) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("photos.getWallUploadServer");
		params.put("uid", user_id);
		params.put("gid", group_id);
		JSONObject root = sendRequest(params);
		JSONObject response = root.getJSONObject("response");
		return response.getString("upload_url");
	}

	// http://vk.com/dev/photos.saveProfilePhoto
	public String[] saveProfilePhoto(String server, String photo, String hash) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("photos.saveProfilePhoto");
		params.put("server", server);
		params.put("photo", photo);
		params.put("hash", hash);
		JSONObject root = sendRequest(params);
		JSONObject response = root.getJSONObject("response");
		String src = response.optString("photo_src");
		String hash1 = response.optString("photo_hash");
		String[] res = new String[] { src, hash1 };
		return res;
	}

	// http://vk.com/dev/wall.addComment
	public long createWallComment(Long owner_id, Long post_id, String text, Long reply_to_cid, Collection<String> attachments, boolean from_group, String captcha_key, String captcha_sid)
			throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("wall.addComment");
		params.put("owner_id", owner_id);
		params.put("post_id", post_id);
		params.put("text", text);
		params.put("reply_to_cid", reply_to_cid);
		params.put("attachments", arrayToString(attachments));
		if (from_group)
			params.put("from_group", "1");
		addCaptchaParams(captcha_key, captcha_sid, params);
		JSONObject root = sendRequest(params, true);
		JSONObject response = root.getJSONObject("response");
		long cid = response.optLong("cid");
		return cid;
	}

	// http://vk.com/dev/wall.editComment
	public boolean editWallComment(long cid, Long owner_id, String text, Collection<String> attachments, String captcha_key, String captcha_sid) throws MalformedURLException, IOException,
			JSONException, KException {
		Params params = new Params("wall.editComment");
		params.put("comment_id", cid);
		params.put("owner_id", owner_id);
		params.put("message", text);
		params.put("attachments", arrayToString(attachments));
		addCaptchaParams(captcha_key, captcha_sid, params);
		JSONObject root = sendRequest(params, true);
		int response = root.optInt("response");
		return response == 1;
	}

	// http://vk.com/dev/wall.post
	public long createWallPost(String owner_id, String message, Collection<String> attachments, String export, boolean only_friends, boolean from_group, boolean signed, String lat, String lon,
			String captcha_key, String captcha_sid) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("wall.post");
		params.put("owner_id", owner_id);
		params.put("attachments", arrayToString(attachments));

		if (message != null)
			params.put("message", message);
		if (export != null && export.length() != 0)
			params.put("services", export);
		if (from_group)
			params.put("from_group", "1");
		if (only_friends)
			params.put("friends_only", "1");
		if (signed)
			params.put("signed", "1");
		addCaptchaParams(captcha_key, captcha_sid, params);
		JSONObject root = sendRequest(params, true);
		JSONObject response = root.getJSONObject("response");
		long post_id = response.optLong("post_id");
		return post_id;
	}

	// http://vk.com/dev/photos.getTags
	public ArrayList<PhotoTag> getPhotoTagsById(Long pid, Long owner_id) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("photos.getTags");
		params.put("owner_id", owner_id);
		params.put("pid", pid);
		JSONObject root = sendRequest(params);
		JSONArray array = root.optJSONArray("response");
		if (array == null)
			return new ArrayList<PhotoTag>();
		ArrayList<PhotoTag> photo_tags = parsePhotoTags(array, pid, owner_id);
		return photo_tags;
	}

	private ArrayList<PhotoTag> parsePhotoTags(JSONArray array, Long pid, Long owner_id) throws JSONException {
		ArrayList<PhotoTag> photo_tags = new ArrayList<PhotoTag>();
		int category_count = array.length();
		for (int i = 0; i < category_count; ++i) {
			// in getUserPhotos first element is integer
			if (array.get(i) instanceof JSONObject == false)
				continue;
			JSONObject o = (JSONObject) array.get(i);
			PhotoTag p = PhotoTag.parse(o);
			photo_tags.add(p);
			if (pid != null)
				p.pid = pid;
			if (owner_id != null)
				p.owner_id = owner_id;
		}
		return photo_tags;
	}

	// http://vk.com/dev/account.getCounters
	public Counters getCounters(String captcha_key, String captcha_sid) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("account.getCounters");
		addCaptchaParams(captcha_key, captcha_sid, params);
		JSONObject root = sendRequest(params);
		JSONObject response = root.optJSONObject("response");
		return Counters.parse(response);
	}

	// http://vk.com/dev/photos.getMessagesUploadServer
	public String photosGetMessagesUploadServer() throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("photos.getMessagesUploadServer");
		JSONObject root = sendRequest(params);
		JSONObject response = root.getJSONObject("response");
		return response.getString("upload_url");
	}

	// http://vk.com/dev/messages.send
	public String sendMessage(String uid, long chat_id, String message, String title, String type, Collection<String> attachments, ArrayList<Long> forward_messages, String lat, String lon,
			String captcha_key, String captcha_sid) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("messages.send");
		if (chat_id <= 0)
			params.put("uid", uid);
		else
			params.put("chat_id", chat_id);
		params.put("message", message);
		params.put("title", title);
		params.put("type", type);
		params.put("attachment", arrayToString(attachments));
		params.put("forward_messages", arrayToString(forward_messages));
		params.put("lat", lat);
		params.put("long", lon);
		addCaptchaParams(captcha_key, captcha_sid, params);
		JSONObject root = sendRequest(params, true);
		Object message_id = root.opt("response");
		if (message_id != null)
			return String.valueOf(message_id);
		return null;
	}

	// http://vk.com/dev/photos.saveWallPhoto
	// public ArrayList<Photo> saveWallPhoto(String server, String photo, String hash, String user_id, Long group_id)
	// throws MalformedURLException, IOException, JSONException,
	// KException {
	// Params params = new Params("photos.saveWallPhoto");
	// params.put("server", server);
	// params.put("photo", photo);
	// params.put("hash", hash);
	// params.put("uid", user_id);
	// params.put("gid", group_id);
	// JSONObject root = sendRequest(params);
	// JSONArray array = root.getJSONArray("response");
	// ArrayList<Photo> photos = parsePhotos(array);
	// return photos;
	// }

	// http://vk.com/dev/photos.saveWallPhoto
	public String saveWallPostcard(String server, String photo, String hash, String user_id, Long group_id) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("photos.saveWallPhoto");
		params.put("server", server);
		params.put("photo", photo);
		params.put("hash", hash);
		params.put("uid", user_id);
		params.put("gid", group_id);
		JSONObject root = sendRequest(params);
		JSONArray array = root.getJSONArray("response");
		String id = parseId(array);
		return id;
	}

	private String parseId(JSONArray array) throws JSONException {
		// ArrayList<Photo> photos = new ArrayList<Photo>();
		int category_count = array.length();
		String id = null;
		for (int i = 0; i < category_count; ++i) {
			// in getUserPhotos first element is integer
			if (array.get(i) instanceof JSONObject == false)
				continue;
			JSONObject o = (JSONObject) array.get(i);
			// Photo p = Photo.vkParse(o);
			// photos.add(p);
			id = o.getString("pid");
		}
		return id;
	}

	//
	// /**
	// * Saves a photo after being successfully uploaded. URL obtained with photos.getMessagesUploadServer method.
	// * @param server
	// * @param photo
	// * @param hash
	// * @return
	// * @throws MalformedURLException
	// * @throws IOException
	// * @throws JSONException
	// * @throws KException
	// */
	// // http://vk.com/dev/photos.saveMessagesPhoto
	// public ArrayList<Photo> saveMessagesPhoto(String server, String photo,
	// String hash) throws MalformedURLException, IOException,
	// JSONException, KException {
	// Params params = new Params("photos.saveMessagesPhoto");
	// params.put("server", server);
	// params.put("photo", photo);
	// params.put("hash", hash);
	// JSONObject root = sendRequest(params);
	// JSONArray array = root.getJSONArray("response");
	// ArrayList<Photo> photos = parsePhotos(array);
	// return photos;
	// }
	//
	// private ArrayList<Photo> parsePhotos(JSONArray array) throws JSONException {
	// ArrayList<Photo> photos = new ArrayList<Photo>();
	// int category_count = array.length();
	// for (int i = 0; i < category_count; ++i) {
	// // in getUserPhotos first element is integer
	// if (array.get(i) instanceof JSONObject == false)
	// continue;
	// JSONObject o = (JSONObject) array.get(i);
	// Photo p = Photo.vkParse(o);
	// photos.add(p);
	// }
	// return photos;
	// }

	/**
	 * Returns an array with the uploaded photo, the returned object contains id, pid, aid, owner_id, src, src_big, src_small, created fields. In case there are high-resolution photos, addresses with
	 * src_xbig and src_xxbig names will also be returned.
	 * 
	 * @param url
	 * @param friendID
	 * @param bitmap
	 * @return
	 */
	// public String sendPostCartToFriendViaMessage(long friendID, Bitmap bitmap) {
	// // Create a new HttpClient and Post Header
	// try {
	//
	// String uploadServer = photosGetMessagesUploadServer();
	//
	// Log.d(TAG, "uploadServer "+ uploadServer);
	//
	// HttpClient client = new DefaultHttpClient();
	// HttpPost httpPost = new HttpPost(uploadServer);
	//
	// MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
	// entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	//
	// // final File file = new File(fileName);
	// // FileBody fb = new FileBody(file);
	//
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// bitmap.compress(CompressFormat.PNG, 100, bos);
	// byte[] data = bos.toByteArray();
	// ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");
	//
	// // ContentBody contentBody = null;
	// entityBuilder.addPart("photo", bab);
	// final HttpEntity entity = entityBuilder.build();
	//
	// httpPost.setEntity(entity);
	// HttpResponse response = client.execute(httpPost);
	//
	//
	//
	// BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	// StringBuilder builder = new StringBuilder();
	// for (String line; (line = reader.readLine()) != null;) {
	// builder.append(line).append("\n");
	// }
	// Log.d(TAG, "photoObject "+ builder.toString());
	// JSONObject photoObject = new JSONObject(builder.toString());
	//
	// ArrayList<Photo> photosList = saveMessagesPhoto(photoObject.get("server").toString(), photoObject.get("photo").toString(), photoObject.get("hash").toString());
	//
	// Log.d(TAG, "photosList "+ photosList.size());
	//
	// Long photoId = photosList.get(0).getId();
	//
	// List<String> attachments = new ArrayList<String>();
	//
	// String att = "photo" + Account.getInstance().getVkontakteUserId() + "_" + photoId;
	//
	// attachments.add(att);
	//
	// return sendMessage(friendID, 0, "ANY MESSAGE", "ANY TITLE", "0", attachments, null, null, null, null, null);
	//
	//
	//
	// // (Long, long, String, String, String, Collection<String>, ArrayList<Long>, String, String, String, String)
	// // (long, null, String, String, String, List<String>, null, null, null, null, null)
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } catch (KException e) {
	// e.printStackTrace(); //
	// }
	// return null;
	// }

	// http://vk.com/dev/photos.getWallUploadServer
	public String photosGetWallUploadServer(Long user_id, Long group_id) throws MalformedURLException, IOException, JSONException, KException {
		Params params = new Params("photos.getWallUploadServer");
		params.put("uid", user_id);
		params.put("gid", group_id);
		JSONObject root = sendRequest(params);
		JSONObject response = root.getJSONObject("response");
		return response.getString("upload_url");
	}

	/**
	 * Upload image to user wall. Method requires httpmime library to create POST with image you can download it here:http://hc.apache.org/downloads.cgi direct link: http://mirrors.besplatnyeprogrammy
	 * .ru/apache//httpcomponents/httpclient/binary /httpcomponents-client-4.2.3-bin.zip Adds a new post on a user wall or community wall. Can also be used to publish suggested or scheduled posts.
	 * 
	 * @param filePath
	 *            absolutely path to image
	 * @param userID
	 * @return
	 * @return (photos.saveWallPhoto) Returns an array with the uploaded photo, the returned object contains id, pid, aid, owner_id, src, src_big, src_small, created fields. In case there are
	 *         high-resolution photos, addresses with src_xbig and src_xxbig names will also be returned.
	 */
	public long postCartToFriendsWall(String friendId, Bitmap bitmap, String message) {
		try {
			String uploadServer = photosGetWallUploadServer(friendId, null);

			Log.d(TAG, "uploadServer " + uploadServer);

			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(uploadServer);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			byte[] data = bos.toByteArray();
			ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");

			entityBuilder.addPart("photo", bab);
			final HttpEntity entity = entityBuilder.build();

			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			JSONObject photoObject = new JSONObject(builder.toString());
			String photoId = saveWallPostcard(photoObject.get("server").toString(), photoObject.get("photo").toString(), photoObject.get("hash").toString(), friendId, null);

			// Long photoId = photosList.get(0).getId();
			List<String> attachments = new ArrayList<String>();
			String att = "photo" + Account.getInstance().getVkontakteUserId() + "_" + photoId;

			attachments.add(att);

			return createWallPost(friendId, message, attachments, null, false, false, false, null, null, null, null);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (KException e) {
			e.printStackTrace(); //
		}
		return 0;

	}
	
	public void sendFriendMessage(String friendId, Bitmap bitmap, String message) {
		try {
			String uploadServer = photosGetWallUploadServer(friendId, null);

			Log.d(TAG, "uploadServer " + uploadServer);

			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(uploadServer);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			byte[] data = bos.toByteArray();
			ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");

			entityBuilder.addPart("photo", bab);
			final HttpEntity entity = entityBuilder.build();

			httpPost.setEntity(entity);
			HttpResponse response = client.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			JSONObject photoObject = new JSONObject(builder.toString());
			String photoId = saveWallPostcard(photoObject.get("server").toString(), photoObject.get("photo").toString(), photoObject.get("hash").toString(), friendId, null);

			// Long photoId = photosList.get(0).getId();
			List<String> attachments = new ArrayList<String>();
			String att = "photo" + Account.getInstance().getVkontakteUserId() + "_" + photoId;

			attachments.add(att);

			sendMessage(friendId, 0, message, null, null, attachments, null, null, null, null, null);
//			(friendId, message, attachments, null, false, false, false, null, null, null, null);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (KException e) {
			e.printStackTrace(); //
		}
//		return 0;

	}

}
