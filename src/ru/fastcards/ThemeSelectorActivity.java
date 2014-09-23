package ru.fastcards;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.common.Article;
import ru.fastcards.common.Theme;
import ru.fastcards.editor.EditorActivity;
import ru.fastcards.shop.ArticleAdapter;
import ru.fastcards.shop.Purchaser;
import ru.fastcards.shop.ThemeDialogFragment;
import ru.fastcards.shop.WealthView;
import ru.fastcards.social.api.Api;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ThemeSelectorActivity extends ActionBarActivity{
	private static final String TAG = "ShopActivity";
	protected Context context;
	protected List<Theme> themesList = new ArrayList<Theme>();

	private ArticleAdapter adapter;
	private GridView gridView;
	private ProgressDialog dialog;
	private DataBaseHelper dbHelper;
	private HandlerCallback themeHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme_selector);
		dbHelper = DataBaseHelper.getInstance(context);
		context = this;

		gridView = (GridView) findViewById(R.id.gv_postcards);
		getThemesList();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		themeHandler = new HandlerCallback(this);
	}
	
	private static class HandlerCallback extends Handler {
		WeakReference<ThemeSelectorActivity> wrActivity;
	    public HandlerCallback(ThemeSelectorActivity activity) {
	            wrActivity = new WeakReference<ThemeSelectorActivity>(activity);
	    }
	    @Override
	    public void handleMessage(Message msg) {
	    	// TODO Auto-generated method stub
	    	super.handleMessage(msg);
	    	ThemeSelectorActivity activity = wrActivity.get();
	    		if (msg.what==Constants.TRANSACTION_SUCCED)
	    			{
	    			activity.refreshData();
	    			}	
	    		else if (msg.what==Constants.NOT_ENOUGH_STARS)
	    			new PurseDialogFragment().show(activity.getSupportFragmentManager(), TAG);
		};
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (themeHandler!=null) 
			themeHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
	private void getThemesList() {
		
		themesList = dbHelper.getThemesList(getCategoryId());
		Log.d("getThemesList", "loaded from DB "+themesList.size()+" getCategoryId() "+getCategoryId());
		
		if (themesList.isEmpty()){
			dialog = new ProgressDialog(context);
			dialog.setMessage(getString(R.string.loading_postcards));
			dialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			dialog.show();
		}else{
			adapter = new ArticleAdapter(this, (List<Article>) (List<?>)  themesList);
			initializeGrdView();
		}
		
		Utils.checkForUpdate(context, Constants.VERSIONS_THEMES, new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == 1){
					themesList = dbHelper.getThemesList(getCategoryId());
					Log.v("getThemesList", "loaded from updated DB "+themesList.size()+" getCategoryId() "+getCategoryId());
					
					if (dialog != null){
						dialog.dismiss();
						adapter = new ArticleAdapter(context, (List<Article>) (List<?>)  themesList);						
						initializeGrdView();
					} else
						adapter.notifyDataSetChanged();

						
					
				}
				if (themesList.isEmpty()){
					downloadTheme();
				}
				return false;
			}


		});

	}
	
	private void downloadTheme() {
		new AsyncTask<String, String, String>(){
			protected String doInBackground(String[] params) {
				Api api = Api.getInstanse(context);
				try {
					themesList = api.getThemes(getCategoryId());
					for (Theme theme : themesList){
						dbHelper.saveTheme(theme);						
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			};
			protected void onPostExecute(String result) {
				if (dialog != null)
					dialog.dismiss();
				adapter = new ArticleAdapter(context, (List<Article>) (List<?>)  themesList);
				initializeGrdView();
			};
		}.execute();
		
		
	}

	private String getCategoryId() {
		return getIntent().getStringExtra(Constants.EXTRA_CATEGORY_ID);
	}

	/**
	 * Initializes table of content view. Sets number of columns.
	 */
	private void initializeGrdView() {
		gridView.setOnItemClickListener(onPostcardClickListener);
		gridView.setAdapter(adapter);
	}

	private ThemeDialogFragment themeDialog;
	private OnItemClickListener onPostcardClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final int counter=position;
			themeDialog=ThemeDialogFragment.newInstance(context,themesList.get(position));
			themeDialog.show(getSupportFragmentManager(), Constants.SHOP_DIALOG);
			themeDialog.setOnChooseClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					themeDialog.dismiss();
					Intent intent = new Intent(context, EditorActivity.class);
					intent.putExtra(Constants.EXTRA_PURCHASE_ID, themesList.get(counter).getPurchaseId());
					intent.putExtra(Constants.EXTRA_EVENT_ID, getEventId());
					intent.putExtra(Constants.EXTRA_POSTCARD_SELECTED, themeDialog.getSelectedPostcard());
					startActivity(intent);
				}
			});			
			themeDialog.setOnBuyClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					new Purchaser(context, themeHandler, themesList.get(counter), ""+Constants.PURCHASE_TYPE_THEME_ID, themeDialog).buyArticle();
				}
			});

		}
	};
	
	private void refreshData(){
		themesList = dbHelper.getThemesList(getCategoryId());
		adapter = new ArticleAdapter(context, (List<Article>) (List<?>)  themesList);
		gridView.setAdapter(adapter);
//		adapter.notifyDataSetChanged();
		themeDialog.refreshDialogButtons();
	}

	private String getEventId() {
		String eventId = getIntent().getStringExtra(Constants.EXTRA_EVENT_ID);
		Log.d(TAG, "getEventId "+eventId);
		return eventId;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.wealth, menu);
		MenuItem item = menu.findItem(R.id.action_wealth);		
		WealthView wealthView = WealthView.getInstanse(this);
		
		if (Utils.hasHoneycomb()) MenuItemCompat.setActionView(item, wealthView);		
		if (wealthView!=null) wealthView.refreshWealth();
		
		return true;
	}
}
