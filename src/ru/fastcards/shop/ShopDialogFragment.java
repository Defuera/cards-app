package ru.fastcards.shop;

import ru.fastcards.PurseDialogFragment;
import ru.fastcards.R;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.common.Theme;
import ru.fastcards.utils.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class ShopDialogFragment extends PreviewDialogFragment{
	
	private Context context;
	private View content;
	private LayoutInflater inflater;
	private AlertDialog.Builder alert;
	private TextView title_text;
	
	private Article article;
	private String purchase_type;
	private ArticleLoader themeLoader;
	private ArticleLoader offerLoader;
	private Button ok;
	private Button cancel;
	private TextView balance;
	private LinearLayout progress;
	private TextView notificationText;
	
	
	public ShopDialogFragment() {
		// TODO Auto-generated constructor stub
	}
	
	public static ShopDialogFragment newInstance(Context context,Article article,String purchase_type){
		ShopDialogFragment fragment=new ShopDialogFragment();
		fragment.context=context;
		
		fragment.article=article;
		fragment.purchase_type=purchase_type;
		
		fragment.alert=new AlertDialog.Builder(context);
		
		fragment.inflater=(LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		return fragment;
	}

	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		alert.setCustomTitle(null);		
		createLayoutParams();		
		createDialog();		
		return alert.create();
	}

	private void createDialog(){
		
		content=inflater.inflate(R.layout.shop_dialog_content, null);
		RelativeLayout mainScreen=(RelativeLayout) content.findViewById(R.id.dialog_layout);
		mainScreen.setLayoutParams(new LinearLayout.LayoutParams(getContentParams().width,getContentParams().height));

		createTitle();
		
		createContent();
		
		setDialogButtons();
}
	
	private void createTitle(){
		RelativeLayout title_frame=(RelativeLayout) content.findViewById(R.id.title_frame);
		title_frame.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		title_text=(TextView) content.findViewById(R.id.title);
		
		content.findViewById(R.id.price).setVisibility(View.VISIBLE);
		
		if (article.isBought()||article.getPrice()==0){
			content.findViewById(R.id.price).setVisibility(View.INVISIBLE);
		}

		
		balance=(TextView) content.findViewById(R.id.tv_money);
		balance.setText(""+(int)article.getPrice());
	} 
	
	private void createContent(){
		LinearLayout content_frame=(LinearLayout) content.findViewById(R.id.content_frame);
		content_frame.setLayoutParams(getContentParams());
		
		progress=(LinearLayout) content.findViewById(R.id.progress_layout);
		notificationText=(TextView) content.findViewById(R.id.notification_text);

		switch (Integer.parseInt(purchase_type)){
			case Constants.PURCHASE_TYPE_THEME_ID:
				themeLoader=new ArticleLoader(getActivity(), article,title_text,content_frame,getContentParams().height/3,getContentParams().height*2/3,callback);
				themeLoader.loadTheme();
				break;
			case Constants.PURCHASE_TYPE_TEXT_ID:
				new ArticleLoader(getActivity(), article,title_text,content_frame,getContentParams().height/3,getContentParams().height*2/3,callback).loadTextPack();
				break;
			case Constants.PURCHASE_TYPE_OFFER_ID: 
				offerLoader=new ArticleLoader(getActivity(), article,title_text,content_frame,getContentParams().height/3,getContentParams().height*2/3,callback);
				offerLoader.loadOffer();
				break;
			case Constants.PURCHASE_TYPE_MONEY_ID:
				new PurseDialogFragment().show(getActivity().getSupportFragmentManager(),"TAG");
				dismiss();
		}
		alert.setView(content);
	}
	
	private void setDialogButtons(){
		ok=(Button) content.findViewById(R.id.button_ok);
		
		if (Integer.parseInt(purchase_type)==Constants.PURCHASE_TYPE_THEME_ID) ok.setEnabled(false);
		
		cancel=(Button) content.findViewById(R.id.button_cancel);
		cancel.setText(getResources().getString(R.string.str_cancel));
		cancel.setOnClickListener(getOnCancelClickListener());
		
		if (article.isBought()||article.getPrice()==0){
			if (Integer.parseInt(purchase_type)==Constants.PURCHASE_TYPE_THEME_ID){
				ok.setText(getResources().getString(R.string.str_choose));
				ok.setOnClickListener(getOnChooseClickListener());}
		else {			
				ok.setText(getResources().getString(R.string.str_ok));
				ok.setOnClickListener(getOnCancelClickListener());}
			}
		
		else {
			ok.setText(getResources().getString(R.string.str_buy));
			ok.setOnClickListener(getOnBuyClickListener());
		}
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
				else if (msg.what==1){
					ShopDialogFragment.this.dismiss();
					Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_LONG).show();
				}
				else 
				{
					progress.setVisibility(View.INVISIBLE);
					article=(Article) msg.obj;
					refreshInformation();
					}
			}
			else if (messageCounter==5) ok.setEnabled(true);
		}
	};

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onDismiss(dialog);
	}

	
	public void refreshDialogButtons(){
		if ((""+Constants.PURCHASE_TYPE_THEME_ID).equals(purchase_type))
		{
		ok.setText(getResources().getString(R.string.str_choose));
		ok.setOnClickListener(getOnChooseClickListener());
		content.findViewById(R.id.price).setVisibility(View.INVISIBLE);
		}
		else dismiss();
	}
	
	public void refreshInformation(){
		createTitle();
		createContent();
		setDialogButtons();
	}

	public int getSelectedPostcard(){
		return themeLoader.getThemeCreator().getSelectedPostcard();
	}

	public Theme getChooseTheme(){
		return themeLoader.getChooseTheme();
	}
	
	public Offer getOffer(){
		return offerLoader.getOffer();
	}

}
