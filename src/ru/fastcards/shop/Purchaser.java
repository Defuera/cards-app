package ru.fastcards.shop;

import ru.fastcards.R;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.social.api.Api;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;


public class Purchaser {
	
	
	
	private Context context;
	private PreviewDialogFragment dialog;
	private Article article;
	private String purchase_type;
	private DataBaseHelper dbHelper;
	private Handler callback;
	
	public Purchaser(Context context,Handler callback, Article article,String purchase_type,PreviewDialogFragment dialog) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.dialog=dialog;
		this.article=article;
		this.purchase_type=purchase_type;
		this.callback=callback;
		dbHelper=DataBaseHelper.getInstance(context);
	}
	
	public Purchaser(Context context,Handler callback){
		this.context=context;
		this.callback=callback;
	}

	public void buyArticle(){
		new AsyncTask<String, String, String>(){

			@Override
			protected String doInBackground(String... params) {
				String result = null;
				try {
					final Api api = Api.getInstanse(context);
					result = api.makePurchase(article.getPurchaseId());
				} catch (Exception e) {
					e.printStackTrace();
				} 
				return result;
			}
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if ("".equals(result)){
					Toast.makeText(context, context.getResources().getString(R.string.str_transaction_success), Toast.LENGTH_LONG).show();
					refreshDB(article,purchase_type);
					Account account=Account.getInstance();
					account.refreshWealth(context);
					callback.sendEmptyMessage(Constants.TRANSACTION_SUCCED);
					dialog.refreshDialogButtons();
				} 
				else if ((context.getResources().getString(R.string.str_not_star_error)+" ").equals(result)){
					callback.sendEmptyMessage(Constants.NOT_ENOUGH_STARS);
					}
				else {
					Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
				}
			}
			
		}.execute();
	}
	
	private void refreshDB(Article article,String purchaseType){
		switch (Integer.parseInt(purchaseType)){
		case Constants.PURCHASE_TYPE_THEME_ID:
			dbHelper.purchaseTheme(article.getPurchaseId());
			break;
		case Constants.PURCHASE_TYPE_TEXT_ID:
			dbHelper.purchaseTextPack(article.getPurchaseId());
			break;
		case Constants.PURCHASE_TYPE_OFFER_ID:
			buyOffer();
			break;
		}
	}
	
	private void buyOffer(){
		Offer offer=((ShopDialogFragment)dialog).getOffer();
		offer.setBought(true);
		for (Article a:offer.getArticlesList()){
			switch (Integer.parseInt(a.getPurchaseType()))
			{
			case Constants.PURCHASE_TYPE_THEME_ID:
				dbHelper.purchaseTheme(a.getPurchaseId());
				break;
			case Constants.PURCHASE_TYPE_TEXT_ID:
				dbHelper.purchaseTextPack(a.getPurchaseType());
				break;
			}	
}
}
}
