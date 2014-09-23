package ru.fastcards;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.common.Notification;
import ru.fastcards.common.SimpleItem;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListSelectorActivity extends TrackedActivity {
	protected static final String TAG = "ListSelectorActivity";
	private List<String[]> categories = new ArrayList<String[]>();
//	private String[] category;
	private Context context;
//	private String[] arrayIds;
	private ListView listView;
	private List<SimpleItem> itemsList;
	private SimpleItemAdapter adapter;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;
		
		setContentView(R.layout.activity_listview);
		listView = (ListView) findViewById(R.id.lv_recipients);
		
		Log.d(TAG, "ListSelectorActivity CREATED");

		getItems(getIntent().getStringExtra(Constants.EXTRA_TYPE));
		
	}

	private void getItems(String type) {		
		final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		if (Constants.EXTRA_CATEGORY_ID.equals(type)){
			itemsList = (List<SimpleItem>)(List<?>) dbHelper.getCategoriesList(getIntent().getStringExtra(Constants.EXTRA_CATEGORY_GROUP));
			
			check();
			
			Utils.checkForUpdate(context, Constants.VERSIONS_CATEGORIES, new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					if (msg.what == 1){
						itemsList = (List<SimpleItem>)(List<?>) dbHelper.getCategoriesList(getIntent().getStringExtra(Constants.EXTRA_CATEGORY_GROUP));
						Log.v("getItems", "arrayItems "+itemsList.size());							
						check2();

					}
					return false;
				}


			});
		
		}
		if (Constants.EXTRA_CATEGORY_GROUP.equals(type)){
			
			itemsList = (List<SimpleItem>)(List<?>) dbHelper.getCategoryGroupsList();
			check();
			
//			adapter  = new CategoryGroupAdapter(context, itemsList);
			Utils.checkForUpdate(context, Constants.VERSIONS_GROUPS, new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					if (msg.what == 1){
						itemsList = (List<SimpleItem>)(List<?>) dbHelper.getCategoryGroupsList();
						check2();
					}
					return false;
				}
			});
		}
		
		if(Constants.EXTRA_NOTIFICATION.equals(type)){
			String[] namesArray = context.getResources().getStringArray(R.array.array_notification_timers);
			itemsList = new ArrayList<SimpleItem>();
			for (int i= 0; i < namesArray.length; i++){
//				idsArray[i] = Integer.toString(i);
				itemsList.add(new Notification(Integer.toString(i), namesArray[i]));
			}
//			adapter  = new SimpleItemAdapter(context, itemsList);
			setListView();
		}


	}

	private void setListView() {
		adapter  = new SimpleItemAdapter(context, itemsList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);		
	}

	private void check() {
		if (itemsList.isEmpty())
		{
			dialog = new ProgressDialog(context);
			dialog.setMessage(getString(R.string.loading_categories));
			dialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			dialog.show();
		}else {
//			adapter  = new SimpleItemAdapter(context, itemsList);
			setListView();
		}
		
	}
	
	private void check2() {
		if (dialog != null){
			dialog.dismiss();
//			adapter  = new SimpleItemAdapter(context, itemsList);
			setListView();
		}else
			adapter.notifyDataSetChanged();
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

	    case android.R.id.home:
	    	finish();
	        return true;
		default: {
		}
			return super.onOptionsItemSelected(item);
		}
	}
	

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = getIntent();
			intent.putExtra(Constants.EXTRA_ID, itemsList.get(position).getId());
			setResult(RESULT_OK, intent);
			finish();
		}
	};
	
//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		Intent intent = getIntent();
//		
////		System.out.println("onListItemClick position "+position+" "+arrayIds[position]);
//		intent.putExtra(Constants.EXTRA_ID, itemsList.get(position).getId());
//		setResult(RESULT_OK, intent);
//		finish();
//	}
}
