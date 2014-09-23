package ru.fastcards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import ru.fastcards.common.Comunication;
import ru.fastcards.common.Recipient;
import ru.fastcards.recipientselectors.SeceltMultipleContactsActivity;
import ru.fastcards.send.SendPostcardTask;
import ru.fastcards.social.api.VkLoginActivity;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.ImageManager;
import ru.fastcards.utils.Utils;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;

public class HubActivity extends TrackedActivity {
	private static final String TAG = "HubActivity";
	private Context context;

	private Account account;
	private SimpleFacebook mSimpleFacebook = SimpleFacebook.getInstance(this);

	private Bitmap postcardBitmap;

	private PriorityQueue<SendPostcardTask> sendQueue;
	// private ArrayList<String> idsList;
	private ProgressDialog progressDialog;
	private DataBaseHelper dbHelper;
	private String text;

	@Override
	public void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hub);
		context = this;
		account = Account.getInstance();
		dbHelper = DataBaseHelper.getInstance(context);
		getExtras();
	}

	private void getExtras() {
		Intent intent = getIntent();
		text = intent.getStringExtra(Constants.POSTCARD_TEXT);

		String postcardUrl = intent.getStringExtra(Constants.POSTCARD_FRONT_IMAGE);
		//
		ImageManager manager = new ImageManager(context);
		String filename = manager.createFileNameFromUrl(postcardUrl);
		postcardBitmap = manager.decodeBitmapFromFile(filename);

		Log.d(TAG, "text " + text);
		Log.d(TAG, "postcardBytes " + postcardUrl);

	}

	public void onPostInstagrammButtonClick(View v) {
		Log.d(TAG, "onPostInstagrammButtonClick postcardBitmap " + postcardBitmap);
		String pathofBmp = Images.Media.insertImage(getContentResolver(), postcardBitmap, "test", null);

		if (pathofBmp != null) {
			Uri bmpUri = Uri.parse(pathofBmp);

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");
			intent.putExtra(Intent.EXTRA_STREAM, bmpUri);

			PackageManager packManager = getPackageManager();
			List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

			boolean resolved = false;
			for (ResolveInfo resolveInfo : resolvedInfoList) {
				if (resolveInfo.activityInfo.packageName.startsWith("com.instagram.android")) {
					intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
					resolved = true;
					break;
				}
			}
			if (resolved) {
				startActivity(intent);
			} else {
				Toast.makeText(this, "Instagram app isn't found", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void onPostTwitterButtonClick(View v) {
		String pathofBmp = Images.Media.insertImage(getContentResolver(), postcardBitmap, "test", null);
		Uri bmpUri = Uri.parse(pathofBmp);

		Intent tweetIntent = new Intent(Intent.ACTION_SEND);
		tweetIntent.putExtra(Intent.EXTRA_TEXT, "");
		tweetIntent.setType("text/plain");
		tweetIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);

		PackageManager packManager = getPackageManager();
		List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

		boolean resolved = false;
		for (ResolveInfo resolveInfo : resolvedInfoList) {
			if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
				tweetIntent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
				resolved = true;
				break;
			}
		}
		if (resolved) {
			startActivity(tweetIntent);
		} else {
			Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show();
		}
	}

	public void onPostFbButtonClick(View v) {
		if (account.hasFbToken()) {
			String comType = Constants.COMUNICATION_TYPE_FB;
			Comunication com = new Comunication("0", "0", comType, "");
			SendPostcardTask task = new SendPostcardTask(this, postcardBitmap, text, com, comType, getCallback());
			task.execute();
		} else {
			startFbLoginActivity(false);
		}
	}

	public void onPostVkButtonClick(View v) {
		if (account.hasVkToken()) {
			String comType = Constants.COMUNICATION_TYPE_VK;
			Comunication com = new Comunication("0", "0", comType, Account.getInstance().getVkontakteUserId());
			SendPostcardTask task = new SendPostcardTask(this, postcardBitmap, text, com, comType, getCallback());
			task.execute();
		} else {
			startVkLoginActivity(false);
		}
	}

	private Handler.Callback getCallback() {
		final ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(getString(R.string.str_sending));
		dialog.setMessage(getString(R.string.str_please_wait));
		dialog.show();
		Handler.Callback callback = new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				dialog.dismiss();
				Toast.makeText(context, context.getString(R.string.str_sending_successfull), Toast.LENGTH_SHORT).show();
				return false;
			}
		};
		return callback;
	}

	public void OnSendFbClick(View v) {
		if (account.isLoggedInFb()) {
			startSocialSelectorActivity(Constants.REQUEST_FB_RECIPIENTS);
		} else
			startFbLoginActivity(true);
	}

	private void startFbLoginActivity(boolean isLaunchRecSelector) {
		if (isLaunchRecSelector)
			mSimpleFacebook.login(onFbLoginListener);
		else
			mSimpleFacebook.login(null);
	}

	OnLoginListener onFbLoginListener = new SimpleFacebook.OnLoginListener() {

		@Override
		public void onFail(String reason) {
			Log.w(TAG, reason);
		}

		@Override
		public void onException(Throwable throwable) {
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			Log.i(TAG, "In progress");
		}

		@Override
		public void onLogin() {
			Log.i(TAG, "Logged in with token ~~~!" + mSimpleFacebook.getAccessToken() + "!~~~");
			// account.save(context);
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					startSocialSelectorActivity(Constants.REQUEST_FB_RECIPIENTS);
					super.handleMessage(msg);
				}
			};
			Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_FB, handler);
		}

		@Override
		public void onNotAcceptingPermissions() {
			Log.w(TAG, "User didn't accept read permissions");
		}

	};
	private boolean isLaunchVkRecSelectorActivity;

	public void OnSendVkClick(View v) {
		if (account.isLoggedInVk()) {
			startSocialSelectorActivity(Constants.REQUEST_VK_RECIPIENTS);
		} else
			startVkLoginActivity(true);
	}

	private void startVkLoginActivity(boolean isLaunchVkRecSelectorActivity) {
		this.isLaunchVkRecSelectorActivity = isLaunchVkRecSelectorActivity;
		Intent intent = new Intent(context, VkLoginActivity.class);
		startActivityForResult(intent, Constants.REQUEST_VK_AUTH);
	}

	private void startSocialSelectorActivity(int requestCode) {
		Intent intent = new Intent(context, SeceltMultipleContactsActivity.class);
		String comType = null;
		switch (requestCode) {
		case Constants.REQUEST_VK_RECIPIENTS:
			comType = Constants.COMUNICATION_TYPE_VK;
			break;
		case Constants.REQUEST_FB_RECIPIENTS:
			comType = Constants.COMUNICATION_TYPE_FB;
			break;
		default:
			break;
		}
		Log.i(TAG, "startSocialSelectorActivity " + comType + " req 111, 122? " + requestCode);
		intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE, comType);
		startActivityForResult(intent, requestCode);
	}

	public void OnSendMailClick(View v) {
		Intent intent = new Intent(context, SeceltMultipleContactsActivity.class);
		intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE, Constants.COMUNICATION_TYPE_EMAIL);
		startActivityForResult(intent, Constants.REQUEST_CONTACTS_EMAIL);
	}

	public void OnSendMessageClick(View v) {
		Intent intent = new Intent(context, SeceltMultipleContactsActivity.class);
		intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE, Constants.COMUNICATION_TYPE_PHONE);
		startActivityForResult(intent, Constants.REQUEST_CONTACTS_MSG);
	}

	public void startSendingSingle(List<Recipient> recipientsList, String comType) {
		createProgressDialog();
		createSendTaskAndExecute(recipientsList, comType);
	}

	private void startSendingQueue(List<Recipient> recipientsList, String comType) {
		createProgressDialog();
		createSendTaskQueue(recipientsList, comType);
		proceedSending();
	}
	
	private void createSendTaskAndExecute(List<Recipient> recipientsList, String comType) {
		final int recipientsCount = recipientsList.size();
		Handler.Callback callback = new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				Toast.makeText(context, "Postcard successfully send to "+recipientsCount+" recipients", Toast.LENGTH_SHORT).show();
				return false;
			}
		}; 
		
		List<Comunication> comList = new ArrayList<Comunication>();
		for (Recipient rec : recipientsList)
			comList.add(dbHelper.getPrimaryComunication(rec.getId(), comType));
		SendPostcardTask task = new SendPostcardTask(this, postcardBitmap, text, comList, comType, callback);
		task.execute();
	}

	protected void createProgressDialog() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(getString(R.string.str_progress_sending_title));
		progressDialog.setMessage(getString(R.string.str_progress_sending_body));
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	private void createSendTaskQueue(List<Recipient> recipientsList, String comType) {
		// idsList = new ArrayList<String>();
		sendQueue = new PriorityQueue<SendPostcardTask>();

		for (Recipient rec : recipientsList) {
			addTaskToSendQueue(rec, comType);
			// idsList.add(rec.getId());
		}
	}

	private List<Recipient> getRecipients(Intent data, int origin) {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		String[] idsArray = data.getStringArrayExtra(Constants.EXTRA_RECIPIENTS_IDS);
		List<Recipient> recipientsList = new ArrayList<Recipient>();
		for (String id : idsArray)
			recipientsList.add(dbHelper.getRecipient(id));
		return recipientsList;
	}

	public void proceedSending() {
		Log.v(TAG, "proceedSending");
		if (sendQueue != null && !sendQueue.isEmpty())
			sendQueue.poll().execute();
		else {
			if (progressDialog != null)
				progressDialog.dismiss();
			// startFinishSendingActivity(idsList);
		}
	};

	public void failedSending() {
		Toast.makeText(context, getString(R.string.str_error_sending_mms), Toast.LENGTH_SHORT).show();
		progressDialog.dismiss();
		sendQueue.clear();
	}

	private void addTaskToSendQueue(Recipient rec, String comType) {
		Comunication com = dbHelper.getPrimaryComunication(rec.getId(), comType);
		sendQueue.add(new SendPostcardTask(this, postcardBitmap, text, com, comType, null));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		SimpleFacebook.getInstance().onActivityResult(this, requestCode, resultCode, data);
		// super.onActivityResult(requestCode, resultCode, data);
		if (progressDialog != null)
			progressDialog.dismiss();
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_VK_AUTH: {
				if (isLaunchVkRecSelectorActivity) {
					Handler handler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							startSocialSelectorActivity(Constants.REQUEST_VK_RECIPIENTS);
							super.handleMessage(msg);
						}
					};
					Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_VK, handler);
				}
			}
				break;
			case Constants.REQUEST_VK_RECIPIENTS: {
				if (data != null) {
					List<Recipient> recipientsList = getRecipients(data, Constants.ORIGIN_VK);
					if (!recipientsList.isEmpty() && data != null)
						startSendingQueue(recipientsList, Constants.COMUNICATION_TYPE_VK);
				}
			}
				break;
			case Constants.REQUEST_FB_RECIPIENTS: {
				if (data != null) {
					List<Recipient> recipientsList = getRecipients(data, Constants.ORIGIN_FB);
					if (!recipientsList.isEmpty() && data != null)
						startSendingQueue(recipientsList, Constants.COMUNICATION_TYPE_FB);
				}
			}
				break;
			case Constants.REQUEST_CONTACTS_EMAIL: {
				if (data != null) {
					List<Recipient> recipientsList = getRecipients(data, Constants.ORIGIN_CONTACTS);
					if (!recipientsList.isEmpty() && data != null)
						startSendingSingle(recipientsList, Constants.COMUNICATION_TYPE_EMAIL);
				}
			}
				break;
			case Constants.REQUEST_CONTACTS_MSG: {
				if (data != null) {
					List<Recipient> recipientsList = getRecipients(data, Constants.ORIGIN_CONTACTS);
					if (!recipientsList.isEmpty()){
//						if (Utils.hasKitKat())
							startSendingQueue(recipientsList, Constants.COMUNICATION_TYPE_PHONE);
//						else 
//							startSendingSingle(recipientsList, Constants.COMUNICATION_TYPE_PHONE);
					}
				}
			}
				break;
			}
		}
	}

	public void stopMmsQueueAndTryIntent() {
		
	}

	public void clearQueue() {
		sendQueue.clear();
		
	}

}
