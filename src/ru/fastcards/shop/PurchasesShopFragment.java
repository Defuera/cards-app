package ru.fastcards.shop;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.Theme;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.ScreenParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class PurchasesShopFragment extends Fragment implements OnItemClickListener {
	
	private View v;
	private Context context;
	private List<Theme> buyThemesList;
	private List<TextPack>buyTextList;
	private List<Offer> buyOffersList;
	private GridView themeGridView,textGridView;
	private ListView offerListView;
	private String current_tab=Constants.TAB_THEME;
	private onShopItemClickListener shopItemClickListener;
	private View offerView;
	private View themeView;
	private View textView;
	
	private static final String TAG="PurchasesShopFragment";
	
	
//	public static PurchasesShopFragment newInstance(Context context){
//		PurchasesShopFragment fragment=new PurchasesShopFragment();
////		fragment.context=context;
//		return fragment;
//	}
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	        	shopItemClickListener = (onShopItemClickListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement shopItemClickListener");
	        }
	  }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context=getActivity();
		v=inflater.inflate(R.layout.purchases_shop_fragment, null);
		createTabs();
		initializeGridView();
		return v;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (buyThemesList==null) createBoughtThemesLists();
			else {
				List<Article> listArticle=(List<Article>)(List<?>)buyThemesList;
				setContentToGridView((AdapterView)themeGridView, listArticle, createAdapter(listArticle));
			}
		
		if (buyOffersList==null) createBoughtOfferLists();
		else {
			List<Article> listArticle=(List<Article>)(List<?>)buyOffersList;
			setContentToGridView((AdapterView)offerListView, listArticle, createAdapter(listArticle));
			}
		
		if (buyTextList==null) createBoughtTextPackList();
		else {
			List<Article> listArticle=(List<Article>)(List<?>)buyTextList;
			setContentToGridView((AdapterView)textGridView, listArticle, createAdapter(listArticle));
		}	
	}
	
	private void createTabs(){
		TabHost tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
		tabHost.setup();
        

        
        // создаем вкладку и указываем тег
		TabSpec tabSpec = tabHost.newTabSpec(Constants.TAB_THEME);
        // название вкладки
        themeView=createIndicatorView(getResources().getString(R.string.themes));
        isSelected(themeView);
        tabSpec.setIndicator(themeView);
        tabSpec.setContent(R.id.themes_layout);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);
        
        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(Constants.TAB_OFFER);
        // название вкладки

        offerView=createIndicatorView(getResources().getString(R.string.str_selections));
        tabSpec.setIndicator(offerView);

        tabSpec.setContent(R.id.offer_layout);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);
        
       // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(Constants.TAB_TEXT);
        // название вкладки
        textView=createIndicatorView(getResources().getString(R.string.texts));
        tabSpec.setIndicator(textView);
        tabSpec.setContent(R.id.texts_layout);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);
        
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				current_tab=tabId;
				if (Constants.TAB_OFFER.equals(tabId)){
					isSelected(offerView);
					isNotSelected(themeView);
					isNotSelected(textView);
			        }
			
			else if (Constants.TAB_THEME.equals(tabId))
				{
				isSelected(themeView);
				isNotSelected(offerView);
				isNotSelected(textView);
				}
			else {
				isSelected(textView);
				isNotSelected(offerView);
				isNotSelected(themeView);
			}
			}
		});
        
	}
	
	private View createIndicatorView(String title){
		LayoutInflater inflater=getActivity().getLayoutInflater();
		View v=null;
		v=inflater.inflate(R.layout.tab_indicator, null);
		TextView tabTitle=(TextView) v.findViewById(R.id.tabTitle);
		tabTitle.setTextColor(getResources().getColor(R.color.text_event_orange));
		tabTitle.setText(title.toUpperCase());
		View selectedView=v.findViewById(R.id.view_selected);
		selectedView.setVisibility(View.INVISIBLE);
		v.setLayoutParams(new LayoutParams(ScreenParams.screenWidth/3,((ActionBarActivity)context).getSupportActionBar().getHeight()));
		return v;
	}
	
	private void isSelected(View v){
		View selectedView=v.findViewById(R.id.view_selected);
		selectedView.setVisibility(View.VISIBLE);
	}
	
	private void isNotSelected(View v){
		View selectedView=v.findViewById(R.id.view_selected);
		selectedView.setVisibility(View.INVISIBLE);
	}

	
	private void createBoughtOfferLists(){
	//Load MyOffers
	new AsyncTask<Params, List<Offer>, List<Offer>>() {
		final Api api = Api.getInstanse(context);
		@Override
		protected List<Offer> doInBackground(Params... params) {
			try {
				buyOffersList =api.getBoughtOffers();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return buyOffersList;
		}
		protected void onPostExecute(List<Offer> listOffer) {
			List<Article> listArticle=(List<Article>)(List<?>)buyOffersList;
			if (listArticle!=null)
				setContentToGridView((AdapterView)offerListView, listArticle, createAdapter(listArticle));
		}
	}.execute();
	}

	
	//load MyThemes
	private void createBoughtThemesLists(){
		final ProgressDialog dialog=new ProgressDialog(context);
		dialog.setMessage(getString(R.string.loading_postcards));
		
	new AsyncTask<Params, List<Theme>, List<Theme>>() {
		final Api api = Api.getInstanse(context);
		
		protected void onPreExecute() {
			dialog.show();
		};
		@Override
		protected List<Theme> doInBackground(Params... params) {
			try {
				buyThemesList =api.getBoughtThemes();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return buyThemesList;
		}
		protected void onPostExecute(List<Theme> listTheme) {
			List<Article> listArticle=(List<Article>)(List<?>)buyThemesList;
			if (listArticle!=null){
				dialog.dismiss();
				setContentToGridView((AdapterView)themeGridView, listArticle, createAdapter(listArticle));
			}
		else 
			Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_SHORT).show();
	}}.execute();
	}
	
	private void createBoughtTextPackList(){
	//Load MyTextPacks
	new AsyncTask<Params, List<TextPack>, List<TextPack>>() {
		final Api api = Api.getInstanse(context);
		@Override
		protected List<TextPack> doInBackground(Params... params) {
			try {
				buyTextList =api.getBoughtTextPacks();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return buyTextList;
		}
		protected void onPostExecute(List<TextPack> listTextPack) {
			List<Article> listArticle=(List<Article>)(List<?>)buyTextList;
			if (listArticle!=null)
				setContentToGridView((AdapterView)textGridView, listArticle, createAdapter(listArticle));
		}
	}.execute();
	}
	
	private void initializeGridView(){
		themeGridView=(GridView) v.findViewById(R.id.themes_grid_view);
		themeGridView.setOnItemClickListener(this);		
		textGridView = (GridView) v.findViewById(R.id.texts_grid_view);
		textGridView.setOnItemClickListener(this);
		offerListView = (ListView) v.findViewById(R.id.offers_list_view);
		offerListView.setOnItemClickListener(this);
	}
	
	private ArticleAdapter createAdapter(List<Article> buyArticleList){
		return new ArticleAdapter(context,buyArticleList);
	}
	
	private void setContentToGridView(AdapterView<ArticleAdapter> adapterView,List<Article> buyArticleList,ArticleAdapter adapter){
		if (buyArticleList!=null) {
		adapterView.setAdapter(adapter);}
	}


	public void refresh(){
		buyOffersList=null;
		buyThemesList=null;
		buyTextList=null;
		if (this.isVisible()){
			createBoughtOfferLists();
			createBoughtThemesLists();
			createBoughtTextPackList();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (current_tab==Constants.TAB_OFFER) {
			shopItemClickListener.buyItem(buyOffersList.get(position), ""+Constants.PURCHASE_TYPE_OFFER_ID);
		}
		if (current_tab==Constants.TAB_THEME) {
			shopItemClickListener.buyItem(buyThemesList.get(position), ""+Constants.PURCHASE_TYPE_THEME_ID);
		}
		if (current_tab==Constants.TAB_TEXT) {
			shopItemClickListener.buyItem(buyTextList.get(position), ""+Constants.PURCHASE_TYPE_TEXT_ID);
		}
	}
}
