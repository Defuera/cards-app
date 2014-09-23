package ru.fastcards.recipientselectors;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.social.api.FbApi;
import ru.fastcards.social.api.KException;
import ru.fastcards.social.api.VkLoginActivity;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.sromku.simple.fb.Permissions;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class AddContactsFragment extends Fragment implements OnClickListener  {

	protected static final String TAG = "AddContactsFragment";
	private View contentView;
	private Context context;
	private Account account = Account.getInstance();
	private SimpleFacebook mSimpleFacebook;
	private ImageButton groupsBtn;

    @Override
	public void onResume() {
	    super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
//        mSimpleFacebook.
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_add_contacts, container, false);

		context = getActivity();

		ImageButton vkBtn = (ImageButton) contentView.findViewById(R.id.btn_vk);
		vkBtn.setOnClickListener(this);
		ImageButton contactsBtn = (ImageButton) contentView.findViewById(R.id.btn_contacts);
		contactsBtn.setOnClickListener(this);
		ImageButton facebookBtn = (ImageButton) contentView.findViewById(R.id.btn_facebook);
		facebookBtn.setOnClickListener(this);
		groupsBtn = (ImageButton) contentView.findViewById(R.id.btn_groups);
		groupsBtn.setOnClickListener(this);
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		return contentView;
	}	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_vk: {
			if (account.isLoggedInVk()) {
				startSocialSelectorActivity(Constants.REQUEST_VK_RECIPIENTS);
			} else
				startVkLoginActivity();
		}
			break;
		case R.id.btn_contacts: {
			Intent intent = new Intent(context, SeceltMultipleContactsActivity.class);
			intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE,	Constants.COMUNICATION_TYPE_CONTACTS_ID);
			getActivity().startActivityForResult(intent, Constants.REQUEST_CONTACTS);
		}
			break;
		case R.id.btn_facebook: {
			if (account.isLoggedInFb()) {
				startSocialSelectorActivity(Constants.REQUEST_FB_RECIPIENTS);
			} else
				startFbLoginActivity();
		}
			break;
		case R.id.btn_groups: {
			startChooseGroupActivity();
		}
			break;
		default:
			break;
		}
	}
	
	private void startChooseGroupActivity() {
		Intent intent = new Intent(getActivity(), ChooseGroupActivity.class);
		getActivity().startActivityForResult(intent, Constants.REQUEST_RECIPIENTS_GROUP);
		
	}

	private void startFbLoginActivity() {		
		mSimpleFacebook.login(onFbLoginListener);		
	}
	
	OnLoginListener onFbLoginListener = new SimpleFacebook.OnLoginListener()
	{

	    @Override
	    public void onFail(String reason)
	    {
	        Log.w(TAG, reason);
	    }

	    @Override
	    public void onException(Throwable throwable)
	    {
	        Log.e(TAG, "Bad thing happened", throwable);
	    }

	    @Override
	    public void onThinking(){
	        Log.i(TAG, "In progress");
	    }

	    @Override
	    public void onLogin()
	    {
	        Log.i(TAG, "Logged in with token ~~~!"+mSimpleFacebook.getAccessToken()+"!~~~");
//	        account.save(context);
			Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					startSocialSelectorActivity(Constants.REQUEST_FB_RECIPIENTS);
					super.handleMessage(msg);
				}
			};
			Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_FB, handler);
	    }

	    @Override
	    public void onNotAcceptingPermissions()
	    {
	        Log.w(TAG, "User didn't accept read permissions");
	    }

	};

	private void startFbFriendSelectorActivity() {
		FbApi fbApi = new FbApi();
		try {
			fbApi.importFriendsToDataBase(context);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (KException e) {
			e.printStackTrace();
		}		
	}

	private void startSocialSelectorActivity(int requestCode) {
		Intent intent = new Intent(context,	SeceltMultipleContactsActivity.class);
		switch (requestCode) {
		case Constants.REQUEST_VK_RECIPIENTS:
			intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE,	Constants.COMUNICATION_TYPE_VK);
			break;
		case Constants.REQUEST_FB_RECIPIENTS:
			intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE,	Constants.COMUNICATION_TYPE_FB);			
			break;
		default:
			break;
		}		
		getActivity().startActivityForResult(intent, requestCode);
	}
	
	private void startVkLoginActivity() {
		Intent intent = new Intent(context, VkLoginActivity.class);
		startActivityForResult(intent, Constants.REQUEST_VK_AUTH);
	}

	public void hideGroups() {
		groupsBtn.setVisibility(View.GONE);
	}	
	
}
