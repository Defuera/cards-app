package ru.fastcards.shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.common.MoneyPack;
import ru.fastcards.inapp.IabHelper;
import ru.fastcards.inapp.IabResult;
import ru.fastcards.inapp.Inventory;
import ru.fastcards.inapp.Purchase;
import ru.fastcards.inapp.SkuDetails;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class StarsShopActivity extends ActionBarActivity {

	protected static final String TAG = "StarsShopActivity";
	private Context context;
	private ProgressDialog progressDialog;
	private List<MoneyPack> moneyPacksList;
	private IabHelper mHelper;
	private Account account = Account.getInstance();

	private String payload;

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stars_shop);
		context = this;
		startProgressDialog();

		Utils.checkForUnconsumedItems(context);

		if (Utils.isNetworkAvailable(context)) {
			loadMoneyPacks();
		} else
			finishActivity();
	}
	

	private void loadMoneyPacks() {
		Log.v(TAG, "loadMoneyPacks");
		new AsyncTask<Params, List<MoneyPack>, List<MoneyPack>>() {
			final Api api = Api.getInstanse(context);

			@Override
			protected List<MoneyPack> doInBackground(Params... params) {
				try {

					moneyPacksList = api.getMoneyPacks();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return moneyPacksList;
			}

			protected void onPostExecute(List<MoneyPack> moneyPack) {
				Log.d(TAG, "moneyPacksList = " + moneyPacksList);
				if (moneyPack != null && !moneyPack.isEmpty()) {
					connectGoogleServer();

				} else {
					Toast.makeText(context,
							"Fastcards server connection error",
							Toast.LENGTH_SHORT).show();
					finishActivity();
				}
			}
		}.execute();

	}

	private void connectGoogleServer() {
		Log.v(TAG, "connectGoogleServer");
		String base64EncodedPublicKey = getKey();

		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.w(TAG, "Problem setting up In-app Billing: " + result);
				} else {
					// Hooray, IAB is fully set up!

					Log.d(TAG, "Hooray, IAB is fully set up!: " + result);

					List<String> additionalSkuList = new ArrayList<String>();
					additionalSkuList.add(moneyPacksList.get(0).getPlaySku());
					additionalSkuList.add(moneyPacksList.get(1).getPlaySku());
					additionalSkuList.add(moneyPacksList.get(2).getPlaySku());
					additionalSkuList.add(moneyPacksList.get(3).getPlaySku());
					additionalSkuList.add(moneyPacksList.get(4).getPlaySku());
					mHelper.queryInventoryAsync(true, additionalSkuList,
							mQueryInventoryFinishedListener);
				}
			}
		});
	}

	IabHelper.QueryInventoryFinishedListener mQueryInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,	Inventory inventory) {
			Log.v(TAG, "mQueryFinishedListener result = " + result
					+ " inventory " + inventory);

			// checkForUnconsumedItems();

			Log.v(TAG, "mQueryFinishedListener");
			if (result.isFailure()) {
				Log.e(TAG, "result.isFailure() " + result.isFailure() + " "
						+ result);
//				return;
			}
			
			if (inventory != null)
				checkForAvailableItems(inventory);

			getPricesFromServer(inventory);
			initializeUi();

		}
	};

	private void getPricesFromServer(Inventory inventory) {
		Log.v(TAG, "Items prices");
		for (MoneyPack pack : moneyPacksList) {

			String sku = pack.getPlaySku();
			Log.i(TAG, " sku " + sku);

			SkuDetails skuDetails = inventory.getSkuDetails(sku);
			Log.i(TAG, " skuDetails " + skuDetails);

			if (skuDetails != null)
				pack.setPrice(skuDetails.getPrice());
			Log.d(TAG, pack.getPlaySku() + " " + pack.getPrice());
		}
		// update the UI

	}

	protected void checkForAvailableItems(Inventory inventory) {
		List<Purchase> purchasesList = new ArrayList<Purchase>();
		for (MoneyPack moneyPack : moneyPacksList) {
			String sku = moneyPack.getPlaySku();
			Purchase purchase = inventory.getPurchase(sku);

			if (purchase != null && verifyDeveloperPayload(purchase)) {
				Log.d(TAG, "We have " + sku + ". Consuming it.");
				purchasesList.add(inventory.getPurchase(sku));
			}
		}
		if (!purchasesList.isEmpty())
			mHelper.consumeAsync(purchasesList, mConsumeMultiFinishedListener);
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		return true; // payload.equals(account.getUserEmail(context));
	}

	private String getKey() {
		return getString(R.string.str_bil_key1)
				+ getString(R.string.str_bil_key2)
				+ getString(R.string.str_bil_key3)
				+ getString(R.string.str_bil_key4);
	}

	private void startProgressDialog() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(R.string.str_loading_title);
		progressDialog.setMessage(getString(R.string.str_loading_stars_body));
		progressDialog.show();
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
	}

	private void finishActivity() {
		finish();
		Toast.makeText(context, getString(R.string.str_no_network_connection),
				Toast.LENGTH_SHORT).show();

	}

	private void initializeUi() {
		progressDialog.dismiss();
		BitmapLoaderAsyncTask loader = new BitmapLoaderAsyncTask(context, null,
				false, false);

		ProgressBar pb0 = (ProgressBar) findViewById(R.id.pb_item0);
		pb0.setVisibility(View.VISIBLE);
		ImageView iv0 = (ImageView) findViewById(R.id.iv_item0);
		loader.loadImageAsync(moneyPacksList.get(0).getImage(), iv0, pb0);
		TextView tv0 = (TextView) findViewById(R.id.tv_item0);
		tv0.setText(moneyPacksList.get(0).getPrice());
		LinearLayout item0 = (LinearLayout) findViewById(R.id.ll_item0);
		item0.setOnClickListener(onSellingItemClickListener(0));

		ProgressBar pb1 = (ProgressBar) findViewById(R.id.pb_item1);
		pb1.setVisibility(View.VISIBLE);
		ImageView iv1 = (ImageView) findViewById(R.id.iv_item1);
		loader.loadImageAsync(moneyPacksList.get(1).getImage(), iv1, pb1);
		TextView tv1 = (TextView) findViewById(R.id.tv_item1);
		tv1.setText(moneyPacksList.get(1).getPrice());
		LinearLayout item1 = (LinearLayout) findViewById(R.id.ll_item1);
		item1.setOnClickListener(onSellingItemClickListener(1));

		ProgressBar pb2 = (ProgressBar) findViewById(R.id.pb_item2);
		pb2.setVisibility(View.VISIBLE);
		ImageView iv2 = (ImageView) findViewById(R.id.iv_item2);
		loader.loadImageAsync(moneyPacksList.get(2).getImage(), iv2, pb2);
		TextView tv2 = (TextView) findViewById(R.id.tv_item2);
		tv2.setText(moneyPacksList.get(2).getPrice());
		LinearLayout item2 = (LinearLayout) findViewById(R.id.ll_item2);
		item2.setOnClickListener(onSellingItemClickListener(2));

		ProgressBar pb3 = (ProgressBar) findViewById(R.id.pb_item3);
		pb3.setVisibility(View.VISIBLE);
		ImageView iv3 = (ImageView) findViewById(R.id.iv_item3);
		loader.loadImageAsync(moneyPacksList.get(3).getImage(), iv3, pb3);
		TextView tv3 = (TextView) findViewById(R.id.tv_item3);
		tv3.setText(moneyPacksList.get(3).getPrice());
		LinearLayout item3 = (LinearLayout) findViewById(R.id.ll_item3);
		item3.setOnClickListener(onSellingItemClickListener(3));

		ProgressBar pb4 = (ProgressBar) findViewById(R.id.pb_item4);
		pb4.setVisibility(View.VISIBLE);
		ImageView iv4 = (ImageView) findViewById(R.id.iv_item4);
		loader.loadImageAsync(moneyPacksList.get(4).getImage(), iv4, pb4);
		TextView tv4 = (TextView) findViewById(R.id.tv_item4);
		tv4.setText(moneyPacksList.get(4).getPrice());
		LinearLayout item4 = (LinearLayout) findViewById(R.id.ll_item4);
		item4.setOnClickListener(onSellingItemClickListener(4));
	}

	private OnClickListener onSellingItemClickListener(final int index) {
		final Activity activity = this;
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v(TAG, "onSellingItemClickListener");
				String sku = moneyPacksList.get(index).getPlaySku();
				// Toast.makeText(context, sku,
				// Toast.LENGTH_SHORT).show();//moneyPacksList.
				try {
					mHelper.launchPurchaseFlow(activity, sku,
							Constants.REQUEST_BUY, mPurchaseFinishedListener,
							getPayload());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private String getPayload() {
		payload = UUID.randomUUID().toString();
		return payload;
	}

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.v(TAG, "mPurchaseFinishedListener");
			if (result.isFailure()) {
				Log.d(TAG, "Error purchasing: " + result);
				// result.getResponse();
				// if (result.getResponse() == 7)
				// consumeItem(purchase);
				// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				Toast.makeText(context,
						"Error purchasing: " + result.getResponse(),
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				Log.d(TAG, "result = " + result + " purchase = " + purchase);
				// consumeItem(purchase);
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};

	/**
	 * Updates info on fastcards server and then send consume item request to
	 * google server
	 * 
	 * @param purchase
	 */
	private void consumeItem(final Purchase purchase) {
		Log.v(TAG, "let's consumeItem " + purchase.getSku());
		new AsyncTask<Object, Object, Boolean>() {
			final Api api = Api.getInstanse(context);

			// Inventory inventory;

			@Override
			protected Boolean doInBackground(Object... params) {
				try {
					Log.v(TAG, "api.buyMoneyPack(purchase.getSku())");
					boolean result = api.buyMoneyPack(purchase.getSku());
					Log.i(TAG, " result = " + result);
					return result;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				Log.d(TAG, "onPostExecute result = " + success);
				if (success) {
					consumeLocally(purchase);
				} else {
					Log.e(TAG, "Fastcards server connection error");
				}
			}

		}.execute();
	};

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.v(TAG, "onConsumeFinished " + result);
			consumeItem(purchase);

			Log.d(TAG,
					"result =" + result + " result.isSuccess() "
							+ result.isSuccess());
			if (result.isSuccess()) {
			} else {
				Log.e(TAG, "mConsumeFinishedListener error "+result.getResponse());
			}
		}
	};

	IabHelper.OnConsumeMultiFinishedListener mConsumeMultiFinishedListener = new IabHelper.OnConsumeMultiFinishedListener() {

		@Override
		public void onConsumeMultiFinished(List<Purchase> purchases,
				List<IabResult> results) {
			Log.v(TAG, "onConsumeMultiFinished " + results);
			int index = 0;
			for (IabResult result : results) {

				if (result.isSuccess()) {
					addToLocalUnconsumedItemsStore(purchases.get(index));
					consumeItem(purchases.get(index));
				} else {
					Log.e(TAG, "mConsumeMultiFinishedListener error "+result.getResponse());
				}
				index++;
			}
		}
	};

	private void addToLocalUnconsumedItemsStore(Purchase purchase) {
		Log.v(TAG, "addToLocalUnconsumedItemsStore " + purchase.getSku() + " "+ purchase.getDeveloperPayload());
		DataBaseHelper.getInstance(context).saveStarPurchase(purchase);
	}

	private void consumeLocally(Purchase purchase) {
		account.refreshWealth(context);
		DataBaseHelper.getInstance(context).removeStarPurchase(	purchase.getDeveloperPayload());
		
		Log.v(TAG,"consumeLocally " + purchase.getSku() + " "+purchase.getDeveloperPayload());
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.wealth, menu);
		MenuItem item = menu.findItem(R.id.action_wealth);

		WealthView wealthView = WealthView.getInstanse(this);
		MenuItemCompat.setActionView(item, wealthView);
		wealthView.refreshWealth();
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_wealth: {
			return true;
		}
		case android.R.id.home:
				finish();
				return true;				
		default: {
		}
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","+ data);
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			Log.d(TAG, "!mHelper.handleActivityResult(requestCode, resultCode, data)");

		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
