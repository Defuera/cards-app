package ru.fastcards.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.json.JSONException;
import ru.fastcards.R;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.common.Recipient;
import ru.fastcards.inapp.Purchase;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.FbApi;
import ru.fastcards.social.api.KException;
import ru.fastcards.social.api.VkApi;

import java.io.*;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static final String TAG = "Utils";

	public static String extractPattern(String string, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(string);
		if (!m.find())
			return null;
		return m.toMatchResult().group(1);
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		InputStreamReader r = new InputStreamReader(is);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[1024];
		try {
			for (int n; (n = r.read(buffer)) != -1;)
				sw.write(buffer, 0, n);
		} finally {
			try {
				is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return sw.toString();
	}

	public static void closeStream(Object oin) {
		if (oin != null)
			try {
				if (oin instanceof InputStream)
					((InputStream) oin).close();
				if (oin instanceof OutputStream)
					((OutputStream) oin).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private static String pattern_string_profile_id = "^(id)?(\\d{1,10})$";
	private static Pattern pattern_profile_id = Pattern
			.compile(pattern_string_profile_id);

	public static String parseProfileId(String text) {
		Matcher m = pattern_profile_id.matcher(text);
		if (!m.find())
			return null;
		return m.group(2);
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;

	}


	public static long formatDateWithDefault(String dateString, String defaultFormat) {
		Date date = null;
		SimpleDateFormat simpleD;
		try{
			simpleD = new SimpleDateFormat(defaultFormat);
			date = simpleD.parse(dateString);
		}catch(java.text.ParseException e){
			try {
				simpleD = new SimpleDateFormat("yyyy-MM-dd");
				date = simpleD.parse(dateString);
			} catch (java.text.ParseException e1) {
				e1.printStackTrace();
//				simpleD = new SimpleDateFormat("MMM dd, yyyy");
//				try {
//					date = simpleD.parse(dateString);
//				} catch (ParseException e2) {
//					e2.printStackTrace();
//
//				}
			}
		}
		if (date == null)
			return 0;
		return date.getTime();
	}

	public static long formatDate(String dateString, String pattern) {
		Date date = null;
		SimpleDateFormat simpleD;
		try {
			simpleD = new SimpleDateFormat(pattern);
			date = simpleD.parse(dateString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		if (date == null)
			return 0;
//		Log.v(TAG, "formatDate "+dateString+" to "+date);
		return date.getTime();
	}

	/**
	 * Makes year field not important for comparing
	 * @param date - date to clear
	 * @return the same date ignoring anything but day and month
	 */
	public static Date clearDate(Date date) { 
		Calendar calendar = Calendar.getInstance();		
		int currentYear = calendar.get(Calendar.YEAR);
		calendar.setTime(date);	
		if (calendar.get(Calendar.YEAR) < currentYear)
			calendar.set(Calendar.YEAR, currentYear);
		return calendar.getTime();		
	}
	
	public static long clearDate(long date) { 
		Calendar calendar = Calendar.getInstance();		
		int currentYear = calendar.get(Calendar.YEAR);
		calendar.setTimeInMillis(date);	
		if (calendar.get(Calendar.YEAR) < currentYear)
			calendar.set(Calendar.YEAR, currentYear);
		return calendar.getTimeInMillis();		
	}
	
	/**
	 * Uses static final constants to detect if the device's platform version is
	 * Gingerbread or later.
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * Uses static final constants to detect if the device's platform version is
	 * Honeycomb or later.
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * Uses static final constants to detect if the device's platform version is
	 * Honeycomb MR1 or later.
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * Uses static final constants to detect if the device's platform version is
	 * ICS or later.
	 */
	public static boolean hasICS() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
	
	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	/**
	 * 
	 * @param context
	 * @param data - versions type (can be calendar, categories, etc. anything loadable from server)
	 * @param handler - callback to be called after data is updated
	 */
	public static void checkForUpdate(final Context context, final String data, final Handler.Callback handler) {
		if (Utils.isNetworkAvailable(context)) {
			final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);

			new AsyncTask<Object, Object, Boolean>() {

				@Override
				protected Boolean doInBackground(Object... params) {
					boolean result = false;
					try {
						// String data = Constants.VERSIONS_CALENDAR;
						Api api = Api.getInstanse(context);
						long lastModified = api.getVersions(data);
						result = dbHelper.checkForUpdates(data, lastModified);
						if (result) {
							api.updateData(data);
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (WrongResponseCodeException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return result;
				}

				protected void onPostExecute(Boolean result) {
//					if (result) {
//
//					}
					Message msg = Message.obtain(null, result ? 1 : 0);
					handler.handleMessage(msg);
				};

			}.execute();

		}
	}

	public static void setRecipientsExtra(List<Recipient> recList,	Intent intent) {

		int arrayLength = recList.size();
		
		String[] friendIdsArray = new String[arrayLength];
		String[] friendNamesArray = new String[arrayLength];
		String[] friendNickNameArray = new String[arrayLength];
		String[] friendThumbsArray = new String[arrayLength];
		int[] friendGendersArray = new int[arrayLength];

		if (!recList.isEmpty()) {
			for (int i = 0; i < recList.size(); i++) {
				Recipient recipient = recList.get(i);
				
				friendIdsArray[i] = recipient.getId();
				friendNamesArray[i] = recipient.getName();
				friendNickNameArray[i] = recipient.getNickName();
				friendThumbsArray[i] = recipient.getImageUri();
				friendGendersArray[i] = recipient.getGender();
			}
			
			intent.putExtra(Constants.EXTRA_RECIPIENTS_IDS, friendIdsArray);
			intent.putExtra(Constants.EXTRA_NAME, friendNamesArray);
			intent.putExtra(Constants.EXTRA_RECIPIENTS_NICK_NAME,	friendNickNameArray);
			intent.putExtra(Constants.EXTRA_RECIPIENTS_THUMB_URL, friendThumbsArray);
			intent.putExtra(Constants.EXTRA_RECIPIENTS_GENDER, friendGendersArray);
		}
	}

	public static List<Recipient> getRecipientsExtra(Intent data, int origin) {
		String[] friendIdsArray = data.getStringArrayExtra(Constants.EXTRA_RECIPIENTS_IDS);
		String[] friendNamesArray = data.getStringArrayExtra(Constants.EXTRA_NAME);
		String[] friendNickNameArray = data.getStringArrayExtra(Constants.EXTRA_RECIPIENTS_NICK_NAME);
		String[] friendThumbsArray = data.getStringArrayExtra(Constants.EXTRA_RECIPIENTS_THUMB_URL);
		int[] friendGendersArray = data.getIntArrayExtra(Constants.EXTRA_RECIPIENTS_GENDER);
		

		List<Recipient> recipientsList = new ArrayList<Recipient>();
		
		if (friendIdsArray != null)
		for (int i = 0; i < friendIdsArray.length; i++) {
			recipientsList.add(new Recipient(friendIdsArray[i],	friendNamesArray[i], friendNickNameArray[i],
				friendThumbsArray[i], friendGendersArray[i], origin));
	}

		return recipientsList;
	}

	public static void setIIsGroupDataExtra(List<ISendableItem> itemsList, Intent intent) {
		int arrayLength = itemsList.size();
		
		String[] idsArray = new String[arrayLength];
		boolean[] isGroupArray = new boolean[arrayLength];

		if (!itemsList.isEmpty()) {
			for (int i = 0; i < itemsList.size(); i++) {
				ISendableItem item = itemsList.get(i);
				
				idsArray[i] = item.getId();
				isGroupArray[i] = item.isGroup();
			}
			
			intent.putExtra(Constants.EXTRA_RECIPIENTS_IDS, idsArray);
			intent.putExtra(Constants.EXTRA_IS_GROUP, isGroupArray);
		}
	}
	
	public static List<ISendableItem> getIIsGroupDataExtra(Context context,	Intent data) {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);

		String[] IdsArray = data.getStringArrayExtra(Constants.EXTRA_RECIPIENTS_IDS);
		boolean[] isGroupArray = data.getBooleanArrayExtra(Constants.EXTRA_IS_GROUP);

		List<ISendableItem> itemsList = new ArrayList<ISendableItem>();

		if (IdsArray != null)
			for (int i = 0; i < IdsArray.length; i++) {
				if (isGroupArray[i]) {
					itemsList.add(dbHelper.getContactsList(IdsArray[i]));

				} else {
					itemsList.add(dbHelper.getRecipient(IdsArray[i]));
				}

			}

		return itemsList;
	}
	
	public static void importFriendsToDataBase(final Context context, final String request, final Handler handler) {

		new AsyncTask<String, String, String>() {
			private ProgressDialog pd;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd = new ProgressDialog(context);
				pd.setTitle(context.getString(R.string.title_loading_dialog));
				pd.setMessage(context.getString(R.string.str_loading_contacts));
				pd.setCancelable(false);
				pd.show();
			}

			@Override
			protected String doInBackground(String... params) {
				try {
					if (Constants.COMUNICATION_TYPE_FB.equals(request)){
						FbApi fbApi = new FbApi();
						fbApi.importFriendsToDataBase(context);
					}
					if(Constants.COMUNICATION_TYPE_VK.equals(request)){
						Account account = Account.getInstance();
						String uid = account.getVkontakteUserId();
						VkApi vkApi = new VkApi(account.getVkontakteToken(), uid);
						vkApi.importFriendsToDataBase(uid, null, context);
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (KException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (pd != null)
					pd.dismiss();
				if (handler != null)
					handler.sendEmptyMessage(0);
			}
		}.execute();
		
	}

	/**
	 * Unconsumed items may exist in case payment of currensy have been succesful with google play server, 
	 * but connection with fastcards server has failed, so fastcards server haven't been updated;
	 */
	public static void checkForUnconsumedItems(Context context){
		final DataBaseHelper db = DataBaseHelper.getInstance(context);
		List<Purchase> purchasesList = db.getStarPurchases();
		if (!purchasesList.isEmpty()){
			final Api api = Api.getInstanse(context);
			for (Purchase purchase : purchasesList){
				final String sku = purchase.getSku(); 
				Log.e(TAG, "consume Locally purchase "+sku);
				
				new AsyncTask<Object, Object, Boolean>(){

					@Override
					protected Boolean doInBackground(Object... params) {
						try {
							return api.buyMoneyPack(sku);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return false;
					}
					
					@Override
					protected void onPostExecute(Boolean result) {
						if (result){
							db.removeStarPurchase(sku);
						}
					};
					
				}.execute();
				
			}
		}
	}

	public static void deleteRecFromDataBase(final Context context,final String comType,  final Handler handler) {
			new AsyncTask<Object, Object, Object>() {
				ProgressDialog dialog;

				protected void onPreExecute() {
					dialog = new ProgressDialog(context);
					dialog.setTitle(context.getString(R.string.str_log_out_title));
					dialog.setMessage(context.getString(R.string.str_please_wait));
					dialog.show();
				};

				@Override
				protected Object doInBackground(Object... params) {
					DataBaseHelper.getInstance(context).deleteRecipientsByComunicationType(comType);
					return null;
				};

				protected void onPostExecute(Object result) {
					dialog.dismiss();
					if (handler != null)
						handler.sendEmptyMessage(0);
				}
			}.execute();

		
		
	}
	
}
