package ru.fastcards.send;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.protocol.HTTP;

import ru.fastcards.HubActivity;
import ru.fastcards.R;
import ru.fastcards.common.Comunication;
import ru.fastcards.social.api.FbApi;
import ru.fastcards.social.api.VkApi;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.Utils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SqliteWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.provider.MediaStore.Images;
import android.provider.Telephony;
import android.provider.Telephony.Carriers;
import android.util.Log;
import android.widget.Toast;

import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Photo;


public class SendPostcardTask implements Comparable<SendPostcardTask>{

	private final String TAG = "SendPostcardTask";
//	private ISenderActivity activity;
	private Bitmap postcard;
	private List<Comunication> comList;
	
	/**
	 * For debbuging only
	 */
//	private String recName;
	private HubActivity activity;
	private Callback taskCallback;
	private String text;
	private String comType;

	/**
	 * 
	 * @param activity
	 * @param postcard
	 * @param com
	 * @param recName - parameter for debugging only, null can be passed
	 * @param taskCallback 
	 */
	public SendPostcardTask(HubActivity activity,  Bitmap postcard, String text, List<Comunication> com, String comType, Callback taskCallback){
		this.postcard = postcard;
		this.comList = com;
		this.comType = comType;
		this.activity = activity;
		this.taskCallback = taskCallback;
		this.text = text;
	}
	
	public SendPostcardTask(HubActivity activity,  Bitmap postcard, String text, Comunication com, String comType, Callback taskCallback){
		this.postcard = postcard;
		this.comList = new ArrayList<Comunication>();
		this.comList.add(com);
		this.comType = comType;		
		this.activity = activity;
		this.taskCallback = taskCallback;
		this.text = text;
	}
	
	private void postVk(final Bitmap bitmap, final String info) {
		final Account account = Account.getInstance();		
		new AsyncTask<Object,Object,Object>(){
			@Override
			protected Object doInBackground(Object... params) {
				VkApi vkApi = new VkApi(account.getVkontakteToken(), account.getVkontakteUserId());
				vkApi.postCartToFriendsWall(info, bitmap, text);
				return null;
			}
			
			protected void onPostExecute(Object result) {
				Log.w(TAG, "onPostExecute postVk"+ activity == null ? "null" : activity.toString());
				activity.proceedSending();
				if (taskCallback != null)
					taskCallback.handleMessage(null);
			}
		}.execute();		
	}

	private void postFacebook(Bitmap bitmap, final String comInfo) {
		SimpleFacebook mSimpleFacebook = SimpleFacebook.getInstance(activity);
		
		final SimpleFacebook.OnPublishListener callback = new SimpleFacebook.OnPublishListener() {
			
			@Override
			public void onFail(String reason) {
				Log.wtf(TAG, "onFail "+reason);					
			}
			
			@Override
			public void onException(Throwable throwable) {
				Log.e(TAG, "throwable " + throwable);					
			}
			
			@Override
			public void onThinking() {
				Log.v(TAG, "onThinking " );					
			}
			
			@Override
			public void onComplete(final String photoId) {

				new AsyncTask<Object,Object,Object>(){						
					@Override													
					protected Object doInBackground(Object... params) {																			
						FbApi fbApi = new FbApi();														
						Log.v(TAG, "postCartToFriendsWall "+ photoId);														
						fbApi.tagPerson(photoId, comInfo);														
						return null;													
					}	
					protected void onPostExecute(Object result) {
						activity.proceedSending();
						if (taskCallback != null)
							taskCallback.handleMessage(null);
					};
									
				}.execute();
			}
		};

		Photo photo = new Photo(bitmap);
		photo.addDescription(text);
		mSimpleFacebook.publish(photo, callback);		
	}	


