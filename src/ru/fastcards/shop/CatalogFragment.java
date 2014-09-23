package ru.fastcards.shop;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.common.CategoryGroup;
import ru.fastcards.common.Offer;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.ThemesCategory;
import ru.fastcards.social.api.Api;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.ScreenParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class CatalogFragment extends Fragment{
	
	private ActionBar actionBar;
	private Activity context;
	private String groupId;
	private List<CategoryGroup> listGroup;
	private List<ThemesCategory> listCategory;
	private CatalogAdapter adapter;
	private GridShopFragment themeGridFragment;
	private GridShopFragment textGridFragment;
	private ListShopFragment offerListFragment;
	
	private static final String TAG="Catalog";
	private ProgressDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.catalog_fragment, null);
		context=getActivity();
        setHasOptionsMenu(true);
        createTabs(v);
        addFragments();
		getActionBar();
		if (listGroup==null || listGroup.isEmpty() )getGroups();
		else getThemesCategories(false);
		return v;
	}
		
	private void getActionBar(){
		actionBar=((ActionBarActivity)context).getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(false);
	}
	private void createTabs(View v){
		TabHost tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
		tabHost.setup();
        

        
        // создаем вкладку и указываем тег
		TabSpec tabSpec = tabHost.newTabSpec(Constants.TAB_THEME);
        // название вкладки
        final View themeView=createIndicatorView(getResources().getString(R.string.themes));
        isSelected(themeView);
        tabSpec.setIndicator(themeView);
        tabSpec.setContent(R.id.themes_layout);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);
        
        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(Constants.TAB_OFFER);
        // название вкладки

        final View offerView=createIndicatorView(getResources().getString(R.string.str_selections));
        tabSpec.setIndicator(offerView);

        tabSpec.setContent(R.id.offer_layout);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);
        
       // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec(Constants.TAB_TEXT);
        // название вкладки
        final View textView=createIndicatorView(getResources().getString(R.string.texts));
        tabSpec.setIndicator(textView);
        tabSpec.setContent(R.id.texts_layout);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);
        
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (Constants.TAB_OFFER.equals(tabId)){
					isSelected(offerView);
					isNotSelected(themeView);
					isNotSelected(textView);}
			
				else if (Constants.TAB_THEME.equals(tabId)){
					isSelected(themeView);
					isNotSelected(offerView);
					isNotSelected(textView);}
				else {
					isSelected(textView);
					isNotSelected(offerView);
					isNotSelected(themeView);}
				}
		});
        
	}
	
	private void addFragments(){
        FragmentTransaction ft=((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        themeGridFragment=new GridShopFragment();
        textGridFragment=new GridShopFragment();
        offerListFragment=new ListShopFragment();

        ft.add(R.id.themes_layout, themeGridFragment);
        ft.add(R.id.texts_layout, textGridFragment);
        ft.add(R.id.offer_layout, offerListFragment);
        
        ft.commit();
	}
	
	private View createIndicatorView(String title){
		LayoutInflater inflater=context.getLayoutInflater();
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

	
	private void getGroups(){
		final Api api=Api.getInstanse(context);
		dialog=new ProgressDialog(context);
		dialog.setMessage(getString(R.string.loading_catalog));
		
		new AsyncTask<Void, List<CategoryGroup>, List<CategoryGroup>>() {
			
			protected void onPreExecute() {
				dialog.show();
			};

			@Override
			protected List<CategoryGroup> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				listGroup=null;
				try {
					listGroup=api.getCategoryGroups2();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return listGroup;
			}
			
		@Override
		protected void onPostExecute(List<CategoryGroup> result) {
				// TODO Auto-generated method stub
				if (result!=null) {
					adapter = new CatalogAdapter(context,android.R.layout.simple_list_item_1,result);
					actionBar.setListNavigationCallbacks(adapter, navigationListener);
					getThemesCategories(false);
				}
				else 
					Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		}.execute();
	}
	
	private OnNavigationListener navigationListener=new OnNavigationListener() {
		
		@Override
		public boolean onNavigationItemSelected(int position, long id) {
			// TODO Auto-generated method stub
			groupId=listGroup.get(position).getId();
			getThemesCategories(false);
			return true;
		}
	};
	
	private void getThemesCategories(boolean refresh){
			if (!refresh) dialog.show();
		new AsyncTask<Void, List<ThemesCategory>, List<ThemesCategory>>() {
			final Api api=Api.getInstanse(context);
			@Override
			protected List<ThemesCategory> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				List<ThemesCategory> listArticle=null;
				try {
					listArticle=api.getThemesCategories(groupId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return listArticle;
			}
			
			@Override
			protected void onPostExecute(List<ThemesCategory> result) {
				// TODO Auto-generated method stub
				if (result==null)
					Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_SHORT).show();
				else {
					listCategory=result;
					loadThemes();
					loadTextPacks();
					loadOffers();
				}
			}
			
		}.execute();
	}
	
	
	private void loadThemes(){
		dialog.dismiss();
		if (listCategory.size()==1)
			themeGridFragment.setListArticle(listCategory.get(0).getThemesList());
		else 
			themeGridFragment.setThemesCategories(context, listCategory);
	}
	
	private void loadTextPacks(){
		new AsyncTask<Void, List<TextPack>, List<TextPack>>() {
			final Api api=Api.getInstanse(context);

			@Override
			protected List<TextPack> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				List<TextPack> listArticle=null;
				try {
					listArticle=api.getTextCategories(groupId,listCategory.get(0).getCategory().getId());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return listArticle;
			}
			
			@Override
			protected void onPostExecute(List<TextPack> result) {
				// TODO Auto-generated method stub
				textGridFragment.setListArticle(result);
			}
			
		}.execute();
	}
	
	private void loadOffers(){
		new AsyncTask<Void, List<Offer>, List<Offer>>() {
			final Api api=Api.getInstanse(context);

			@Override
			protected List<Offer> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				List<Offer> listArticle=null;
				try {
					listArticle=api.getOffersCategories(groupId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return listArticle;
			}
			
			@Override
			protected void onPostExecute(List<Offer> result) {
				// TODO Auto-generated method stub
				offerListFragment.setOfferList(context, result);
			}
			
		}.execute();
	}

	public void refresh(){
		if (this.isVisible())
		{
			getThemesCategories(true);
		}
	}
}