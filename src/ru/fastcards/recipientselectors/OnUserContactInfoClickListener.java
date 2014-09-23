package ru.fastcards.recipientselectors;

import java.util.ArrayList;
import java.util.List;

import com.sromku.simple.fb.SimpleFacebook;

import ru.fastcards.R;
import ru.fastcards.common.Comunication;
import ru.fastcards.social.api.VkLoginActivity;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class OnUserContactInfoClickListener implements OnClickListener {



			PopupWindow puw;
			private TextView contactInfoTv;
			private ImageView contactInfoIv;
			private String primaryContact;
			private Context context;
			private Account account = Account.getInstance();
			List<Comunication> comList = new ArrayList<Comunication>();

			public OnUserContactInfoClickListener(Context context, ImageView contactInfoIv,	TextView contactInfoTv, String primaryContact) {
				this.contactInfoTv = contactInfoTv;
				this.contactInfoIv = contactInfoIv;
				this.primaryContact = primaryContact;
				this.context = context;
				setConList();
			}

			private void setConList() {
				comList.add(new Comunication(null, null, Constants.COMUNICATION_TYPE_VK, null));
				comList.add(new Comunication(null, null, Constants.COMUNICATION_TYPE_FB, null));				
			}

			@Override
			public void onClick(View view) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				LinearLayout  contentView = new LinearLayout(context);
				contentView.setOrientation(LinearLayout.VERTICAL);
				contentView.setPadding(20, 20, 20, 20);
								
				for (final Comunication comunication : comList){
					final String comType = comunication.getType();
					LinearLayout innerView = (LinearLayout) inflater.inflate(R.layout.raw_contact_info, null);
					innerView.setClickable(true);
					final TextView textView = (TextView) innerView.findViewById(R.id.tv_info);
					String info = comunication.getInfo();

					textView.setText(getInfoText(comType));
					
					System.out.println("info "+info);
					
					ImageView image = (ImageView) innerView.findViewById(R.id.iv_icon);
					image.setImageBitmap(getContactInfoBitmap(comType));
					
					innerView.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							if (!authIsNessesary(comType)){
								contactInfoIv.setImageBitmap(getContactInfoBitmap(comType));
								contactInfoTv.setText(textView.getText());
							}
							account.setPrimaryContact(context, comType);
							puw.dismiss();
						}

					});
					contentView.addView(innerView);
				}

				puw = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
				puw.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.abc_ab_solid_dark_holo));
				puw.showAsDropDown(view);
				
			}
			
			/**
			 * Checks if user is logged in to chosen social network and if not starts log in session to current sn.
			 * @param comType 
			 */
			private boolean authIsNessesary(String comType) {
				boolean needToAuth = false;
				if (Constants.COMUNICATION_TYPE_VK.equals(comType) && !account.isLoggedInVk()){
					startVkLoginActivity();
					needToAuth = true;
				}
				if (Constants.COMUNICATION_TYPE_FB.equals(comType) && !account.isLoggedInFb()){	
					SimpleFacebook.getInstance(((Activity) context)).login(null);	
					needToAuth = true;
				}
				return needToAuth;
			}
			
			private void startVkLoginActivity() {
				Intent intent = new Intent(context, VkLoginActivity.class);
				((Activity) context).startActivityForResult(intent, Constants.REQUEST_VK_AUTH_SENDYOURSELF);
			}
			
			private CharSequence getInfoText(String comType) {
				if (Constants.COMUNICATION_TYPE_VK.equals(comType)){
					if (account.isLoggedInVk()){
						return context.getString(R.string.str_post_vk);
					}else{
						return context.getString(R.string.str_auth_vk);
					}
				}
				if (Constants.COMUNICATION_TYPE_FB.equals(comType)){
					if (account.isLoggedInFb()){
						return context.getString(R.string.str_post_fb);
					}else{
						return context.getString(R.string.str_auth_fb);
					}
				}
				return null;
			}
		
			private Bitmap getContactInfoBitmap(String comType) {
				System.out.println("getContactInfoBitmap "+ comType);
				Bitmap bmp = null;
				if (Constants.COMUNICATION_TYPE_VK.equals(comType)){
					return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_vk_small);
				}

				if (Constants.COMUNICATION_TYPE_FB.equals(comType)){
					return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_fb_small);
					
				}
				return bmp;
			}

}
