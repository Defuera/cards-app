package ru.fastcards.profile;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.SocialNetworkItem;
import ru.fastcards.social.api.VkLoginActivity;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;
import com.sromku.simple.fb.SimpleFacebook.OnLogoutListener;

public class SocialNetworkActivity extends ActionBarActivity {

	private final String TAG = "SocialNetworkActivity";
	private Context context;
	private Account account = Account.getInstance();
	private List<SocialNetworkItem> networksList = new ArrayList<SocialNetworkItem>();
	private ListView listView;
	private SocialNetworksAdapter adapter;
	private AlertDialog logOutDialog;
	private SimpleFacebook mSimpleFacebook = SimpleFacebook.getInstance(this);
	private DataBaseHelper dbHelper;

	@Override
	public void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		// mSimpleFacebook.
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_network);

		context = this;
		dbHelper = DataBaseHelper.getInstance(context);

		createNetworksList();
		setAdapter();
	}

	private void createNetworksList() {
		networksList.add(new SocialNetworkItem(getString(R.string.str_vkontakte), account.getVkontakteUserName(context), R.drawable.btn_vk));
		networksList.add(new SocialNetworkItem(getString(R.string.str_facebook), account.getFbUserName(context), R.drawable.btn_fb));
	}

	private void recreateNetworkList() {
		networksList.clear();
		createNetworksList();
		adapter.notifyDataSetChanged();
	}

	private void setAdapter() {

		listView = (ListView) findViewById(R.id.social_networks_list);
		adapter = new SocialNetworksAdapter(context, networksList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				createDialog(networksList.get(position));
			}
		});
	}

	private void createDialog(final SocialNetworkItem network) {
		boolean loggedIn = false;

		if (getString(R.string.str_vkontakte).equals(network.getTitle())) {
			loggedIn = account.isLoggedInVk();

		}
		if (getString(R.string.str_facebook).equals(network.getTitle())) {
			loggedIn = account.isLoggedInFb();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getString(loggedIn ? R.string.str_title_log_out : R.string.str_title_log_in));
		builder.setMessage(getString(loggedIn ? R.string.str_title_log_out : R.string.str_title_log_in) + " " + network.getTitle());

		builder.setPositiveButton(R.string.str_ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (getString(R.string.str_vkontakte).equals(network.getTitle())) {
					if (account.isLoggedInVk())
						logOutVk();
					else
						logInVk();
				}
				if (getString(R.string.str_facebook).equals(network.getTitle())) {
					if (account.isLoggedInFb())
						logOutFb();
					else
						logInFb();
				}
			}

		});

		builder.setNegativeButton(R.string.str_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});

		builder.create().show();
	}

	private void logOutVk() {
		account.removeVkontakteToken(this);
		recreateNetworkList();

		Utils.deleteRecFromDataBase(context, Constants.COMUNICATION_TYPE_VK, null);
	}

	private void logOutFb() {
		mSimpleFacebook.logout(onLogoutListener);
		recreateNetworkList();
		Utils.deleteRecFromDataBase(context, Constants.COMUNICATION_TYPE_FB, null);
	}

	OnLogoutListener onLogoutListener = new SimpleFacebook.OnLogoutListener() {

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
			// show progress bar or something to the user while login is happening
			Log.i(TAG, "In progress");
		}

		@Override
		public void onLogout() {
			Log.i(TAG, "You are logged out");
			recreateNetworkList();
		}

	};

	private void logInVk() {
		Intent intent = new Intent(context, VkLoginActivity.class);
		startActivityForResult(intent, Constants.REQUEST_VK_AUTH);
	}

	private void logInFb() {
		mSimpleFacebook.login(onFbLoginListener);
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
			// account.save(context);
			Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_FB, null);
			recreateNetworkList();
		}

		@Override
		public void onNotAcceptingPermissions() {
			Log.w(TAG, "User didn't accept read permissions");
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_test: {
			// dbHelper.deleteRecipientsByComunicationType(Constants.COMUNICATION_TYPE_FB_ID);
			Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_VK, null);
			return true;
		}
		case android.R.id.home:
			finish();
			return true;
		default: {
		}
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		SimpleFacebook.getInstance().onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_VK_AUTH: {
				Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_VK, null);
				recreateNetworkList();
			}
				break;
			default:
				break;
			}
		}

	}

}