	private void sendMms(Bitmap bitmap) {
		String[] APN_PROJECTION = {
			     Telephony.Carriers.TYPE,            // 0
			     Telephony.Carriers.MMSC,            // 1
			     Telephony.Carriers.MMSPROXY,        // 2
			     Telephony.Carriers.MMSPORT          // 3
			 };
		
		Cursor cursor = null;
//		if (Utils.hasICS()){
			try{
				Log.v(TAG, "Trying to reach apn settings");
			cursor = SqliteWrapper.query(activity, activity.getContentResolver(), Uri.withAppendedPath(Carriers.CONTENT_URI, "current"), APN_PROJECTION, null, null, null);
			Log.i(TAG, "success");
			sendMmsInBackground(cursor, bitmap);
			}catch (Exception e){
				Log.w(TAG, "reach apn settings first way fail");
				Log.d(TAG, "trying to reach apn settings other way");
				try{			
					cursor = activity.getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current"),
						null, null, null, null);
					sendMmsInBackground(cursor, bitmap);
					Log.i(TAG, "success");
				}catch (Exception e1){
					Log.w(TAG, "fail again");
					sendMmsViaIntent(bitmap);
					activity.stopMmsQueueAndTryIntent();
				}
			}
			Log.v(TAG, "Send message success");
	}

	
	private void sendMmsInBackground(Cursor cursor, final Bitmap bitmap) {
		
		Log.d(TAG, "cursor.getCount() "+cursor.getCount());
		Log.d(TAG, "cursor.getCount() "+cursor.getColumnCount());
		Log.d(TAG, "cursor.getCount() "+Arrays.toString(cursor.getColumnNames()));

		cursor.moveToLast();
		String type = cursor.getString(cursor.getColumnIndex(Telephony.Carriers.TYPE));
		String mmsc = cursor.getString(cursor.getColumnIndex(Telephony.Carriers.MMSC));
		String proxy = cursor.getString(cursor.getColumnIndex(Telephony.Carriers.MMSPROXY));
		String port = cursor.getString(cursor.getColumnIndex(Telephony.Carriers.MMSPORT));
		
		Log.v(TAG, "TYPE: "+type);
		Log.v(TAG, "MMSC: "+mmsc);
		Log.v(TAG, "MMSPROXY: "+proxy);
		Log.v(TAG, "MMSPORT: "+port);
		
		Settings sendSettings = new Settings();

		sendSettings.setMmsc(mmsc);
		sendSettings.setProxy(proxy);
		sendSettings.setPort(port);
		
		Handler handler = new Handler(){
			@Override
			public void handleMessage(android.os.Message msg) {
				if (msg.what == -1){		
					activity.clearQueue(); 
					sendMmsViaIntent(bitmap);
				} else
					activity.proceedSending();
				super.handleMessage(msg);
			}
		};
		
		Transaction sendTransaction = new Transaction(activity, sendSettings, handler);
		Transaction.NUM_RETRIES = 0;
		Message mMessage = new Message(text, getInfosArray());
		mMessage.setImage(bitmap);   // not necessary for voice or sms messages
		mMessage.setType(Message.TYPE_SMSMMS);  // could also be Message.TYPE_VOICE	
		
		sendTransaction.sendNewMessage(mMessage, Transaction.NO_THREAD_ID);
	}
	
	private void sendMmsViaIntent(Bitmap bitmap) {
		try{
			Log.v(TAG, "trying first way");
			sendMmsViaIntent(bitmap, "com.android.mms.ui.ComposeMessageActivity");
			Log.i(TAG, "success");
		}catch (Exception e){
			Log.v(TAG, "trying second way");
			try{
				sendMmsViaIntent(bitmap, "com.android.mms.ui.ConversationComposer");
				Log.i(TAG, "success");
			}catch (Exception e1){
				Toast.makeText(activity, activity.getString(R.string.str_error_sending_mms), Toast.LENGTH_SHORT).show();
//				activity.clearQueue(); //proceedSending();
			}								
		}
	}
	private void try2(Bitmap bitmap) {

		
	}

	private void sendMmsViaIntent(Bitmap bitmap, String activityName) {
//		activity.setPaused(true);
		String pathofBmp = Images.Media.insertImage(activity.getContentResolver(), bitmap,"title", null);
	    Uri bmpUri = Uri.parse(pathofBmp);
		
		Intent i = new Intent(Intent.ACTION_SEND);
		i.putExtra("address", getInfosArray());
		i.setClassName("com.android.mms", activityName);
		i.putExtra(Intent.EXTRA_STREAM, bmpUri);
		i.setType("image/png");
		i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivityForResult(i, Constants.REQUEST_SEND_MMS);//(i);			
	}
	

	
	private void sendMmsViaIntent2(Bitmap bitmap) {
		String pathofBmp = Images.Media.insertImage(activity.getContentResolver(), bitmap,"title", null);
	    Uri bmpUri = Uri.parse(pathofBmp);
		
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setType("vnd.android-dir/mms-sms");
		smsIntent.putExtra("address", getInfosArray());
		smsIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
		
		activity.startActivityForResult(smsIntent, Constants.REQUEST_SEND_MMS);
	}
	
	private void sendEmail(Bitmap bitmap) {
		Log.d(TAG , "COMUNICATION_TYPE_EMAIL");
		String pathofBmp = Images.Media.insertImage(activity.getContentResolver(), bitmap,"test", null);
	    Uri bmpUri = Uri.parse(pathofBmp);
		
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		// The intent does not have a URI, so declare the "text/plain" MIME type
		emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
		emailIntent.putExtra(Intent.EXTRA_EMAIL, getInfosArray()); // recipients
//		emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.str_email_subject));
		emailIntent.putExtra(Intent.EXTRA_TEXT, text);
		emailIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
		// You can also attach multiple items by passing an ArrayList of Uris
	    try {
	    	activity.startActivityForResult(emailIntent, Constants.REQUEST_SEND_EMAIL);//(emailIntent);
	    } catch (android.content.ActivityNotFoundException ex) {
	     Toast.makeText(activity, "There is no email application installed.", Toast.LENGTH_SHORT).show();
	    }		
	}

	private String[] getInfosArray() {
		String[] infosArray = new String[comList.size()];
		for (int i = 0; i < comList.size(); i++)
			infosArray[i] = comList.get(i).getInfo();
		return infosArray;
	}

	public void execute() {		
		Log.d(TAG, "execute for com "+comList);
		if (activity != null){
		Log.v(TAG, "EXECUTE for "+" comType "+comType);
		if (Constants.COMUNICATION_TYPE_EMAIL.equals(comType)){
			sendEmail(postcard);
		}
		if (Constants.COMUNICATION_TYPE_FB.equals(comType)){
			if (comList.size() > 1)
				Log.e(TAG, "comList.size()  >1 comList.size() = "+comList.size());
			postFacebook(postcard, comList.get(0).getInfo());			
		}
		if (Constants.COMUNICATION_TYPE_VK.equals(comType)){
			if (comList.size() > 1)
				Log.e(TAG, "comList.size()  >1 comList.size() = "+comList.size());
			postVk(postcard, comList.get(0).getInfo());					
		}		
		if (Constants.COMUNICATION_TYPE_PHONE.equals(comType)){
				sendMms(postcard);				
		}
		} else Log.wtf(TAG, "WTF!!! "+ this+" has empty activity!");		
	}
	
//	private int getPriority(){
//		if (Constants.COMUNICATION_TYPE_EMAIL.equals(com.getType())){
//			return 2;
//		}
//		if (Constants.COMUNICATION_TYPE_FB.equals(com.getType())){
//			return 1;		
//		}
//		if (Constants.COMUNICATION_TYPE_VK.equals(com.getType())){
//			return 0;	
//		}
//		if (Constants.COMUNICATION_TYPE_PHONE.equals(com.getType())){
//			return 3;			
//		}
//		return -1;
//	}

	@Override
	public int compareTo(SendPostcardTask another) {
		return 0;//this.getPriority() - another.getPriority();
	}

	public String toString(){
		return comType+" comListSize "+comList.size()+" comList.get(0) "+comList.get(0);
	}	
}
