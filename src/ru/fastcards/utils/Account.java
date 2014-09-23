package ru.fastcards.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.common.Comunication;
import ru.fastcards.shop.WealthView;
import ru.fastcards.social.api.Api;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Patterns;

import com.facebook.Session;
import com.sromku.simple.fb.SimpleFacebook;

public final class Account {

	private static final String TAG = "Account";
	private String vkontakteToken;
	private static String vkontakteUsertId;
	// private static String facebookToken;
	private static String instagramToken;
	private static String fastCardsId;
	private static String primaryContact;

	private static float wealth;

	private static Account account;

	public static Account getInstance() {
		Account temporaryAcc = account;
		if (temporaryAcc == null) {
			synchronized (Account.class) {
				temporaryAcc = account;
				if (temporaryAcc == null) {
					account = temporaryAcc = new Account();
				}
			}
		}
		return temporaryAcc;
	}

	public boolean hasVkToken() {
		Log.w(TAG, "hasVkToken " + (vkontakteToken != null) + " " + vkontakteToken);
		return vkontakteToken != null;
	}

	public void restoreAccount(Context ctx) {
		SharedPreferences accountCache = ctx.getSharedPreferences(Constants.CACHE_FILE_NAME, 0);
		if (accountCache != null) {
			setVkontakteToken(accountCache.getString(Constants.CACHE_VKONTAKTE_TOKEN, null));
			setVkontakteUserId(accountCache.getString(Constants.CACHE_VKONTAKTE_USER_ID, null));
			setOdnoklassnikiToken(accountCache.getString(Constants.CACHE_INSTAGRAM_TOKEN, null));
			setFastcardsUserId(accountCache.getString(Constants.CACHE_FASTCARDS_USER_ID, null));
			setWealth(accountCache.getFloat(Constants.CACHE_USER_WEALTH, 0));
		}
	}

	public Comunication getPrimaryComunication(Context ctx) {
		SharedPreferences accountCache = ctx.getSharedPreferences(Constants.CACHE_FILE_NAME, 0);
		if (accountCache == null) {
			Log.d(TAG, "account.getPrimaryContact = " + null);
			return getAnyComunication();
		}
		Log.d(TAG, "account.getPrimaryContact = " + accountCache.getString(Constants.CACHE_USER_PRIMARY_CONTACT, null));
		String comType = accountCache.getString(Constants.CACHE_USER_PRIMARY_CONTACT, null);
		if (Constants.COMUNICATION_TYPE_VK.equals(comType))
			return new Comunication("0", "0", Constants.COMUNICATION_TYPE_VK, getVkontakteUserId());
		else if (Constants.COMUNICATION_TYPE_FB.equals(comType))
			return new Comunication("0", "0", Constants.COMUNICATION_TYPE_FB, "");
		else
			return getAnyComunication();

	}

	private Comunication getAnyComunication() {
		if (isLoggedInVk())
			return new Comunication("0", "0", Constants.COMUNICATION_TYPE_VK, getVkontakteUserId());
		if (isLoggedInFb())
			return new Comunication("0", "0", Constants.COMUNICATION_TYPE_FB, "");
		return null;
	}

	public void setPrimaryContact(Context context, String primaryContact) {
		SharedPreferences.Editor cacheEditor = context.getSharedPreferences(Constants.CACHE_FILE_NAME, 0).edit();
		cacheEditor.putString(Constants.CACHE_USER_PRIMARY_CONTACT, primaryContact);
		cacheEditor.commit();
	}

	/**
	 * Determines device default language
	 * 
	 * @return Locale.getDefault().getCountry()
	 */
	public String getDeviceLanguage() {
		return Locale.getDefault().getISO3Language();
		// return "RU";

	}

	public void setFastcardsUserId(String id) {
		account.fastCardsId = id;
	}

	public String getFastcardsUserId() {
		return fastCardsId;
	}

	public String getVkontakteToken() {
		Log.d(TAG, "getVkontakteToken " + vkontakteToken);
		return vkontakteToken;
	}

	public void setVkontakteToken(String vkontakteToken) {
		Log.d(TAG, "setVkontakteToken " + vkontakteToken);
		this.vkontakteToken = vkontakteToken;
	}

	/**
	 * Removes vk token from cache and from shared prefs, call save() function is not necessary after calling this method
	 * 
	 * @param context
	 */
	public void removeVkontakteToken(Context context) {
		SharedPreferences.Editor cacheEditor = context.getSharedPreferences(Constants.CACHE_FILE_NAME, 0).edit();
		cacheEditor.putString(Constants.CACHE_VKONTAKTE_TOKEN, null);
		cacheEditor.commit();

		this.vkontakteToken = null;

		Log.d(TAG, "removeVkontakteToken " + vkontakteToken);
	}

