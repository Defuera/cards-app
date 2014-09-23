package ru.fastcards.shop;

import ru.fastcards.R;
import ru.fastcards.common.Theme;
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

public class ThemeDialogFragment extends PreviewDialogFragment{
	
	
private View content;
private Context context;
private Builder alert;
private Theme theme;
private ThemeCreator themeCreator;
private Button ok,cancel;
private TextView balance;
private LinearLayout progress;
private TextView notificationText;

public ThemeDialogFragment() {
	// TODO Auto-generated constructor stub
}

public static ThemeDialogFragment newInstance(Context context,Theme theme){
	ThemeDialogFragment fragment=new ThemeDialogFragment();
	fragment.theme=theme;
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
	title_text.setText(theme.getName());
	
	if (theme.isBought()||theme.getPrice()==0){
		content.findViewById(R.id.price).setVisibility(View.INVISIBLE);
//		content.findViewById(R.id.star_substrate).setVisibility(View.INVISIBLE);
	}
	
	balance=(TextView) content.findViewById(R.id.tv_money);
	balance.setText(""+(int)theme.getPrice());
}

private void createContent(){
	LinearLayout content_frame=(LinearLayout) content.findViewById(R.id.content_frame);
	progress=(LinearLayout) content.findViewById(R.id.progress_layout);
	notificationText=(TextView) content.findViewById(R.id.notification_text);
	
	content_frame.setLayoutParams(getContentParams());
	themeCreator=new ThemeCreator(context,getContentParams().height/3,getContentParams().height*2/3);
	content_frame.addView(themeCreator.createTheme(theme,callback));
}

private void setDialogButtons(){
	ok=(Button) content.findViewById(R.id.button_ok);
	ok.setEnabled(false);
	cancel=(Button) content.findViewById(R.id.button_cancel);
	cancel.setText(getResources().getString(R.string.str_cancel));
	cancel.setOnClickListener(getOnCancelClickListener());
	
	if (theme.isBought()||theme.getPrice()==0){
		ok.setText(getResources().getString(R.string.str_choose));
		ok.setOnClickListener(getOnChooseClickListener());}
	else {
		ok.setText(getResources().getString(R.string.str_buy));
		ok.setOnClickListener(getOnBuyClickListener());
	}
}

public void refreshDialogButtons(){
	ok.setText(getResources().getString(R.string.str_choose));
	ok.setOnClickListener(getOnChooseClickListener());
	content.findViewById(R.id.price).setVisibility(View.INVISIBLE);
}

public int getSelectedPostcard(){
	return themeCreator.getSelectedPostcard();
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
				notificationText.setText(context.getResources().getString(R.string.load_previevs));
			}	
			else {
				Toast.makeText(context, getResources().getString(R.string.server_connecting_error), Toast.LENGTH_SHORT).show();
				dismiss();
			}
		}
		else if (messageCounter==5) ok.setEnabled(true);
	}
};

}
