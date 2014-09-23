package ru.fastcards.recipientselectors;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Comunication;
import ru.fastcards.common.Recipient;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.content.Context;
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

public class OnContactInfoClickListener implements OnClickListener{
	
//	public OnContactInfoClickListener(final Recipient item, final List<Comunication> comList, final ImageView contactInfoIv, final TextView nameTextView){
//		
//	}
	PopupWindow puw;
	private Context context;
	private Recipient item;
	private ImageView contactInfoIv;
	private TextView nameTextView;
	private List<Comunication> comList;

	public OnContactInfoClickListener(Context context, Recipient item, List<Comunication> comList, ImageView contactInfoIv, TextView nameTextView) {
//		return new OnClickListener() {
		this.context = context;
		this.item = item;
		this.comList= comList;
		this.contactInfoIv = contactInfoIv;
		this.nameTextView = nameTextView;
//		@Override
//		public void onClick(View v) {
	}
	
	public OnContactInfoClickListener(ImageView contactInfoIv2, TextView contactInfoTv, String primaryContact) {
		// TODO Auto-generated constructor stub
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

			textView.setText(comType == Constants.COMUNICATION_TYPE_PHONE ? 
					info :
					getContactInfoText(comunication, item.getName()));
			
			System.out.println("info "+info);
			
			ImageView image = (ImageView) innerView.findViewById(R.id.iv_icon);
			image.setImageBitmap(getContactInfoBitmap(comType));
			
			innerView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					setPrimaryContactInfo(comunication, item.getId());
					contactInfoIv.setImageBitmap(getContactInfoBitmap(comType));
					nameTextView.setText(textView.getText());
					dismissPopUp();
				}
			});
			contentView.addView(innerView);
		}

		puw = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		puw.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.abc_ab_solid_dark_holo));
		puw.showAsDropDown(view);
		
	}
	
	private void dismissPopUp() {
		puw.dismiss();
		
	}
		
	private void setPrimaryContactInfo(Comunication comunication, String recipientId) {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		dbHelper.makeComunicationPrimary(comunication.getUuid(), recipientId);		
	}
	

	private Bitmap getContactInfoBitmap(String comType) {
		System.out.println("getContactInfoBitmap "+ comType);
		Bitmap bmp = null;
		if (comType.equals(Constants.COMUNICATION_TYPE_VK)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_vk_small);
		}
		if (comType.equals(Constants.COMUNICATION_TYPE_PHONE)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_tel_book_small);
			
		}
		if (comType.equals(Constants.COMUNICATION_TYPE_EMAIL)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_email_small);
			
		}		
		if (comType.equals(Constants.COMUNICATION_TYPE_FB)){
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_fb_small);
			
		}
//		if (comType.equals(Constants.COMUNICATION_TYPE_CONTACTS_ID)){
//			return BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_contacts_small);
//			
//		}
		
		return bmp;
	}
	
	private String getContactInfoText(Comunication comunication, String name) {
		String comType = comunication.getType();
		if (comType.equals(Constants.COMUNICATION_TYPE_VK) || comType.equals(Constants.COMUNICATION_TYPE_FB))	
			return name;
		else
			return comunication.getInfo();
	}
	
}
