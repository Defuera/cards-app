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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.fastcards.R;
import ru.fastcards.common.Article;
import ru.fastcards.common.Banner;
import ru.fastcards.common.Category;
import ru.fastcards.common.CategoryGroup;
import ru.fastcards.common.MoneyPack;
import ru.fastcards.common.Offer;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.Theme;
import ru.fastcards.common.ThemesCategory;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import ru.fastcards.utils.WrongResponseCodeException;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Denis V
 * @since 20.11.2013 Add field description to UpdateTextPack Add uodateAppeals method
 * 
 */
public class Api {
	private static final String TAG = "Api";
	private static final int MAX_TRIES = 3;
	private static Api instanse;
	private String BASE_URL = "http://5.39.221.121/api/";
	private Context context;
	private Account account;
	private DataBaseHelper dbHelper;

	private Api(Context context) {
		if (context == null){
			Log.e(TAG, "create api with context "+context);
		}
		this.context = context;
		account = Account.getInstance();
		dbHelper = DataBaseHelper.getInstance(context);		
	}
	
	public static Api getInstanse(Context context){
		if (context == null)
			return null;
		if (instanse == null){
			instanse = new Api(context);
		}
		
		return instanse;
	}

	/**
	 * Updates list of category groups in DB
	 * 
	 * @return List with string array. Each item is an array of 2 strings. string[0] - for ID, string[1] for categoryGroup name.
	 * @throws IOException
	 * @throws JSONException
	 */
	// /GetGroups.php
	public void updateCategoryGroups() throws IOException, JSONException {
		Params params = new Params("/GetGroups.php");
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("GroupsOfCategories");

		List<CategoryGroup> categories = new ArrayList<CategoryGroup>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);
			dbHelper.saveCategoryGroup(o.getString("ID"), o.getString("Name"), null);

