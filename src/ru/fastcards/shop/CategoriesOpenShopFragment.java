//package ru.fastcards.shop;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.util.List;
//
//import org.json.JSONException;
//
//import ru.fastcards.R;
//import ru.fastcards.onShopItemClickListener;
//import ru.fastcards.common.Article;
//import ru.fastcards.common.Offer;
//import ru.fastcards.common.TextPack;
//import ru.fastcards.common.Theme;
//import ru.fastcards.social.api.Api;
//import ru.fastcards.social.api.Params;
//import ru.fastcards.utils.Constants;
//import ru.fastcards.utils.ScreenParams;
//import ru.fastcards.utils.WrongResponseCodeException;
//import android.app.Activity;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBarActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.GridView;
//import android.widget.ListView;
//import android.widget.TabHost;
//import android.widget.TextView;
//import android.widget.TabHost.OnTabChangeListener;
//
//public class CategoriesOpenShopFragment extends Fragment implements OnItemClickListener{
//	
//	private View v;
//	private Context context;
//	private String categoryID;
//	private String current_tab=Constants.TAB_THEME;
//	private List<Offer> offerList;
//	private List<Theme> themeList;
//	private List<TextPack> textList;
//	private ListView offersGridView;
//	private GridView themesGridView;
//	private GridView textGridView;
//	private ArticleAdapter adapter;
//	private onShopItemClickListener shopItemClickListener;
//	private View offerView;
//	private View themeView;
//	private View textView;
//	
//	private final static String TAG="CategoriesOpenShopFragment";
//
//	@Override
//	public void onAttach(Activity activity) {
//	    super.onAttach(activity);
//	        try {
//	        	shopItemClickListener = (onShopItemClickListener) activity;
//	        } catch (ClassCastException e) {
//	            throw new ClassCastException(activity.toString() + " must implement shopItemClickListener");
//	        }
//	  }
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		
//		context=getActivity();
//		
//		v=inflater.inflate(R.layout.purchases_shop_fragment, null);
//
//		createTabs();
//
//		initializeGridView();
//		
//		createOfferLists();
//		createThemeLists();
//		createTextLists();
//		
//		return v;
//	}
//	
//	
//	private void createTabs(){
//		TabHost tabHost = (TabHost) v.findViewById(android.R.id.tabhost);
//		tabHost.setup();
//
//		TabHost.TabSpec tabSpec;
//        
//
//        // создаем вкладку и указываем тег
//        tabSpec = tabHost.newTabSpec(Constants.TAB_THEME);
//        // название вкладки
//        themeView=createIndicatorView(getResources().getString(R.string.themes));
//        tabSpec.setIndicator(themeView);
//        isSelected(themeView);
//        tabSpec.setContent(R.id.themes_grid_view);
//        // добавляем в корневой элемент
//        tabHost.addTab(tabSpec);
//        
//        // создаем вкладку и указываем тег
//        tabSpec = tabHost.newTabSpec(Constants.TAB_OFFER);
//        // название вкладки
//
//        offerView=createIndicatorView(getResources().getString(R.string.str_selections));
//        tabSpec.setIndicator(offerView);
//        tabSpec.setContent(R.id.offers_list_view);
//        // добавляем в корневой элемент
//        tabHost.addTab(tabSpec);
//        
//       // создаем вкладку и указываем тег
//        tabSpec = tabHost.newTabSpec(Constants.TAB_TEXT);
//        // название вкладки
//        textView=createIndicatorView(getResources().getString(R.string.texts));
//        tabSpec.setIndicator(textView);
//        tabSpec.setContent(R.id.texts_grid_view);
//        // добавляем в корневой элемент
//        tabHost.addTab(tabSpec);
//        
//        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
//			
//			@Override
//			public void onTabChanged(String tabId) {
//				// TODO Auto-generated method stub
//				current_tab=tabId;
//				if (Constants.TAB_OFFER.equals(tabId)){
//					isSelected(offerView);
//					isNotSelected(themeView);
//					isNotSelected(textView);
//			        }
//			
//			else if (Constants.TAB_THEME.equals(tabId))
//				{
//				isSelected(themeView);
//				isNotSelected(offerView);
//				isNotSelected(textView);
//				}
//			else {
//				isSelected(textView);
//				isNotSelected(offerView);
//				isNotSelected(themeView);
//			}
//			}
//		});
//        
//        initializeGridView();
//	}
//
//private View createIndicatorView(String title){
//	LayoutInflater inflater=getActivity().getLayoutInflater();
//	View v=null;
//	v=inflater.inflate(R.layout.tab_indicator, null);
//	TextView tabTitle=(TextView) v.findViewById(R.id.tabTitle);
//	tabTitle.setTextColor(getResources().getColor(R.color.text_event_orange));
//	tabTitle.setText(title.toUpperCase());
//	View selectedView=v.findViewById(R.id.view_selected);
//	selectedView.setVisibility(View.INVISIBLE);
//	v.setLayoutParams(new LayoutParams(ScreenParams.screenWidth/3,((ActionBarActivity)context).getSupportActionBar().getHeight()));
//	return v;
//}
//
//private void isSelected(View v){
//	View selectedView=v.findViewById(R.id.view_selected);
//	selectedView.setVisibility(View.VISIBLE);
//}
//
//private void isNotSelected(View v){
//	View selectedView=v.findViewById(R.id.view_selected);
//	selectedView.setVisibility(View.INVISIBLE);
//}
//	
//	private void createOfferLists(){
//		
//		new AsyncTask<Params, List<Offer>, List<Offer>>() {
//			Api api=new Api(context);
//			@Override
//			protected List<Offer> doInBackground(Params... params) {
//				try {
//					offerList = api.getOffers(categoryID);
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				return offerList;
//			}
//			protected void onPostExecute(List<Offer> offerList) {
//				if (offerList!=null)
//				setContentToGridView((AdapterView)offersGridView, (List<Article>) (List<?>)offerList);
//			}
//		}.execute();	}
//	
//	private void createThemeLists()	{
//		new AsyncTask<Params, List<Theme>, List<Theme>>(){
//			Api api=new Api(context);
//
//			@Override
//			protected List<Theme> doInBackground(Params... params) {
//				// TODO Auto-generated method stub
//				try {
//					themeList = api.getThemes(categoryID);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return themeList;
//			}
//			
//			protected void onPostExecute(java.util.List<Theme> themeList) {
//				if (themeList!=null)
//				setContentToGridView((AdapterView)themesGridView, (List<Article>) (List<?>)themeList);
//			};
//		
//		}.execute();}
//	
//	private void createTextLists(){
//		new AsyncTask<Params, List<TextPack>, List<TextPack>>(){
//			Api api=new Api(context);
//			@Override
//			protected List<TextPack> doInBackground(Params... params) {
//				// TODO Auto-generated method stub
//				try {
//					textList=api.getTextPacksByCategory(categoryID);
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (WrongResponseCodeException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return textList;
//			}
//			
//			protected void onPostExecute(java.util.List<TextPack> textList) {
//				if (textList!=null)
//				setContentToGridView((AdapterView)textGridView, (List<Article>) (List<?>)textList);
//			};
//		}.execute();}
//	
//	private void initializeGridView(){
//		offersGridView = (ListView) v.findViewById(R.id.offers_list_view);
//		offersGridView.setOnItemClickListener(this);
//		themesGridView = (GridView) v.findViewById(R.id.themes_grid_view);
//		themesGridView.setOnItemClickListener(this);
//		textGridView = (GridView) v.findViewById(R.id.texts_grid_view);
//		textGridView.setOnItemClickListener(this);
//	}
//	
//	
//	private void setContentToGridView(AdapterView<ArticleAdapter> gridView,List<Article> buyArticleList){
//		if (buyArticleList!=null) {
//		adapter = new ArticleAdapter(context,buyArticleList);
//		gridView.setAdapter(adapter);}
//	}
//	
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//		// TODO Auto-generated method stub
//		if (current_tab==Constants.TAB_OFFER) {
//			shopItemClickListener.buyItem(offerList.get(position), ""+Constants.PURCHASE_TYPE_OFFER_ID);
//		}
//		if (current_tab==Constants.TAB_THEME) {
//			shopItemClickListener.buyItem(themeList.get(position),""+Constants.PURCHASE_TYPE_THEME_ID);
//		}
//		if (current_tab==Constants.TAB_TEXT) {
//			shopItemClickListener.buyItem(textList.get(position),""+Constants.PURCHASE_TYPE_TEXT_ID);
//		}
//	}
//
//	public void refresh(){
//		if (this.isVisible()){
//		createOfferLists();
//		createThemeLists();
//		createTextLists();}
//	}
//
//	public void setCategoryID(String ID){
//		this.categoryID=ID;
//	}
//
//}
