package ru.fastcards.shop;

import java.io.IOException;

import org.json.JSONException;

import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.Theme;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.Constants;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.TextView;


public class ArticleLoader {
	
	private static final String TAG="Article Loader";
	private FragmentActivity activity;
	private Article article;
	private ViewGroup container;
	private TextView title;
	private ThemeCreator themeCreator;
	private Theme  theme;
	private Offer offer;
	private int previewHeight;
	private int largeHeight;
	private Handler callback;
	
	public ArticleLoader(FragmentActivity activity,Article article,TextView title,ViewGroup container,int previewHeight,int largeHeight,Handler callback) {
		// TODO Auto-generated constructor stub
		this.activity=activity;
		this.article=article;
		this.container=container;
		this.title=title;
		this.previewHeight=previewHeight;
		this.largeHeight=largeHeight;
		this.callback=callback;
	}
	
	public void loadArticle(){
		int purchaseType=Integer.parseInt(article.getPurchaseType());

		switch (purchaseType){
		case Constants.PURCHASE_TYPE_THEME_ID: loadTheme();break;
		case Constants.PURCHASE_TYPE_TEXT_ID: loadTextPack();break;
		case Constants.PURCHASE_TYPE_OFFER_ID: loadOffer();break;
		case Constants.PURCHASE_TYPE_MONEY_ID: 
			Intent intent=new Intent(activity,StarsShopActivity.class);
			activity.startActivity(intent);
			break;
		}
	}
	
	protected void loadTheme(){

		new AsyncTask<Params, Theme, Theme>() {
			final Api api = Api.getInstanse(activity);
			
			@Override
			protected Theme doInBackground(Params... params) {
				try {
					theme=(Theme)api.getPurchase(article.getPurchaseId(), ""+Constants.PURCHASE_TYPE_THEME_ID);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return theme;
			}
			protected void onPostExecute(Theme theme) {
				if (theme!=null){
					themeCreator=new ThemeCreator(activity,previewHeight,largeHeight);
					title.setText(theme.getName());
					container.addView(themeCreator.createTheme(theme,callback));
					callback.sendEmptyMessage(0);
					}
				else ;
					callback.sendEmptyMessage(1);
				}
		}.execute();
			
	}
	
	protected void loadTextPack(){
		new AsyncTask<Params, TextPack, TextPack>() {
			final Api api = Api.getInstanse(activity);
			TextPack  textPack;
			@Override
			protected TextPack doInBackground(Params... params) {
				try {
					textPack=(TextPack)api.getPurchase(article.getPurchaseId(), ""+Constants.PURCHASE_TYPE_TEXT_ID);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return textPack;
			}
			protected void onPostExecute(TextPack textPack) {
				if (textPack!=null){
					title.setText(textPack.getName());
					container.addView(new TextCreator(activity,previewHeight,largeHeight).createTextPack(textPack,callback));
					callback.sendEmptyMessage(0);
				}
				else callback.sendEmptyMessage(1);
				}
		}.execute();
	}

	protected void loadOffer(){
		new AsyncTask<Params, Offer, Offer>() {
			final Api api = Api.getInstanse(activity);
			@Override
			protected Offer doInBackground(Params... params) {
				try {
					offer=(Offer)api.getPurchase(article.getPurchaseId(), ""+Constants.PURCHASE_TYPE_OFFER_ID);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return offer;
			}
			protected void onPostExecute(Offer offer) {
				if (offer!=null){
					title.setText(offer.getName());
					Message msg=callback.obtainMessage(2, offer);
					callback.sendMessage(msg);
					container.addView(new OfferCreator(activity, offer,previewHeight,largeHeight).createOfferView());
				}
				else callback.sendEmptyMessage(1);
				}
		}.execute();
	}
	
	public ThemeCreator getThemeCreator(){
		return themeCreator;
	}
	
	public Theme getChooseTheme(){
		return theme;
	}
	
	public Offer getOffer(){
		return offer;
	}
}