			Log.d(TAG, "updateCategoryGroups " + o.getString("Name"));
			// updateCategories(o.getString("ID"));
		}
	}

	// /GetGroups2.php
	public void updateCategoryGroups2() throws IOException, JSONException {
		Params params = new Params("/GetGroups2.php");
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("GroupsOfCategories");

		List<CategoryGroup> categories = new ArrayList<CategoryGroup>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);
			dbHelper.saveCategoryGroup(o.getString("ID"), o.getString("Name"), o.getString("CoverImage"));

			Log.d(TAG, "updateCategoryGroups " + o.getString("Name") + " image " + o.getString("CoverImage"));
			// updateCategories(o.getString("ID"));
		}
	}

	/**
	 * Get's the list of category groups
	 * 
	 * @return List with string array. Each item is an array of 2 strings. string[0] - for ID, string[1] for categoryGroup name.
	 * @throws IOException
	 * @throws JSONException
	 */
	// /GetGroups.php
	public List<CategoryGroup> getCategoryGroups() throws IOException, JSONException {
		Params params = new Params("/GetGroups.php");
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("GroupsOfCategories");

		List<CategoryGroup> categories = new ArrayList<CategoryGroup>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);
			// dbHelper.saveCategoryGroup(o.getString("ID"), o.getString("Name"));

			categories.add(new CategoryGroup(o.getString("ID"), o.getString("Name"), null));
			Log.d(TAG, "updateCategoryGroups " + o.getString("Name"));
			// updateCategories(o.getString("ID"));
		}
		return categories;
	}
	
	// /GetGroups2.php
	public List<CategoryGroup> getCategoryGroups2() throws IOException, JSONException {
		Params params = new Params("/GetGroups2.php");
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("GroupsOfCategories");

		List<CategoryGroup> categories = new ArrayList<CategoryGroup>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);
			// dbHelper.saveCategoryGroup(o.getString("ID"), o.getString("Name"));

			categories.add(new CategoryGroup(o.getString("ID"), o.getString("Name"),  o.getString("CoverImage")));
			Log.d(TAG, "updateCategoryGroups " + o.getString("Name"));
			// updateCategories(o.getString("ID"));
		}
		return categories;
	}

	/**
	 * Updates Table_Themes in DB.
	 * 
	 * @param categoryId
	 * @throws IOException
	 * @throws JSONException
	 */
	// /GetThemes.php
	public void updateThemes(String categoryId) throws IOException, JSONException {

		Log.d(TAG, "GetThemes ");
		Params params = new Params("/GetThemes.php");
		params.put("Loc", account.getDeviceLanguage());
		params.put("User", account.getUserEmail(context));
		params.put("CategoryID", categoryId);
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Themes");

		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			Log.d(TAG, "updateThemes " + o.getString("ID"));

			Theme theme = parseTheme(o, categoryId);

			dbHelper.saveTheme(theme);
		}
	}

	/**
	 * Returns List<ThemesCategory>
	 * 
	 * @param categoryId
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetThemesCategories.php
	public List<ThemesCategory> getThemesCategories(String group2Id) throws IOException, JSONException {

		Log.d(TAG, "GetThemesCategories ");
		Params params = new Params("/GetThemesCategories.php");
		params.put("Loc", account.getDeviceLanguage());
		params.put("User", account.getUserEmail(context));
		params.put("GroupID", group2Id);
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Categories");

		List<ThemesCategory> themeCatList = new ArrayList<ThemesCategory>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			Category category = new Category(o.getString("ID"), o.getString("Name"), null);
			Log.v(TAG, "category " + category.getName());

			JSONArray themesArray = o.getJSONArray("Themes");
			List<Theme> themesList = new ArrayList<Theme>();
			int length=themesArray.length();
			for (int j = 0; j < themesArray.length(); j++) {
				JSONObject themeObject = themesArray.getJSONObject(j);
				Theme theme = parseTheme(themeObject, null);
				Log.w(TAG, "theme " + theme.getName());
				themesList.add(theme);
			}
			themeCatList.add(new ThemesCategory(category, themesList));
		}

		return themeCatList;
	}

	/**
	 * Loads from server and returns list of themes for specified categoryID;
	 * 
	 * @param categoryId
	 *            - to load themes
	 * @return List<Theme>
	 * @throws IOException
	 * @throws JSONException
	 */
	// /GetThemes.php
	public List<Theme> getThemes(String categoryId) throws IOException, JSONException {

		Log.d(TAG, "GetThemes ");
		Params params = new Params("/GetThemes.php");
		params.put("Loc", account.getDeviceLanguage());
		params.put("User", account.getUserEmail(context));
		params.put("CategoryID", categoryId);
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Themes");

		List<Theme> themesList = new ArrayList<Theme>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			Log.d(TAG, "updateThemes " + o.getString("ID"));

			Theme theme = parseTheme(o, categoryId);
			themesList.add(theme);
		}
		return themesList;
	}

	private Theme parseTheme(JSONObject o, String categoryId) throws JSONException {
		String id = o.getString("ID");
		String name = o.getString("Name");
		String purchaseID = o.getString("PurchaseID");
		String author = o.getString("Author");
		double price = o.getDouble("Price");
		boolean bought = o.getInt("Bought") == 1;
		String coverImage = o.getString("CoverImage");
		String eCardImage = o.getString("ECardImage");
		String squareImage = o.getString("SquareImage");
		String postCardFrontImage = o.getString("PostCardFrontImage");
		String postCardBackImage = o.getString("PostCardBackImage");
		int eTextTop = o.getInt("ETextTop");
		int eTextLeft = o.getInt("ETextLeft");
		int pTextTop = o.getInt("PTextTop");
		int pTextLeft = o.getInt("PTextLeft");
		int textColorRed = o.getInt("TextColorRed");
		int textColorGreen = o.getInt("TextColorGreen");
		int textColorBlue = o.getInt("TextColorBlue");

		String eCardThumb = o.getString("ECardThumb");
		String squareThumb = o.getString("SquareThumb");
		String postCardFrontThumb = o.getString("PostCardFrontThumb");
		String postCardBackThumb = o.getString("PostCardBackThumb");
		return new Theme(id, name, categoryId, purchaseID, author, price, bought, coverImage, eCardImage, eCardThumb, squareImage, squareThumb, postCardFrontImage, postCardFrontThumb,
				postCardBackImage, postCardBackThumb, eTextTop, eTextLeft, pTextTop, pTextLeft, textColorRed, textColorGreen, textColorBlue);
	}

	/**
	 * Returns list of categories for specified group
	 * 
	 * @param groupID
	 *            - id of the category group to return categories from
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetCategories
	public void updateCategories(String groupID) throws IOException, JSONException {
		Params params = new Params("/GetCategories.php");
		params.put("GroupID", groupID);
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Categories");

		// List<Category> categories = new ArrayList<Category>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			String id = o.getString("ID");
			String name = o.getString("Name");

			dbHelper.saveCategory(id, name, groupID);

		}
	}

	/**
	 * Returns list of categories for specified group
	 * 
	 * @param groupID
	 *            - id of the category group to return categories from
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetCategories
	public List<Category> getCategories(String groupID) throws IOException, JSONException {
		Params params = new Params("/GetCategories.php");
		params.put("GroupID", groupID);
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Categories");

		List<Category> categories = new ArrayList<Category>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			Category category = new Category(o.getString("ID"), o.getString("Name"), null);
			// category.setId(o.getString("ID"));
			// category.setName(o.getString("Name"));
			categories.add(category);
		}

		return categories;
	}

	// GetCategories
	private List<String> getCategoriesIds(String groupID) throws IOException, JSONException {
		Params params = new Params("/GetCategories.php");
		params.put("GroupID", groupID);
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Categories");

		List<String> categoryIds = new ArrayList<String>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			categoryIds.add(o.getString("ID"));
		}

		return categoryIds;
	}

	private JSONObject sendRequest(Params params) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		String url = getSignedUrl(params);
		String body = "";
		// if (is_post)
		body = params.getParamsString();
		Log.i(TAG, "url=" + url);
		if (body.length() != 0)
			Log.i(TAG, "body=" + body);
		String response = "";
		for (int i = 1; i <= MAX_TRIES; ++i) {
			try {
				if (i != 1)
					Log.i(TAG, "try " + i);
				response = sendRequestInternal(url, body);
				break;
			} catch (javax.net.ssl.SSLException ex) {
				processNetworkException(i, ex);
			} catch (java.net.SocketException ex) {
				processNetworkException(i, ex);
			}
		}
		Log.i(TAG, "response=" + response);
		JSONObject root = new JSONObject(response);
		return root;
	}

	private void processNetworkException(int i, IOException ex) throws IOException {
		ex.printStackTrace();
		if (i == MAX_TRIES)
			throw ex;
	}

	private String getSignedUrl(Params params) {
		// params.put("access_token", access_token);
		String args = params.getParamsString();
		return BASE_URL + params.method_name + "?" + args;
	}

	/**
	 * Ð Ñ›Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Â°Ð Ð…Ð Ñ‘Ð Âµ: Ð â€™Ð Ñ•Ð Â·Ð Ð†Ð¡Ð‚Ð Â°Ð¡â€°Ð Â°Ð ÂµÐ¡â€š ID Ð Ñ—Ð Ñ•Ð Â»Ð¡ÐŠÐ Â·Ð Ñ•Ð Ð†Ð Â°Ð¡â€šÐ ÂµÐ Â»Ð¿Ñ—Ð…?, Ð Ñ‘Ð Ñ˜Ð¿Ñ—Ð…? Ð ÂµÐ Ñ–Ð Ñ•
	 * Ð Â°Ð Ñ”Ð Ñ”Ð Â°Ð¡Ñ“Ð Ð…Ð¡â€šÐ Â° Ð Ñ‘ Ð¿Ñ—Ð…?Ð¡Ñ“Ð Ñ˜Ð Ñ˜Ð¡Ñ“ Ð Ð† Ð ÂµÐ Ñ–Ð Ñ• Ð Ñ”Ð Ñ•Ð¡â‚¬Ð ÂµÐ Â»Ð¡ÐŠÐ Ñ”Ð Âµ, Ð Ð† Ð Ð…Ð Â°Ð¡â‚¬Ð ÂµÐ Ñ˜ Ð Ñ—Ð¡Ð‚Ð Ñ‘Ð Â»Ð Ñ•Ð Â¶Ð ÂµÐ Ð…Ð Ñ‘Ð Ñ‘.
	 * Ð â€¢Ð¿Ñ—Ð…?Ð Â»Ð Ñ‘ Ð Ñ—Ð Ñ•Ð Â»Ð¡ÐŠÐ Â·Ð Ñ•Ð Ð†Ð Â°Ð¡â€šÐ ÂµÐ Â»Ð¿Ñ—Ð…? Ð¿Ñ—Ð…? Ð Â·Ð Â°Ð Ò‘Ð Â°Ð Ð…Ð Ð…Ð¡â€¹Ð Ñ˜ Ð Â°Ð Ñ”Ð Ñ”Ð Â°Ð¡Ñ“Ð Ð…Ð¡â€šÐ Ñ•Ð Ñ˜ Ð Ð†
	 * Ð¿Ñ—Ð…?Ð Ñ‘Ð¿Ñ—Ð…?Ð¡â€šÐ ÂµÐ Ñ˜Ð Âµ Ð Ð…Ð ÂµÐ¡â€š Ð²Ð‚â€œ Ð¿Ñ—Ð…?Ð Ñ•Ð Â·Ð Ò‘Ð Â°Ð ÂµÐ¡â€šÐ¿Ñ—Ð…?Ð¿Ñ—Ð…? Ð Ð…Ð Ñ•Ð Ð†Ð¡â€¹Ð â„– Ð Ñ‘ Ð ÂµÐ Ñ˜Ð¡Ñ“
	 * Ð Ð…Ð Â°Ð¡â€¡Ð Ñ‘Ð¿Ñ—Ð…?Ð Â»Ð¿Ñ—Ð…?Ð¡Ð‹Ð¡â€šÐ¿Ñ—Ð…?Ð¿Ñ—Ð…? Ð¿Ñ—Ð…?Ð¡â€šÐ Â°Ð¡Ð‚Ð¡â€šÐ Ñ•Ð Ð†Ð¡â€¹Ð Âµ Ð Ò‘Ð ÂµÐ Ð…Ð¡ÐŠÐ Ñ–Ð Ñ‘. Ð ÑŸÐ Â°Ð¡Ð‚Ð Â°Ð Ñ˜Ð ÂµÐ¡â€šÐ¡Ð‚Ð¡â€¹: User Ð²Ð‚â€œ
	 * Ð¿Ñ—Ð…?Ð¡â€šÐ¡Ð‚Ð Ñ•Ð Ñ”Ð Â°, Ð¿Ñ—Ð…?Ð Ñ•Ð Ñ•Ð¡â€šÐ Ð†Ð ÂµÐ¡â€šÐ¿Ñ—Ð…?Ð¡â€šÐ Ð†Ð¡Ñ“Ð ÂµÐ¡â€š Ð Â°Ð Ò‘Ð¡Ð‚Ð ÂµÐ¿Ñ—Ð…?Ð¡Ñ“ Ð¿Ñ—Ð…?Ð Â»Ð ÂµÐ Ñ”Ð¡â€šÐ¡Ð‚Ð Ñ•Ð Ð…Ð Ð…Ð Ñ•Ð â„–
	 * Ð Ñ—Ð Ñ•Ð¡â€¡Ð¡â€šÐ¡â€¹ apple/google Ð Â°Ð Ñ”Ð Ñ”Ð Â°Ð¡Ñ“Ð Ð…Ð¡â€šÐ Â°.
	 * 
	 * @param groupID
	 * @throws IOException
	 * @throws JSONException
	 */
	// Login.php
	public void login() throws IOException, JSONException {
		Params params = new Params("/Login.php");
		params.put("User", account.getUserEmail(context));

		JSONObject root = sendRequest(params);
		JSONObject response = root.getJSONObject("UserInfo");

		System.out.println("UserInfo " + response);

		Account account = Account.getInstance();
		account.setFastcardsUserId(response.getString("ID"));
		account.setWealth(context, (float) response.getDouble("Wealth"));

		account.save(context);

	}

	private String sendRequestInternal(String url, String body) throws IOException, MalformedURLException, WrongResponseCodeException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setUseCaches(false);
			connection.setDoOutput(false);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");

			int code = connection.getResponseCode();
			Log.i(TAG, "code=" + code);
			if (code == -1)
				throw new WrongResponseCodeException("Network error");
			// on error can also read error stream from connection.
			InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
			String response = Utils.convertStreamToString(is);
			return response;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	/**
	 * Ð Ñ›Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Â°Ð Ð…Ð Ñ‘Ð Âµ: Ð ÑŸÐ Ñ•Ð Â»Ð¡Ñ“Ð¡â€¡Ð Â°Ð ÂµÐ¡â€š Ð Ñ—Ð ÂµÐ¡Ð‚Ð ÂµÐ¡â€¡Ð ÂµÐ Ð…Ð¡ÐŠ Ð Ò‘Ð Â°Ð¡â€š, Ð Ð…Ð Â°Ð Â·Ð Ð†Ð Â°Ð Ð…Ð Ñ‘Ð¿Ñ—Ð…?
	 * Ð Ñ—Ð¡Ð‚Ð Â°Ð Â·Ð Ò‘Ð Ð…Ð Ñ‘Ð Ñ”Ð Ñ•Ð Ð† Ð Ñ‘ Ð¿Ñ—Ð…?Ð Ð†Ð¿Ñ—Ð…?Ð Â·Ð Â°Ð Ð…Ð Ð…Ð¡â€¹Ð Âµ Ð¿Ñ—Ð…? Ð Ð…Ð Ñ‘Ð Ñ˜Ð Ñ‘ Ð Ñ”Ð Â°Ð¡â€šÐ ÂµÐ Ñ–Ð Ñ•Ð¡Ð‚Ð Ñ‘Ð Ñ‘.
	 * 
	 * @throws JSONException
	 * @throws IOException
	 * @throws WrongResponseCodeException
	 * @throws MalformedURLException
	 */
	// /GetCalendar.php
	public void updateCalendar() throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/GetCalendar.php");
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Holidays");

		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			long date = Utils.formatDate(o.getString("Date"), "yyyy-MM-dd");
			String name = o.getString("Name");
			String categoryId = o.getString("CategoryID");

			String eventId = UUID.randomUUID().toString();
			dbHelper.saveEvent(eventId, name, date, categoryId, 0, Constants.EVENT_TYPE_COMMON_HOLIDAYS);
			Log.i(TAG, "updateCalendar name" + name + " categoryId " + categoryId + " date " + o.getString("Date"));
		}

	}

	/**
	 * 
	 * @param dataType
	 * @return lastmodified in milliseconds or -1 if dataType is not recognized
	 * @throws MalformedURLException
	 * @throws WrongResponseCodeException
	 * @throws IOException
	 * @throws JSONException
	 */
	public long getVersions(String dataType) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/GetVersion.php");
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Versions");

		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			String data = o.getString("Data");

			if (dataType.equals(data)) {
				long lastModified = Utils.formatDate(o.getString("LastModified"), "yyyy-MM-dd hh:mm:ss");
				Log.d(TAG, "getVersions " + dataType + " lastModified " + lastModified);
				return lastModified;
			}
		}

		return -1;
	}

	public void updateData(String data) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Log.d(TAG, "updateData " + data);
		if ("Calendar".equals(data)) {
			dbHelper.clearCalendarTable();
			updateCalendar();
			// break;
		}
		if ("Groups".equals(data)) {
			dbHelper.clearCategoryGroupsTable();
			updateCategoryGroups();
			// break;
		}
		if ("Appeals".equals(data)) {
			dbHelper.clearAppealsTable();
			updateAppeals();
			// break;
		}
		if ("Categories".equals(data)) {
			dbHelper.clearCategoriesTable();
			for (String categoryId : dbHelper.getCategoryGroupsIds())
				updateCategories(categoryId);
		}
		if ("Themes".equals(data)) {
			dbHelper.clearThemesTable();
			for (String categoryId : getCategoryIds())
				updateThemes(categoryId);
		}
		if ("Texts".equals(data)) {
			dbHelper.clearTextsTable();
			for (String categoryId : getCategoryIds())
				updateTextPacksByCategory(categoryId);
		}
	}

	private List<String> getCategoryIds() {
		List<String> categoryIds = dbHelper.getCategoryIds();
		if (categoryIds.isEmpty())
			for (String groupId : dbHelper.getCategoryGroupsIds())
				try {
					categoryIds.addAll(getCategoriesIds(groupId));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
		return categoryIds;
	}

	/**
	 * Ð Ñ›Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Â°Ð Ð…Ð Ñ‘Ð Âµ: Ð ÑŸÐ¡Ð‚Ð Ñ•Ð Ñ‘Ð Â·Ð Ð†Ð Ñ•Ð Ò‘Ð Ñ‘Ð¡â€š Ð Ñ•Ð Ñ—Ð ÂµÐ¡Ð‚Ð Â°Ð¡â€ Ð Ñ‘Ð¡Ð‹ Ð Ñ—Ð Ñ•Ð Ñ”Ð¡Ñ“Ð Ñ—Ð Ñ”Ð Ñ‘ Ð Â»Ð¡Ð‹Ð Â±Ð Ñ•Ð Ñ–Ð Ñ• Ð¡â€šÐ Ñ•Ð Ð†Ð Â°Ð¡Ð‚Ð Â°
	 * (Ð¡â€šÐ ÂµÐ Ñ˜Ð¡â€¹, Ð Ð…Ð Â°Ð Â±Ð Ñ•Ð¡Ð‚Ð Â° Ð¡â€šÐ ÂµÐ Ñ”Ð¿Ñ—Ð…?Ð¡â€šÐ Ñ•Ð Ð†, Ð¿Ñ—Ð…?Ð Ñ—Ð ÂµÐ¡â€ Ð Ñ‘Ð Â°Ð Â»Ð¡ÐŠÐ Ð…Ð Ñ•Ð Ñ–Ð Ñ• Ð Ñ—Ð¡Ð‚Ð ÂµÐ Ò‘Ð Â»Ð Ñ•Ð Â¶Ð ÂµÐ Ð…Ð Ñ‘Ð¿Ñ—Ð…?).
	 * Ð ÑŸÐ¡Ð‚Ð Ñ•Ð Ð†Ð ÂµÐ¡Ð‚Ð¿Ñ—Ð…?Ð ÂµÐ¡â€š Ð Ò‘Ð Ñ•Ð¿Ñ—Ð…?Ð¡â€šÐ Â°Ð¡â€šÐ Ñ•Ð¡â€¡Ð Ð…Ð Ñ•Ð Âµ Ð Ñ”Ð Ñ•Ð Â»Ð Ñ‘Ð¡â€¡Ð ÂµÐ¿Ñ—Ð…?Ð¡â€šÐ Ð†Ð Ñ• Ð Ò‘Ð ÂµÐ Ð…Ð ÂµÐ Â¶Ð Ð…Ð¡â€¹Ð¡â€¦
	 * Ð¿Ñ—Ð…?Ð¡Ð‚Ð ÂµÐ Ò‘Ð¿Ñ—Ð…?Ð¡â€šÐ Ð† Ð¡Ñ“ Ð Ñ—Ð Ñ•Ð Â»Ð¡ÐŠÐ Â·Ð Ñ•Ð Ð†Ð Â°Ð¡â€šÐ ÂµÐ Â»Ð¿Ñ—Ð…?. Ð Â¤Ð Ñ•Ð¡Ð‚Ð Ñ˜Ð Ñ‘Ð¡Ð‚Ð¡Ñ“Ð ÂµÐ¡â€š Ð Ð†Ð¿Ñ—Ð…?Ð Âµ
	 * Ð Ð…Ð ÂµÐ Ñ•Ð Â±Ð¡â€¦Ð Ñ•Ð Ò‘Ð Ñ‘Ð Ñ˜Ð¡â€¹Ð Âµ Ð Â·Ð Â°Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Ñ‘ Ð Ð† Ð Â±Ð Â°Ð Â·Ð Âµ Ð Ò‘Ð Â°Ð Ð…Ð Ð…Ð¡â€¹Ð¡â€¦.
	 * 
	 * @param purchaseId
	 * @return - empty String if transaction succeed, error mesage elsewise;
	 * @throws MalformedURLException
	 * @throws WrongResponseCodeException
	 * @throws IOException
	 * @throws JSONException
	 */
	// /MakePurchase.php
	public String makePurchase(String purchaseId) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/MakePurchase.php");
		params.put("User", account.getUserEmail(context));
		params.put("PurchaseID", purchaseId);

		JSONObject root = sendRequest(params);

		boolean transactionSuccess = root.getBoolean("TransactionSuccess");
		String errorLog = root.getString("ErrorLog");

		if (transactionSuccess) {
			Log.d(TAG, "Transaction success purchaseId" + purchaseId);
		} else {
			Log.e(TAG, "Transaction failes " + errorLog);
		}

		return errorLog;
	}

	/**
	 * @param categoryId
	 * @throws MalformedURLException
	 * @throws WrongResponseCodeException
	 * @throws IOException
	 * @throws JSONException
	 */
	// /GetTexts
	public void updateTextPacksByCategory(String categoryId) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/GetTexts.php");
		params.put("CategoryID", categoryId);
		updateTextPacks(params, null, categoryId);
		}
	
	// /GetTexts
	public void updateTextPacksByTheme(String themeId) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/GetTexts.php");
		params.put("ThemeID", themeId);
		updateTextPacks(params, themeId, null);
		}
	
	private void updateTextPacks(Params params, String themeId, String categoryid) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("TextPacks");

		List<TextPack> textPacks = new ArrayList<TextPack>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			TextPack textPack = parseTextPack(o, themeId, categoryid);
			String id = o.getString("ID");
			dbHelper.saveTextPack(textPack);
			
