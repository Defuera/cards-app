package ru.fastcards.shop;

import ru.fastcards.R;
import ru.fastcards.common.TextPack;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TextDialogFragment extends PreviewDialogFragment{
	
	private View content;
	private Context context;
	private TextPack textPack;
	private Builder alert;
	private Button ok;
	private Button cancel;
	private LinearLayout progress;
	
	
	public static TextDialogFragment newInstance(Context context,TextPack textPack){
		TextDialogFragment fragment=new TextDialogFragment();
		fragment.textPack=textPack;
		fragment.context=context;
		
		fragment.alert=new AlertDialog.Builder(context);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content=inflater.inflate(R.layout.shop_dialog_content, null);
		
		createLayoutParams();
		createTitle();
		createContent();
		alert.setView(content);
		
		setDialogButtons();
		
		return alert.create();
		
	}
	
	private void createTitle(){
		RelativeLayout title_frame=(RelativeLayout) content.findViewById(R.id.title_frame);
		title_frame.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		TextView title_text=(TextView) content.findViewById(R.id.title);
		title_text.setText(textPack.getName());
		
		if (textPack.isBought()||textPack.getPrice()==0){
			content.findViewById(R.id.price).setVisibility(View.INVISIBLE);
//			content.findViewById(R.id.star_substrate).setVisibility(View.INVISIBLE);
		}
		
		TextView balance=(TextView) content.findViewById(R.id.tv_money);
		balance.setText(""+(int)textPack.getPrice());
	}
	
	private void createContent(){
		LinearLayout content_frame=(LinearLayout) content.findViewById(R.id.content_frame);
		content_frame.setLayoutParams(getContentParams());
		progress=(LinearLayout) content.findViewById(R.id.progress_layout);
		TextCreator textCreator=new TextCreator(context, getContentParams().height/3,getContentParams().height*2/3);
		content_frame.addView(textCreator.createTextPack(textPack,callback));
	}
	
	private void setDialogButtons(){
		ok=(Button) content.findViewById(R.id.button_ok);
		ok.setText(getResources().getString(R.string.str_buy));
		ok.setOnClickListener(getOnBuyClickListener());
		cancel=(Button) content.findViewById(R.id.button_cancel);
		cancel.setText(getResources().getString(R.string.str_cancel));
		cancel.setOnClickListener(getOnCancelClickListener());
	}
	
	private Handler callback=new Handler(){
		private int messageCounter=0;
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			messageCounter++;
			
			if (messageCounter==1){
				if (msg.what==0) {
					progress.setVisibility(View.INVISIBLE);
				}	
				else {
					Toast.makeText(context, getResources().getString(R.string.server_connecting_error), Toast.LENGTH_SHORT).show();
					dismiss();
				}
			}
		}
	};
	
	
}
