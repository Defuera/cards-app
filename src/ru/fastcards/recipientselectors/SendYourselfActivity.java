package ru.fastcards.recipientselectors;

import ru.fastcards.ListContacts;
import ru.fastcards.R;
import ru.fastcards.common.Comunication;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.manager.ManageGroupActivity;
import ru.fastcards.social.api.VkLoginActivity;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;

public class SendYourselfActivity extends SelectorActivity {
	private CompoundButton sendYourselfTrigger;

	private Account account = Account.getInstance();

	private Context context;
	private ImageView contactInfoIv;
	private TextView contactInfoTv;
	private LinearLayout sendYourselfContactView;
	
	@Override
	public void onItemRemove() {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		this.context = this;
	}
	
	protected OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			ISendableItem item = itemsList.get(position - 1);
			if (item.isGroup())
				startPreviewListActivity(((ListContacts) item), Constants.FLAG_MODIFY_TEMPORARY, position - 1);
		}
	};
	
	void startPreviewListActivity(ListContacts group, String flag, int position) {
		Intent intent = new Intent(this, PreviewListActivity.class);
		intent.putExtra(Constants.EXTRA_ID, group.getId());
		startActivity(intent);
	}
	
	/**
	 * Creates send yourself header view.
	 * @return header view
	 */
	protected View getHeaderView(){		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View header = inflater.inflate(R.layout.header_recipient_selector, null);
		
		contactInfoIv = (ImageView) header.findViewById(R.id.iv_contact_icon);		
		contactInfoTv = (TextView) header.findViewById(R.id.tv_recipient_name);
		sendYourselfContactView = (LinearLayout) header.findViewById(R.id.ll_contact_info);

		Comunication com = account.getPrimaryComunication(context);
		setInfoView(com == null? null : com.getType());
		
		if (Utils.hasHoneycomb()) {
			sendYourselfTrigger = (Switch) header.findViewById(R.id.toogle_send_myself);
		} else {
			sendYourselfTrigger = (CheckBox) header.findViewById(R.id.toogle_send_myself);
		}
		sendYourselfTrigger.setOnCheckedChangeListener(onSendYourselfClickListener);
		return header;
	}	

	private void setInfoView(String primaryContact) {
		contactInfoIv.setImageBitmap(getContactInfoBitmap(primaryContact));		
		contactInfoTv.setText(getSendYourselfText(primaryContact));				
		sendYourselfContactView.setOnClickListener(new OnUserContactInfoClickListener(context, contactInfoIv, contactInfoTv, primaryContact));		
	}

	private OnCheckedChangeListener onSendYourselfClickListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
		{
			if (!sendYourselfTrigger.isChecked()){
				
			}else{
				if (account.isLoggedInFb()){
					setInfoView(Constants.COMUNICATION_TYPE_FB);
					sendYourselfTrigger.setChecked(true);
				}else if(account.isLoggedInVk()){
					setInfoView(Constants.COMUNICATION_TYPE_VK);
					sendYourselfTrigger.setChecked(true);					
				}else{
					sendYourselfTrigger.setChecked(false);
					createLoginSocialDialog();
				}
			}			
		}
	};
	
//	private boolean isLoggedInSomewere() {
//		return account.isLoggedInFb() || account.isLoggedInVk();
//	}
	
	private void createLoginSocialDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(getString(R.string.str_auth_sn_to_sendyourself));
		
		builder.setPositiveButton(getString(R.string.str_vkontakte), vkAuthClickListener);
		builder.setNeutralButton(getString(R.string.str_facebook), fbAuthClickListener);
		builder.setNegativeButton(getString(R.string.str_cancel), cancelClickListener);
		
		builder.create().show();
	}	
	
	private android.content.DialogInterface.OnClickListener vkAuthClickListener = new android.content.DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent intent = new Intent(context, VkLoginActivity.class);
			startActivityForResult(intent, Constants.REQUEST_VK_AUTH_SENDYOURSELF);
		}		
	};
	
	private android.content.DialogInterface.OnClickListener fbAuthClickListener = new android.content.DialogInterface.OnClickListener(){;

		@Override
		public void onClick(DialogInterface dialog, int which) {
			SimpleFacebook.getInstance(((Activity) context)).login(facebookAuthCallback);	
		}		
	};
	
	private android.content.DialogInterface.OnClickListener cancelClickListener = new android.content.DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}		
	};

	private CharSequence getSendYourselfText(String primaryContact) {
		if (Constants.COMUNICATION_TYPE_VK.equals(primaryContact) && account.isLoggedInVk()){
			return getString(R.string.str_post_vk);
		}
		if (Constants.COMUNICATION_TYPE_FB.equals(primaryContact) && account.isLoggedInFb()){
			return getString(R.string.str_post_fb);			
		}
		return getString(R.string.str_authorize);
	}


	private Bitmap getContactInfoBitmap(String comType) {
		System.out.println("getContactInfoBitmap "+ comType);
		Bitmap bmp = null;
		if (Constants.COMUNICATION_TYPE_VK.equals(comType) && account.isLoggedInVk()){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_vk_medium);
		}

		if (Constants.COMUNICATION_TYPE_FB.equals(comType) && account.isLoggedInFb()){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_fb_medium);
			
		}
		return bmp;
	}
	
	public boolean isSendYourself(){	
		return sendYourselfTrigger.isChecked();
	}
	

	private OnLoginListener facebookAuthCallback = new OnLoginListener() {
		
		@Override
		public void onFail(String reason) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onException(Throwable throwable) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onThinking() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onNotAcceptingPermissions() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLogin() {
			account.setPrimaryContact(context, Constants.COMUNICATION_TYPE_FB);
			setInfoView(account.getPrimaryComunication(context).getType());
			sendYourselfTrigger.setChecked(true);
			
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    SimpleFacebook.getInstance().onActivityResult(this, requestCode, resultCode, data); 
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_VK_AUTH_SENDYOURSELF: {
				account.setPrimaryContact(context, Constants.COMUNICATION_TYPE_VK);
				setInfoView(account.getPrimaryComunication(context).getType());
				sendYourselfTrigger.setChecked(true);
			}
				break;
			case Constants.REQUEST_MODIFY_GROUP: {
//				modifyGroup(data);
			}
				break;
			default:
				break;
			}
		}
	}
	
}