//			JSONArray textsArray = o.getJSONArray("Texts");
//			for (int j = 0; j < textsArray.length(); j++) {
//				JSONObject textObject = textsArray.getJSONObject(j);
//
//				String textId = textObject.getString("ID");
//				String textName = textObject.getString("Name");
//				String textString = textObject.getString("Text");
//
//				dbHelper.saveText(textId, id, textName, textString);
//				Log.v(TAG, "saveText " + textString);
//			}
		}
	}
	

	/**
	 * Returns List<ThemesCategory>
	 * 
	 * @param categoryId
	 * @throws IOException
	 * @throws JSONException
	 */
//	 GetTextsCategories.php
	public List<TextPack> getTextCategories(String group2Id,String categoryId) throws IOException, JSONException {

		Log.d(TAG, "GetTextsCategories");
		Params params = new Params("/GetTextsCategories.php");
		// params.put("Loc", account.getDeviceLanguage());
		// params.put("User", account.getUserEmail(context));
		params.put("GroupID", group2Id);

		return getTextPacks(params, categoryId, null);
	}
	
//	public List<TextCategory> getTextCategories(String group2Id) throws IOException, JSONException {
//
//		Log.d(TAG, "GetTextsCategories ");
//		Params params = new Params("/GetTextsCategories.php");
//		params.put("Loc", account.getDeviceLanguage());
//		params.put("User", account.getUserEmail(context));
//		params.put("GroupID", group2Id);
//		JSONObject root = sendRequest(params);
//		JSONArray response = root.getJSONArray("Categories");
//
//		List<TextCategory> textCatList = new ArrayList<TextCategory>();
//		for (int i = 0; i < response.length(); i++) {
//			JSONObject o = response.getJSONObject(i);
//
//			Category category = new Category(o.getString("ID"), o.getString("Name"), null);
//			Log.v(TAG, "category " + category.getName());
//
//			JSONArray themesArray = o.getJSONArray("Texts");
//			List<TextPack> textList = new ArrayList<TextPack>();
//			int length=themesArray.length();
//			for (int j = 0; j < themesArray.length(); j++) {
//				JSONObject themeObject = themesArray.getJSONObject(j);
//				TextPack textPack = parseTextPack(themeObject, null,null);
//				Log.w(TAG, "textPack " + textPack.getName());
//				textList.add(textPack);
//			}
//			textCatList.add(new TextCategory(category, textList));
//		}
//
//		return textCatList;
//	}

	public List<TextPack> getTextPacksByTheme(String themeId) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/GetTexts.php");
		// params.put("User", account.getUserEmail(context));
		// params.put("Loc", account.getDeviceLanguage());
		params.put("ThemeID", themeId);

		return getTextPacks(params, themeId, null);
	}

	public List<TextPack> getTextPacksByCategory(String categoryId) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/GetTexts.php");
		// params.put("User", account.getUserEmail(context));
		// params.put("Loc", account.getDeviceLanguage());
		params.put("CategoryID", categoryId);

		return getTextPacks(params, null, categoryId);
	}

	private List<TextPack> getTextPacks(Params params, String themeId, String categoryId) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("TextPacks");

		List<TextPack> textPacks = new ArrayList<TextPack>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);
			TextPack textPack = parseTextPack(o, themeId, categoryId);
			textPacks.add(textPack);
		}
		return textPacks;
	}

	private TextPack parseTextPack(JSONObject o, String themeId, String categoryId) throws JSONException {
		String id = o.getString("ID");
		String purchaseID = o.getString("PurchaseID");
		String name = o.getString("Name");
		String description = o.getString("Description");

		double price = o.getDouble("Price");
		boolean bought = o.getInt("Bought") == 1;
		String coverImage = o.getString("CoverImage");

		JSONArray textsArray = o.getJSONArray("Texts");
		for (int j = 0; j < textsArray.length(); j++) {
			JSONObject textObject = textsArray.getJSONObject(j);

			String textId = textObject.getString("ID");
			String textName = textObject.getString("Name");
			String textString = textObject.getString("Text");

			dbHelper.saveText(textId, id, textName, textString);
			Log.v(TAG, "saveText " + textString);
		}

		return new TextPack(id, name, description, categoryId, themeId, purchaseID, price, bought, coverImage);
	}

	/**
	 * Returns appeals structure list
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetAppeals.php
	public void updateAppeals() throws IOException, JSONException {
		Params params = new Params("/GetAppeals.php");
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Appeals");

		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			String id = o.getString("ID");
			String maleAppeal = o.getString("MaleAppeal");
			String femaleAppeal = o.getString("FemaleAppeal");

			dbHelper.saveAppeal(id, maleAppeal, femaleAppeal);
		}
	}

	/**
	 * Returns appeals structure list or null if nothing has been loaded
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetRecommendedPurchases.php
	public List<Article> getRecommendedPurchases() throws IOException, JSONException {
		Params params = new Params("/GetRecommendedPurchases.php");
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		params.put("Limit", 10);

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("RecommendedPurchases");

		List<Article> articlesList = new ArrayList<Article>();
		for (int i = 0; i < response.length(); i++) {
			Article article = parseArticle(response.getJSONObject(i), "getNewPurchases");
			articlesList.add(article);
		}
		return articlesList;
	}

	/**
	 * Ð Ñ›Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Â°Ð Ð…Ð Ñ‘Ð Âµ: Ð ÑŸÐ Ñ•Ð Â»Ð¡Ñ“Ð¡â€¡Ð Â°Ð ÂµÐ¡â€š Ð Ñ‘Ð Ð…Ð¡â€žÐ Ñ•Ð¡Ð‚Ð Ñ˜Ð Â°Ð¡â€ Ð Ñ‘Ð¡Ð‹ Ð Ñ• Ð Ð†Ð Ñ•Ð Â·Ð Ñ˜Ð Ñ•Ð Â¶Ð Ð…Ð¡â€¹Ð¡â€¦
	 * Ð Ð†Ð Â°Ð¡Ð‚Ð Ñ‘Ð Â°Ð Ð…Ð¡â€šÐ Â°Ð¡â€¦ Ð Ñ—Ð Ñ•Ð Ñ”Ð¡Ñ“Ð Ñ—Ð Ñ”Ð Ñ‘ Ð Ð†Ð Ð…Ð¡Ñ“Ð¡â€šÐ¡Ð‚Ð ÂµÐ Ð…Ð Ð…Ð ÂµÐ â„– Ð Ð†Ð Â°Ð Â»Ð¡Ð‹Ð¡â€šÐ¡â€¹, Ð Ñ‘Ð Â·Ð Ñ•Ð Â±Ð¡Ð‚Ð Â°Ð Â¶Ð ÂµÐ Ð…Ð Ñ‘Ð¿Ñ—Ð…?
	 * Ð Ñ—Ð Â°Ð Ñ”Ð Ñ•Ð Ð† Ð Ñ‘ Ð Ñ”Ð Ñ•Ð Â»Ð Ñ‘Ð¡â€¡Ð ÂµÐ¿Ñ—Ð…?Ð¡â€šÐ Ð†Ð Ñ• Ð Â·Ð Ð†Ð ÂµÐ Â·Ð Ò‘ Ð Ð† Ð Ð…Ð Ñ‘Ð¡â€¦.
	 * 
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetMoneyPacks.php
	public List<MoneyPack> getMoneyPacks() throws IOException, JSONException {
		Params params = new Params("/GetMoneyPacks.php");
		// params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		// params.put("Limit", 10);

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("MoneyPacks");

		List<MoneyPack> moneyPacksList = new ArrayList<MoneyPack>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			String id = o.getString("ID");
			String name = o.getString("Name");
			int count = o.getInt("Count");
			String imageUrl = o.getString("ImageName");
			String playId = o.getString("GooglePlayID");

			MoneyPack moneyPack = new MoneyPack(id, playId, name, count, imageUrl);
			moneyPacksList.add(moneyPack);
			Log.d(TAG, "getMoneyPacks " + id + " " + name + " " + count + " " + imageUrl);
			// dbHelper.saveAppeal(id, maleAppeal, femaleAppeal);
		}
		return moneyPacksList;
	}

	/**
	 * Ð ÑŸÐ¡Ð‚Ð Ñ•Ð Ñ‘Ð Â·Ð Ð†Ð Ñ•Ð Ò‘Ð Ñ‘Ð¡â€š Ð Ñ—Ð Ñ•Ð Ñ”Ð¡Ñ“Ð Ñ—Ð Ñ”Ð¡Ñ“ Ð Ð†Ð Ð…Ð¡Ñ“Ð¡â€šÐ¡Ð‚Ð ÂµÐ Ð…Ð Ð…Ð ÂµÐ â„– Ð Ð†Ð Â°Ð Â»Ð¡Ð‹Ð¡â€šÐ¡â€¹.
	 * 
	 * @return Ð â€™Ð Ñ•Ð Â·Ð Ð†Ð¡Ð‚Ð Â°Ð¡â€°Ð Â°Ð ÂµÐ¡â€šÐ¿Ñ—Ð…?Ð¿Ñ—Ð…? Ð Â»Ð Ñ‘Ð Â±Ð Ñ• true, Ð ÂµÐ¿Ñ—Ð…?Ð Â»Ð Ñ‘ Ð Ñ•Ð Ñ—Ð ÂµÐ¡Ð‚Ð Â°Ð¡â€ Ð Ñ‘Ð¿Ñ—Ð…? Ð Ñ—Ð¡Ð‚Ð Ñ•Ð¡â‚¬Ð Â»Ð Â°
	 *         Ð¡Ñ“Ð¿Ñ—Ð…?Ð Ñ—Ð ÂµÐ¡â‚¬Ð Ð…Ð Ñ•, Ð Â»Ð Ñ‘Ð Â±Ð Ñ• false, Ð Ð† Ð¿Ñ—Ð…?Ð Â»Ð¡Ñ“Ð¡â€¡Ð Â°Ð Âµ Ð Ð†Ð Ñ•Ð Â·Ð Ð…Ð Ñ‘Ð Ñ”Ð Ð…Ð Ñ•Ð Ð†Ð ÂµÐ Ð…Ð Ñ‘Ð¿Ñ—Ð…? Ð Ñ•Ð¡â‚¬Ð Ñ‘Ð Â±Ð Ñ”Ð Ñ‘.
	 *         Ð â€�Ð Ñ•Ð Ñ—Ð Ñ•Ð Â»Ð Ð…Ð Ñ‘Ð¡â€šÐ ÂµÐ Â»Ð¡ÐŠÐ Ð…Ð Ñ•, Ð Ñ—Ð¡Ð‚Ð Ñ‘ Ð Ñ•Ð¡â‚¬Ð Ñ‘Ð Â±Ð Ñ”Ð Âµ Ð Ð†Ð¡â€¹Ð Ð†Ð Ñ•Ð Ò‘Ð Ñ‘Ð¡â€šÐ¿Ñ—Ð…?Ð¿Ñ—Ð…? Ð ÂµÐ Âµ Ð Ñ•Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Â°Ð Ð…Ð Ñ‘Ð Âµ.
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetMoneyPacks.php
	public boolean buyMoneyPack(String moneyPackId) throws IOException, JSONException {
		Log.d(TAG, "buyMoneyPack transactionSacceed? moneyPackId " + moneyPackId);
		Params params = new Params("/BuyMoneyPack.php");
		params.put("User", account.getUserEmail(context));
		// params.put("Loc", account.getDeviceLanguage());
		params.put("MoneyPackID", moneyPackId);

		JSONObject root = sendRequest(params);
		boolean transactionSacceed = root.getBoolean("TransactionSuccess");
		if (!transactionSacceed)
			Toast.makeText(context, context.getResources().getString(R.string.str_transaction_failed), Toast.LENGTH_LONG).show();

		Log.d(TAG, "buyMoneyPack transactionSacceed? " + transactionSacceed + " moneyPackId " + moneyPackId);
		return transactionSacceed;
	}

	// /GetNewPurchases.php
	// Ð Ñ›Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Â°Ð Ð…Ð Ñ‘Ð Âµ: Ð â€™Ð Ñ•Ð Â·Ð Ð†Ð¡Ð‚Ð Â°Ð¡â€°Ð Â°Ð ÂµÐ¡â€š Ð Ñ—Ð ÂµÐ¡Ð‚Ð ÂµÐ¡â€¡Ð ÂµÐ Ð…Ð¡ÐŠ Ð Ð…Ð Ñ•Ð Ð†Ð Ñ‘Ð Ð…Ð Ñ•Ð Ñ”.
	// Ð ÑŸÐ Â°Ð¡Ð‚Ð Â°Ð Ñ˜Ð ÂµÐ¡â€šÐ¡Ð‚Ð¡â€¹:
	// User Ð²Ð‚â€œ Ð¿Ñ—Ð…?Ð¡â€šÐ¡Ð‚Ð Ñ•Ð Ñ”Ð Â°, Ð¿Ñ—Ð…?Ð Ñ•Ð Ñ•Ð¡â€šÐ Ð†Ð ÂµÐ¡â€šÐ¿Ñ—Ð…?Ð¡â€šÐ Ð†Ð¡Ñ“Ð ÂµÐ¡â€š Ð Â°Ð Ò‘Ð¡Ð‚Ð ÂµÐ¿Ñ—Ð…?Ð¡Ñ“ Ð¿Ñ—Ð…?Ð Â»Ð ÂµÐ Ñ”Ð¡â€šÐ¡Ð‚Ð Ñ•Ð Ð…Ð Ð…Ð Ñ•Ð â„–
	// Ð Ñ—Ð Ñ•Ð¡â€¡Ð¡â€šÐ¡â€¹ apple/google
	// Ð Â°Ð Ñ”Ð Ñ”Ð Â°Ð¡Ñ“Ð Ð…Ð¡â€šÐ Â°.
	// Loc - Ð¿Ñ—Ð…?Ð¡â€šÐ¡Ð‚Ð Ñ•Ð Ñ”Ð Â°, 2 Ð¿Ñ—Ð…?Ð Ñ‘Ð Ñ˜Ð Ð†Ð Ñ•Ð Â»Ð Â° Ð²Ð‚â€œ Ð Ñ•Ð Â±Ð Ñ•Ð Â·Ð Ð…Ð Â°Ð¡â€¡Ð ÂµÐ Ð…Ð Ñ‘Ð Âµ Ð Â»Ð Ñ•Ð Ñ”Ð Â°Ð Â»Ð Ñ‘Ð Â·Ð Â°Ð¡â€ Ð Ñ‘Ð Ñ‘.
	// Ð â€�Ð Ñ•Ð¿Ñ—Ð…?Ð¡â€šÐ¡Ñ“Ð Ñ—Ð Ð…Ð¡â€¹Ð Âµ Ð Â·Ð Ð…Ð Â°Ð¡â€¡Ð ÂµÐ Ð…Ð Ñ‘Ð¿Ñ—Ð…?:
	// EN, RU
	// Limit Ð²Ð‚â€œ Ð¡â€¡Ð Ñ‘Ð¿Ñ—Ð…?Ð Â»Ð Ñ•, Ð Ñ•Ð Ñ–Ð¡Ð‚Ð Â°Ð Ð…Ð Ñ‘Ð¡â€¡Ð Ñ‘Ð Ð†Ð Â°Ð ÂµÐ¡â€š Ð¡Ð‚Ð Â°Ð Â·Ð Ñ˜Ð ÂµÐ¡Ð‚ Ð Ð†Ð¡â€¹Ð Â±Ð Ñ•Ð¡Ð‚Ð Ñ”Ð Ñ‘.
	// Ð¿Ñ—Ð…?Ð ÂµÐ Ñ•Ð Â±Ð¿Ñ—Ð…?Ð Â·Ð Â°Ð¡â€šÐ ÂµÐ Â»Ð¡ÐŠÐ Ð…Ð¡â€¹Ð â„– Ð Ñ—Ð Â°Ð¡Ð‚Ð Â°Ð Ñ˜Ð ÂµÐ¡â€šÐ¡Ð‚, Ð ÂµÐ¿Ñ—Ð…?Ð Â»Ð Ñ‘
	// Ð Ð…Ð Âµ Ð¡Ñ“Ð Ñ”Ð Â°Ð Â·Ð Â°Ð Ð… Ð Ð†Ð¡â€¹Ð Â±Ð Ñ•Ð¡Ð‚Ð Ñ”Ð Â° Ð Â±Ð¡Ñ“Ð Ò‘Ð ÂµÐ¡â€š Ð Ñ•Ð Ñ–Ð¡Ð‚Ð Â°Ð Ð…Ð Ñ‘Ð¡â€¡Ð ÂµÐ Ð…Ð Â° Ð¿Ñ—Ð…?Ð Ñ•Ð¡â€šÐ Ð…Ð ÂµÐ â„–
	// Ð¿Ñ—Ð…?Ð Â»Ð ÂµÐ Ñ˜Ð ÂµÐ Ð…Ð¡â€šÐ Ñ•Ð Ð†.
	// GetNewPurchases.php
	public List<Article> getNewPurchases() throws IOException, JSONException {
		Params params = new Params("/GetNewPurchases.php");
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		params.put("Limit", 10);

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("NewPurchases");

		List<Article> articlesList = new ArrayList<Article>();
		for (int i = 0; i < response.length(); i++) {
			Article article = parseArticle(response.getJSONObject(i), "getNewPurchases ");
			articlesList.add(article);
		}
		return articlesList;
	}

	/**
	 * 
	 * @return Ð Ð†Ð Ñ•Ð Â·Ð Ð†Ð¡Ð‚Ð Â°Ð¡â€°Ð Â°Ð ÂµÐ¡â€š List<Article> Ð Ñ—Ð ÂµÐ¡Ð‚Ð ÂµÐ¡â€¡Ð ÂµÐ Ð…Ð¡ÐŠ Ð¡â€šÐ Ñ•Ð Ð†Ð Â°Ð¡Ð‚Ð Ñ•Ð Ð† Ð¡Ð‚Ð Â°Ð Ð…Ð Â¶Ð Ñ‘Ð¡Ð‚Ð Ñ•Ð Ð†Ð Â°Ð Ð…Ð Ð…Ð¡â€¹Ð â„– Ð Ñ—Ð Ñ•
	 *         Ð Ñ”Ð Ñ•Ð Â»Ð Ñ‘Ð¡â€¡Ð ÂµÐ¿Ñ—Ð…?Ð¡â€šÐ Ð†Ð¡Ñ“ Ð Ñ—Ð¡Ð‚Ð Ñ•Ð Ò‘Ð Â°Ð Â¶
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetBestSellers.php
	public List<Article> getBestSellers() throws IOException, JSONException {
		Params params = new Params("/GetBestSellers.php");
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		params.put("Limit", 10);

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("BestSellers");

		List<Article> articlesList = new ArrayList<Article>();
		for (int i = 0; i < response.length(); i++) {
			Article article = parseArticle(response.getJSONObject(i), "getNewPurchases ");
			articlesList.add(article);
		}
		return articlesList;
	}

	private Article parseArticle(JSONObject jsonObject, String logTitle) throws JSONException {
		JSONObject o = jsonObject;

		String id = "";// o.getString("ObjectID");
		String name = o.getString("Name");
		String purchaseId = o.getString("PurchaseID");
		String purchaseType = o.getString("PurchaseTypeID");
		String cover = o.getString("CoverImage");
		float price;
		if (logTitle != "Article for offer")
			price = (float) o.getDouble("Price");
		else
			price = 0;
		int bought;
		if (logTitle != "Article for offer")
			bought = o.getInt("Bought");
		else
			bought = 0;

		if (logTitle != null)
			Log.d(TAG, logTitle + " " + name + " " + purchaseId + " " + price + " " + cover);

		return new Article(id, name, purchaseId, purchaseType, cover, price, bought == 1);

	}

	// /GetOffers.php
	public List<Offer> getOffers(String categoryID) throws IOException, JSONException {
		Params params = new Params("/GetOffers.php");
		params.put("CategoryID", categoryID);
		return getOffers(params);
	}

	// GetOffersCategories
	public List<Offer> getOffersCategories(String groupID) throws IOException, JSONException {
		Params params = new Params("/GetOffersCategories.php");
		params.put("GroupID", groupID);
		return getOffers(params);
	}

//	/GetAllOffers.php
	public List<Offer> getAllOffers() throws IOException, JSONException {
		Params params = new Params("/GetAllOffers.php");
		return getOffers(params);
	}
	
	private List<Offer> getOffers(Params params) throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Offers");

		List<Offer> offersList = new ArrayList<Offer>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			offersList.add(parseOffer(o));
		}
		return offersList;
	}

	private Offer parseOffer(JSONObject o) throws JSONException {
		String id = o.getString("ID");
		String purchaseId = o.getString("PurchaseID");
		String name = o.getString("Name");
		String description = o.getString("Description");
		float price = (float) o.getDouble("Price");
		int bought = o.getInt("Bought");
		String cover = o.getString("CoverImage");
		String endDate = o.getString("EndDate");

		Offer offer = new Offer(id, name, purchaseId, description, cover, price, bought == 1, endDate);

		JSONArray articlesArray = o.getJSONArray("Content");
		for (int j = 0; j < articlesArray.length(); j++) {
			JSONObject articleObject = articlesArray.getJSONObject(j);
			offer.addArticle(parseArticle(articleObject, "Article for offer"));
		}
		return offer;
	}


	// /GetBanners.php
	public List<Banner> getBanners() throws IOException, JSONException {
		Params params = new Params("/GetBanners.php");
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Banners");

		List<Banner> bannersList = new ArrayList<Banner>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			String id = o.getString("ID");
			String coverImage = o.getString("CoverImage");
			String purchaseId = o.getString("PurchaseID");
			String purchaseTypeId = o.getString("PurchaseTypeID");

			Banner banner = new Banner(id, purchaseId, purchaseTypeId, coverImage);
			bannersList.add(banner);
			Log.d(TAG, "getBanners " + id + " " + purchaseId + " " + purchaseTypeId + " " + coverImage);
		}
		return bannersList;
	}
	
	// GetBoughtThemes.php
	public List<Theme> getBoughtThemes() throws IOException, JSONException {

		Log.d(TAG, "GetBoughtThemes ");
		Params params = new Params("/GetBoughtThemes.php");
		params.put("Loc", account.getDeviceLanguage());
		params.put("User", account.getUserEmail(context));

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Themes");

		List<Theme> themesList = new ArrayList<Theme>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			Log.d(TAG, "updateThemes " + o.getString("ID"));

			String id = o.getString("ID");
			String name = o.getString("Name");
			String purchaseId = o.getString("PurchaseID");
			String author = o.getString("Author");
			String coverImage = o.getString("CoverImage");
			String eCardThumb = o.getString("ECardThumb");
			String squareThumb = o.getString("SquareThumb");
			String postCardFrontThumb = o.getString("PostCardFrontThumb");

			Theme theme = new Theme(id, name);
			theme.setPurchaseId(purchaseId);
			theme.setAuthor(author);
			theme.setCoverImage(coverImage);
			theme.setECardThumb(eCardThumb);
			theme.setSquareThumb(squareThumb);
			theme.setPostCardFrontThumb(postCardFrontThumb);
			theme.setBought(true);

			themesList.add(theme);
			Log.v(TAG, id + " " + name + " " + purchaseId + " " + author + " " + coverImage + " " + eCardThumb + " " + squareThumb + " " + postCardFrontThumb);
		}

		Log.d(TAG, "GetBoughtThemes " + themesList.size() + " \n" + themesList.toString());
		return themesList;
	}

	/**
	 * Download from server list of Text packs, and texts, saves it to database
	 * 
	 * @return 
	 * @throws MalformedURLException
	 * @throws WrongResponseCodeException
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetBoughtTexts.php
	public List<TextPack> getBoughtTextPacks() throws MalformedURLException, WrongResponseCodeException, IOException, JSONException {
		Params params = new Params("/GetBoughtTexts.php");
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("TextPacks");

		List<TextPack> textPacks = new ArrayList<TextPack>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			String id = o.getString("ID");
			String name = o.getString("Name");

			TextPack textPack = new TextPack(id, name);

			String purchaseId = o.getString("PurchaseID");
			String description = o.getString("Description");
			String coverImage = o.getString("CoverImage");

			textPack.setDescription(description);
			textPack.setPurchaseId(purchaseId);
			textPack.setCoverImage(coverImage);
			textPack.setBought(true);

			textPacks.add(textPack);
			Log.d(TAG, "getBoughtTextPacks " + name + " " + description);
		}

		return textPacks;
	}

	// /GetBoughtOffers
	public List<Offer> getBoughtOffers() throws IOException, JSONException {
		Params params = new Params("/GetBoughtOffers.php");
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("Offers");

		List<Offer> offersList = new ArrayList<Offer>();
		for (int i = 0; i < response.length(); i++) {
			JSONObject o = response.getJSONObject(i);

			String id = o.getString("ID");
			String purchaseId = o.getString("PurchaseID");
			String name = o.getString("Name");
			String description = o.getString("Description");
			String cover = o.getString("CoverImage");
			String endDate = o.getString("EndDate");

			Offer offer = new Offer(id, name, purchaseId, description, cover, 0, true, endDate);

			JSONArray articlesArray = o.getJSONArray("Content");
			for (int j = 0; j < articlesArray.length(); j++) {
				JSONObject articleObject = articlesArray.getJSONObject(j);
				offer.addArticle(parseArticle(articleObject, "Article for offer"));
			}

			offersList.add(offer);
			Log.d(TAG, "getBoughtOffers " + id + " " + name + " " + purchaseId + " " + description + " " + " " + endDate + " articlesList size " + offer.getArticlesList().size());
			// dbHelper.saveAppeal(id, maleAppeal, femaleAppeal);
		}
		return offersList;
	}

	/**
	 * Ð â€™Ð Ñ•Ð Â·Ð Ð†Ð¡Ð‚Ð Â°Ð¡â€°Ð Â°Ð ÂµÐ¡â€š Ð Ò‘Ð Â°Ð Ð…Ð Ð…Ð¡â€¹Ð Âµ Ð Ñ• Ð¡â€šÐ Ñ•Ð Ð†Ð Â°Ð¡Ð‚Ð Âµ
	 * 
	 * @param purchaseID
	 *            - article identificator
	 * @param purchaseType
	 *            - type of article
	 * @return list of articles (themes, texts, offers)
	 * @throws IOException
	 * @throws JSONException
	 */
	// GetPurchase
	public Article getPurchase(String purchaseID, String purchaseType) throws IOException, JSONException {
		Params params = new Params("/GetPurchase.php");
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		params.put("PurchaseID", purchaseID);

		Log.v(TAG, "GET PURCAHSE !!!!!!!!!!!!!!!!!!!+");
		JSONObject root = sendRequest(params);

		Article article = null;
		switch (Integer.parseInt(purchaseType)) {
		// Themes
		case 1: {
			Log.d(TAG, "Themes");
			JSONArray response = root.getJSONArray("Purchase");
			for (int i = 0; i < response.length(); i++) {
				JSONObject o = response.getJSONObject(i);
				Theme theme = parseTheme(o, null);
				article = theme;
			}
		}

			break;
		// Texts
		case 2: {
			Log.v(TAG, "Texts");
			JSONArray response = root.getJSONArray("Purchase");
			for (int i = 0; i < response.length(); i++) {
				JSONObject o = response.getJSONObject(i);

				TextPack textPack = parseTextPack(o, null, null);
				article = textPack;
			}
		}

			break;
		// Offers
		case 3: {
			Log.d(TAG, "Offers");
			JSONArray response = root.getJSONArray("Purchase");
			for (int i = 0; i < response.length(); i++) {
				JSONObject o = response.getJSONObject(i);
				Offer offer = parseOffer(o);
				article = offer;
			}
		}

			break;
		default:
			break;
		}

		// System.out.println(articlesList.toString());
		return article;
	}

	// /Search.php
	public List<Article> search(String searchString) throws IOException, JSONException {
		Params params = new Params("/Search.php");
		params.put("User", account.getUserEmail(context));
		params.put("Loc", account.getDeviceLanguage());
		params.put("SearchString", searchString);

		JSONObject root = sendRequest(params);
		JSONArray response = root.getJSONArray("SearchResult");

		List<Article> articlesList = new ArrayList<Article>();
		for (int i = 0; i < response.length(); i++) {
			Article article = parseArticle(response.getJSONObject(i), "getSearch ");
			articlesList.add(article);
		}
		return articlesList;
	}

	// /GetWealth.php
	// Ð Ñ›Ð Ñ—Ð Ñ‘Ð¿Ñ—Ð…?Ð Â°Ð Ð…Ð Ñ‘Ð Âµ: Ð â€™Ð Ñ•Ð Â·Ð Ð†Ð¡Ð‚Ð Â°Ð¡â€°Ð Â°Ð ÂµÐ¡â€š Ð¡â€šÐ ÂµÐ Ñ”Ð¡Ñ“Ð¡â€°Ð ÂµÐ Âµ Ð Ñ”Ð Ñ•Ð Â»Ð Ñ‘Ð¡â€¡Ð ÂµÐ¿Ñ—Ð…?Ð¡â€šÐ Ð†Ð Ñ• Ð Ò‘Ð ÂµÐ Ð…Ð ÂµÐ Ñ– Ð Ð…Ð Â°
	// Ð¿Ñ—Ð…?Ð¡â€¡Ð ÂµÐ¡â€šÐ¡Ñ“ Ð Ñ—Ð Ñ•Ð Â»Ð¡ÐŠÐ Â·Ð Ñ•Ð Ð†Ð Â°Ð¡â€šÐ ÂµÐ Â»Ð¿Ñ—Ð…?
	// Ð ÑŸÐ Â°Ð¡Ð‚Ð Â°Ð Ñ˜Ð ÂµÐ¡â€šÐ¡Ð‚Ð¡â€¹:
	// User Ð²Ð‚â€œ Ð¿Ñ—Ð…?Ð¡â€šÐ¡Ð‚Ð Ñ•Ð Ñ”Ð Â°, Ð¿Ñ—Ð…?Ð Ñ•Ð Ñ•Ð¡â€šÐ Ð†Ð ÂµÐ¡â€šÐ¿Ñ—Ð…?Ð¡â€šÐ Ð†Ð¡Ñ“Ð ÂµÐ¡â€š Ð Â°Ð Ò‘Ð¡Ð‚Ð ÂµÐ¿Ñ—Ð…?Ð¡Ñ“ Ð¿Ñ—Ð…?Ð Â»Ð ÂµÐ Ñ”Ð¡â€šÐ¡Ð‚Ð Ñ•Ð Ð…Ð Ð…Ð Ñ•Ð â„–
	// Ð Ñ—Ð Ñ•Ð¡â€¡Ð¡â€šÐ¡â€¹ apple/google
	// Ð Â°Ð Ñ”Ð Ñ”Ð Â°Ð¡Ñ“Ð Ð…Ð¡â€šÐ Â°.
	// Ð ÑŸÐ¡Ð‚Ð Ñ‘Ð Ñ˜Ð ÂµÐ¡Ð‚ JSON:
	// {
	// "Wealth": 1034.00
	// }
	// /GetWealth.php
	public float getWealth() throws IOException, JSONException {
		Params params = new Params("/GetWealth.php");
		params.put("User", account.getUserEmail(context));

		JSONObject root = sendRequest(params);
		return root.getLong("Wealth");
	}

}
