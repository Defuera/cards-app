package ru.fastcards.recipientselectors;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Comunication;
import ru.fastcards.common.Recipient;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.ImageManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RecipientViewInflater {

	private static final String TAG = "RecipientViewInflater";
	private Context context;
	private ImageManager manager;
	private Resources res;
	private DataBaseHelper dbhelper;
	private BaseAdapter adapter;


	public RecipientViewInflater(Context context, BaseAdapter adapter) {
//		super(context);
//		
//		this.inflate(context, R.layout.row_recipient, null);
		
		this.context = context;
		manager = new ImageManager(context);
		res = context.getResources();
		dbhelper = DataBaseHelper.getInstance(context);
		this.adapter = adapter;
		
//		getRecipientView(item);
	}
	

	public View getRecipientView(final Recipient item, View contentView) {
//		final Recipient item = getItem(position);
		List<Comunication> comList = dbhelper.getComunicationsList(item.getId(), null);		
		
		Comunication primary = comList.get(0);
		for (Comunication com : comList){
			if (com.isPrimaty()) primary = com;
		}
		
		int gender = item.getGender();
		
		Log.d(TAG, "contentView "+contentView);
		ImageView recipientIconIv = (ImageView) contentView.findViewById(R.id.iv_recipient_image);
		setItemBitmap(item, recipientIconIv, primary);
		
		ImageView contactInfoIv = (ImageView) contentView.findViewById(R.id.iv_contact_icon);
		contactInfoIv.setImageBitmap(getContactInfoBitmap(primary.getType()));
		
		TextView contactInfoTv = (TextView) contentView.findViewById(R.id.tv_recipient_name);
		contactInfoTv.setText(item.getName());//getContactInfoText(primary, item.getName()));

		LinearLayout infoLayout = (LinearLayout) contentView.findViewById(R.id.ll_contact_info);
		infoLayout.setOnClickListener(new OnContactInfoClickListener(context, item, comList, contactInfoIv, contactInfoTv));
		
		final EditText nickEditText = (EditText) contentView.findViewById(R.id.tv_recipient_name_modified);
		nickEditText.setText(item.getNickName() == null ? 
				item.getName():
				item.getNickName());
		nickEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d(TAG, "edittext has focus " + hasFocus);
				if (!hasFocus){
					String nick =  nickEditText.getText().toString();
					item.setNickName(nick);
					item.changeNickName(context, nick);
				}
			}
		});
		nickEditText.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE){
//					item.changeNickName(context, nickEditText.getText().toString());
					nickEditText.clearFocus();
					hideKeybord(nickEditText);
				}
				return false;
			}
		});

		TextView genderTextView = (TextView) contentView.findViewById(R.id.tv_recipient_gender);
		genderTextView.setText(defineGender(gender));
		genderTextView.setBackgroundColor(gender == 0 ?
				res.getColor(R.color.bg_text_gender_female):
				res.getColor(R.color.bg_text_gender_male));
		genderTextView.setOnClickListener(onGendeClickListener(item));

		return contentView;
	}
	
	private void hideKeybord(View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	class  MyTextWatcher implements TextWatcher{
		private Recipient rec;
		private EditText nickTextView;

		private MyTextWatcher(Recipient rec) {
			this.rec = rec;
//			this.nickTextView = nickTextView;
		}
		
	        public void afterTextChanged(Editable s) {
	        	rec.setNickName(s.toString());
	        	rec.changeNickName(context, s.toString());//NickName(s.toString());
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}	    
	}

	private OnClickListener onGendeClickListener(final Recipient item) {
		return new OnClickListener(){			
			@Override
			public void onClick(View v) {
				item.changeGender(context);
				System.out.println("onGenderClickListener");
				adapter.notifyDataSetChanged();
			}
			
		};
	}

	private Bitmap getContactInfoBitmap(String comType) {
		System.out.println("getContactInfoBitmap "+ comType);
		Bitmap bmp = null;
		if (comType.equals(Constants.COMUNICATION_TYPE_VK)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_vk_medium);
		}
		if (comType.equals(Constants.COMUNICATION_TYPE_PHONE)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_tel_book_medium);
			
		}
		if (comType.equals(Constants.COMUNICATION_TYPE_EMAIL)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_email_medium);
			
		}		
		if (comType.equals(Constants.COMUNICATION_TYPE_FB)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_fb_medium);
			
		}		
		return bmp;
	}


	private void setItemBitmap(Recipient item, ImageView imageView, Comunication comunication) {
		Bitmap bmp = null;
		
		String type = comunication.getType();
		if (!type.equals(Constants.COMUNICATION_TYPE_VK) && !type.equals(Constants.COMUNICATION_TYPE_FB)){
			if (item.getImageUri() != null){
				System.out.println("SET IMAGE TO RECIPIENT");
				bmp = manager.loadContactPhotoThumbnail(item.getImageUri());
				imageView.setImageBitmap(bmp);
				
			} 			
			if (bmp == null){
				bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile);
				imageView.setImageBitmap(bmp);
			}
		} else {
			if (item.getImageUri() != null) {
			BitmapLoaderAsyncTask bl = new BitmapLoaderAsyncTask(context, null, true, true);
			bl.loadImageAsync(item.getImageUri(), imageView, null);
		}
		}
	}

	private CharSequence defineGender(int gender) {
		switch (gender) {
		case 1:
			return "Male";
		case 0:
			return "Female";
		default:
			break;
		}
		return null;
	}
	
}
