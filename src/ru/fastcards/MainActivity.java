package ru.fastcards;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import org.json.JSONException;

import com.sromku.simple.fb.Permissions;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import ru.fastcards.common.Article;
import ru.fastcards.common.Banner;
import ru.fastcards.editor.EditorActivity;
import ru.fastcards.manager.ManagerActivity;
import ru.fastcards.shop.ActionsFragment;
import ru.fastcards.shop.BannerPagerFragment;
import ru.fastcards.shop.MainShopActivity;
import ru.fastcards.shop.Purchaser;
import ru.fastcards.shop.ShopDialogFragment;
import ru.fastcards.shop.WealthView;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.ScreenParams;
import ru.fastcards.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class MainActivity extends  TrackedActivity implements onShopItemClickListener{

	private static final String TAG = "MainActivity";

	private Context context;
	private Account account = Account.getInstance();
	private Api api;// = new Api(context);
	
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private ShopDialogFragment shopDialog;
	private ActionsFragment actionsFragment;
	private WealthView wealthView;
	private Handler callback;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		callback=new HandlerCallback(this);
		
		supportInvalidateOptionsMenu();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (callback!=null) callback.removeCallbacksAndMessages(null);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		context = this;
		Utils.checkForUnconsumedItems(context);
		
		setScreenParams();		

		api = Api.getInstanse(context);
		account.restoreAccount(context);
		
		loginToFastCards();
		updateContacts();
		
		if (Utils.isNetworkAvailable(context)){
			getListBanner();
			createActionsFragment(); 
		}
		else {
			Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_SHORT).show();
		}
		
		initializeFbSession();
	}
	
    private void initializeFbSession() {
		Permissions[] permissions = new Permissions[]
		{
				Permissions.USER_PHOTOS,
				Permissions.FRIENDS_PHOTOS,
				Permissions.FRIENDS_BIRTHDAY,
				Permissions.EMAIL,
				Permissions.READ_FRIENDLISTS,
				Permissions.PUBLISH_STREAM
		};
		
		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
	    .setAppId(getString(R.string.id_facebook))
	    .setNamespace("FastCards")
	    .setPermissions(permissions)
	    .build();

	    Log.wtf(TAG, "facebook id "+getString(R.string.id_facebook));
		SimpleFacebook.setConfiguration(configuration);
		
	}
	

	private void setScreenParams(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		ScreenParams.setDpi(metrics.densityDpi);
		ScreenParams.setScreenWidth(metrics.widthPixels);
		ScreenParams.setScreenHeight(metrics.heightPixels);
	}

	private void createActionsFragment() {
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		actionsFragment =ActionsFragment.newInstance(context, Constants.GET_NEW);
		fragmentTransaction.add(R.id.ll_fragment_container, actionsFragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
	
	private void getListBanner(){
		new AsyncTask<Params, List<Banner>, List<Banner>>() {
			private List<Banner> listBanner;
			@Override
			protected List<Banner> doInBackground(Params... params) {
				try {
					listBanner = api.getBanners();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return listBanner;
			}
			protected void onPostExecute(final List<Banner> listBanner)
				{	
				createBanner(listBanner);
				}
		}.execute();
	}
	
	
	private void createBanner(List<Banner> list){
		fragmentManager = getSupportFragmentManager();
		BannerPagerFragment bannerPager=new BannerPagerFragment();
//		bannerPager.setListBanner(list);
		fragmentTransaction = fragmentManager.beginTransaction();        
        fragmentTransaction.add(R.id.banner_container, bannerPager);
        fragmentTransaction.commitAllowingStateLoss();
	}

	/**
	 * Checks devise contacts book for new contacts
	 */
	private void updateContacts() {
		ContactsResolver contacts = new ContactsResolver(this, context);
		contacts.importContactsToDb();
	}

	private void loginToFastCards() {
		new AsyncTask<Params, String, Result>() {
			@Override
			protected Result doInBackground(Params... params) {
				try {
					if (!account.hasFastcardsId()) {
						api.login();
					}
//					api.getThemesCategories("2");//updateCategoryGroups2();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			protected void onPostExecute(Result result) {
				supportInvalidateOptionsMenu();
			};
		}.execute();
		
	}

	public void onPostCardButtonClick(View v) {
		Intent intent = new Intent(this, MainShopActivity.class);
		startActivity(intent);
	}

	public void onCalendarButtonClick(View v) {
//		Intent intent = new Intent(this, EventsActivity.class);
		Intent intent = new Intent(this, CalendarActivity.class);
		startActivity(intent);
	}



	
		@Override
		public void buyItem(final Article a,final String purchaseType) {
			if (!Utils.isNetworkAvailable(context)){
				Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_LONG).show();
				return;
			}
			shopDialog=ShopDialogFragment.newInstance(context, a, a.getPurchaseType());
			shopDialog.show(getSupportFragmentManager(), TAG);
			shopDialog.setOnChooseClickListener(onChooseClickListener);
			shopDialog.setOnBuyClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new Purchaser(context, callback, a, purchaseType, shopDialog).buyArticle();
				}
			});
		}
		

		private static class HandlerCallback extends Handler {			
			WeakReference<MainActivity> wrActivity;
			 
		    public HandlerCallback(MainActivity activity) {
		            wrActivity = new WeakReference<MainActivity>(activity);
		    }
		    
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				MainActivity activity=wrActivity.get();
				if (msg.what==Constants.TRANSACTION_SUCCED)
					activity.actionsFragment.refresh();
				else if (msg.what==Constants.NOT_ENOUGH_STARS)
					new PurseDialogFragment().show(activity.getSupportFragmentManager(), TAG);
			};
		}
		
		private OnClickListener onChooseClickListener=new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				shopDialog.dismiss();
				Intent intent = new Intent(context, EditorActivity.class);
				intent.putExtra(Constants.EXTRA_ID, shopDialog.getChooseTheme().getId());
				intent.putExtra(Constants.EXTRA_PURCHASE_ID, shopDialog.getChooseTheme().getPurchaseId());
				intent.putExtra(Constants.EXTRA_POSTCARD_SELECTED, shopDialog.getSelectedPostcard());
				startActivity(intent);
			}
		};


		private void testStuff(){
			new AsyncTask<Params, String, Result>() {
				@Override
				protected Result doInBackground(Params... params) {
					try {
						api.getAllOffers();//updateCategoryGroups2();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return null;
				}				
				protected void onPostExecute(Result result) {
					supportInvalidateOptionsMenu();
				};
			}.execute();
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);

			MenuItem item = menu.findItem(R.id.action_wealth);		
			wealthView = WealthView.getInstanse(this);
			
			if (Utils.hasHoneycomb()) MenuItemCompat.setActionView(item, wealthView);		
			if (wealthView!=null) wealthView.refreshWealth();
			return true;
		}
		

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_wealth: {
				Log.d(TAG, "click action wealth");
				new PurseDialogFragment().show(getSupportFragmentManager(), TAG);
				
				return true;
			}
			case R.id.action_settings: {
//				testStuff();
				Intent intent = new Intent(this, ManagerActivity.class);
				startActivity(intent);
				return true;
			}
			default:{
			}
				return super.onOptionsItemSelected(item);
			}
		}	
		


}