	public String getFacebookToken() {
		Session session = Session.getActiveSession();
		return session == null ? null : Session.getActiveSession().getAccessToken();
	}

	// public void setFacebookToken(String facebookToken) {
	// account.facebookToken = facebookToken;
	// }

	public boolean isLoggedInFb() {
		SimpleFacebook simpleFb = SimpleFacebook.getInstance();
		return simpleFb.isLogin();
	}

	public boolean isLoggedInVk() {
		return hasVkToken();
	}

	public String getOdnoklassnikiToken() {
		return instagramToken;
	}

	public void setOdnoklassnikiToken(String instagramToken) {
		account.instagramToken = instagramToken;
	}

	public String getVkontakteUserId() {
		return vkontakteUsertId;
	}

	public void setVkontakteUserId(String vkontakteUsertId) {
		this.vkontakteUsertId = vkontakteUsertId;
	}

	public void save(Context context) {
		SharedPreferences.Editor cacheEditor = context.getSharedPreferences(Constants.CACHE_FILE_NAME, 0).edit();
		if (vkontakteToken != null)
			cacheEditor.putString(Constants.CACHE_VKONTAKTE_TOKEN, vkontakteToken);
		if (vkontakteUsertId != null)
			cacheEditor.putString(Constants.CACHE_VKONTAKTE_USER_ID, vkontakteUsertId);
		// if (facebookToken != null) cacheEditor.putString(Constants.CACHE_FACEBOOK_TOKEN, facebookToken);
		if (instagramToken != null)
			cacheEditor.putString(Constants.CACHE_INSTAGRAM_TOKEN, instagramToken);
		if (fastCardsId != null)
			cacheEditor.putString(Constants.CACHE_FASTCARDS_USER_ID, fastCardsId);
		cacheEditor.putFloat(Constants.CACHE_USER_WEALTH, wealth);
		cacheEditor.commit();
	}

	public boolean hasFbToken() {
		return isLoggedInFb();
	}

	public float getWealth() {
		return wealth;
	}

	private void setWealth(float wealth) {
		this.wealth = wealth;
	}

	public void setWealth(Context context, float wealth) {
		this.wealth = wealth;
		SharedPreferences.Editor cacheEditor = context.getSharedPreferences(Constants.CACHE_FILE_NAME, 0).edit();
		cacheEditor.putFloat(Constants.CACHE_USER_WEALTH, wealth);
		cacheEditor.commit();
	}

	public boolean hasFastcardsId() {
		return fastCardsId != null;
	}

	public String getUserEmail(Context context) {
		if (context == null)
			return null;
		String possibleEmail = null;
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		android.accounts.Account[] accounts = AccountManager.get(context).getAccounts();
		for (android.accounts.Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				possibleEmail = account.name;
			}
		}
		return possibleEmail;
	}

	public List<Comunication> getComunicationList() {
		List<Comunication> listCom = new ArrayList<Comunication>();
		if (hasVkToken())
			listCom.add(new Comunication("0", "0", Constants.COMUNICATION_TYPE_VK, getVkontakteUserId()));
		if (hasFbToken())
			listCom.add(new Comunication("0", "0", Constants.COMUNICATION_TYPE_FB, getFacebookToken()));
		return listCom;
	}

	// public Comunication getPrimaryComunication() {
	// return new Comunication("0", "0", Constants.COMUNICATION_TYPE_VK_ID, getVkontakteUserId());
	//
	// }

	public String getVkontakteUserName(Context context) {
		String userName;
		if (isLoggedInVk())
			userName = context.getString(R.string.str_logged_in);// "Logged in";
		else
			userName = context.getString(R.string.str_not_synced);// "Not synced";
		return userName;
	}

	public String getFbUserName(Context context) {
		String userName;
		if (isLoggedInFb())
			userName = context.getString(R.string.str_logged_in);
		else
			userName = context.getString(R.string.str_not_synced);// "Not synced";
		return userName;
	}

	public void refreshWealth(Context ctx) {
		final Context context = ctx;
		final Api api = Api.getInstanse(context);
		new AsyncTask<Object, Object, Float>() {

			@Override
			protected Float doInBackground(Object... params) {
				try {
					return api.getWealth();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				return -1.0f;
			}

			protected void onPostExecute(Float wealth) {
				if (wealth != -1.0f) {
					setWealth(context, wealth);
					WealthView.getInstanse((FragmentActivity) context).refreshWealth();
				}
			};
		}.execute();
	}

}
