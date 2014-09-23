package ru.fastcards.recipientselectors;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.TrackedActivity;
import ru.fastcards.common.Recipient;
import ru.fastcards.manager.ChooserItemsCursorAdapter;
import ru.fastcards.manager.ChooserSocialFriendCursorAdapter;
import ru.fastcards.manager.CursorSelectorAdapter;
import ru.fastcards.social.api.FbApi;
import ru.fastcards.social.api.KException;
import ru.fastcards.social.api.Params;
import ru.fastcards.social.api.VkApi;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.MyCursorLoader;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SeceltMultipleContactsActivity extends TrackedActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnQueryTextListener {

	private static final String TAG = "ManageContactFragment";
	private View contentView;
	private ListView listView;

	private CursorSelectorAdapter adapter;
	private Context context;

	private MenuItem joinMenuAction;
	private Cursor cursor;
	private Set<String> selectedRecipientsIds = new HashSet<String>();
	
	private MenuItem searchMenuItem;
	private String searchTerm;
	private ProgressBar pb;
	private String comType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.activity_listview_progress, null,false);
		
		pb = (ProgressBar) contentView.findViewById(R.id.pb_loading);
		setContentView(contentView);
		context = this;

		getExtras();
		
		setAdapter();
		setListView();
		getSupportLoaderManager().initLoader(0, null, this);
	}

	private void setAdapter() {		
		if(comType.equals(Constants.COMUNICATION_TYPE_VK) 
				|| comType.equals(Constants.COMUNICATION_TYPE_FB)){
			adapter = new ChooserSocialFriendCursorAdapter(context);
		}
		else {
			adapter = new ChooserItemsCursorAdapter(context);
		}
		
	}

	private void getExtras(){
		Intent intent = getIntent();
		comType = intent.getStringExtra(Constants.EXTRA_COMUNICATION_TYPE);	
		
		Log.v(TAG, "comType "+comType);
	}

	private void setListView(){
		listView = (ListView) contentView.findViewById(R.id.lv_recipients);
		listView.setOnItemClickListener(itemClickListener);
		listView.setAdapter(adapter);
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				Log.d(TAG, "onItemClick "+id);

				cursor.moveToPosition(position);
				String uuid = cursor.getString(cursor.getColumnIndex("_id"));
				long longId = UUID.fromString(uuid).getMostSignificantBits();
			
				if (adapter.isItemSelected(longId)) {
					adapter.unselectItem(longId);
					selectedRecipientsIds.remove(uuid);
				} else {
					adapter.selectItem(longId);
//					cursor.moveToPosition(position);
					selectedRecipientsIds.add(uuid);
				}
				if (selectedRecipientsIds.isEmpty())
					joinMenuAction.setVisible(false);
				else
					joinMenuAction.setVisible(true);
				
				adapter.notifyDataSetChanged();			
		}
	};
	private String[] idsArray;


	private static final String COLUMNS = "Contacts._id, Contacts.Name, Contacts.ThumbUri";

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		setProgressBar();
		
		String where  = Constants.TABLE_COMUNICATION+".Type = "+"'"+comType+"' AND "+Constants.TABLE_COMUNICATION+".PMRY = "+1;
//		if (comType != null){
//			where = Constants.TABLE_COMUNICATION+".Type" + " = "+"'" + comType + "'";
//	}	
		if (searchTerm != null && !searchTerm.equals("")){
			where += " AND "+DataBaseHelper.FIELD_NAME_SEARCH + " LIKE "+"'%" + searchTerm.toUpperCase() + "%'";
		}	
		
		//Добавить priority при заполнении базы, а здесь учитывать только Prioruty = 1

		Log.d(TAG, "onCreateLoader where = "+where);
		String query = "SELECT "+COLUMNS+" FROM "+Constants.TABLE_CONTACTS + " LEFT OUTER JOIN "+ Constants.TABLE_COMUNICATION + 
				" ON Contacts._id = TableComunication.ContactID WHERE "+where;
		return new MyCursorLoader(context, query); //"Name" + " ASC"
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.v(TAG, "data.getCount() "+data.getCount());
		Log.i(TAG, "data.columns "+Arrays.toString(data.getColumnNames()));
//		for (int i = 0; i < 10; i++)
//			System.out.println("info "+);
		if (data.getCount() == 0 && (searchTerm == null || searchTerm.equals("")))
			loadRecipients();		
//		if (data != null && data.getCount() != 0){
			cursor = data;
			adapter.swapCursor(data);
//			data.close();
			dismissProgressBar();
//		}
		
	}

	private void setProgressBar() {
		if (cursor == null || cursor.getCount() == 0){
			pb.setVisibility(View.VISIBLE);
		}		
	}
	
	private void dismissProgressBar() {
		if (pb != null)
			pb.setVisibility(View.GONE);
	}

	private void loadRecipients() {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		List<Recipient> recList = dbHelper.getRecipientsListByComunicationType(comType);
		if (recList == null || recList.size() == 0){
			setProgressBar();
			if (comType.equals(Constants.COMUNICATION_TYPE_FB)){
				new AsyncTask<Params, String, Result>(){
					@Override
					protected Result doInBackground(Params... arg0) {
						FbApi fbApi = new FbApi();
						try {
							fbApi.importFriendsToDataBase(context);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (KException e) {
							e.printStackTrace();
						}
						return null;
					}					
					protected void onPostExecute(Result result) {	
						dismissProgressBar();
						restartLoader();
					}					
				}.execute();
			}
			
			if (comType.equals(Constants.COMUNICATION_TYPE_VK)){
				new AsyncTask<Params, String, Result>() {

					private Account account = Account.getInstance();

					@Override
					protected Result doInBackground(Params... params) {
						try {
							String uid = account .getVkontakteUserId();
							VkApi vkApi = new VkApi(account.getVkontakteToken(),uid);	
							String fields = "first_name,last_name,mobile_phone,contacts,sex,photo_200_orig,bdate";
							vkApi.importFriendsToDataBase(uid, fields, context);

						} catch (MalformedURLException e){
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e){
							e.printStackTrace();
						} 
						catch (KException e) {
							e.printStackTrace();
						}
						return null;
					}
					
					protected void onPostExecute(Result result) {	
						dismissProgressBar();
						restartLoader();
					}						
				}.execute();
			}
		}
	}
	
	private void restartLoader() {	
	      getSupportLoaderManager().restartLoader(0, null, this);
	};

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private void startSelectedContactsDisplayerAcivity() {
		Intent intent = new Intent(this, SelectedContactsDisplayerAcivity.class);
		ArrayList<String> selectedRecipientsIdsList = new ArrayList<String>();
		
		for (String id : selectedRecipientsIds)
			selectedRecipientsIdsList.add(id);
				
//		int count = 0;
//		for (String id : selectedRecipientsIds)
//			selectedRecipientsIdsArray.get(count++) = id;
//		idsArray = selectedRecipientsIdsArray;
		
		intent.putStringArrayListExtra(Constants.EXTRA_RECIPIENTS_IDS, selectedRecipientsIdsList);
		intent.putExtra(Constants.EXTRA_COMUNICATION_FILTER, comType);
		
//		setResult(RESULT_OK, intent);
		startActivityForResult(intent, Constants.REQUEST_CONTACTS);
//		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_done, menu);
		joinMenuAction = menu.getItem(1);
		// doneMenuAction = menu.getItem(2);		
	       
		searchMenuItem = menu.findItem(R.id.action_search_menu);	      
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);	     
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done: {
			startSelectedContactsDisplayerAcivity();
			setResult(RESULT_OK);
			return true;
		}

		default: {
		}
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		searchTerm = newText;
		Log.i(TAG, "searchTetm "+searchTerm);
		
      getSupportLoaderManager().restartLoader(0, null, this);
		return false;
	}

	
	@Override
	public boolean onQueryTextSubmit(String arg0) {
	     if (searchMenuItem != null) {
         }
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_CONTACTS: {
				String ids[] = data.getStringArrayExtra(Constants.EXTRA_ID);
				Log.e(TAG, "ids "+Arrays.toString(ids));
				Intent intent = new Intent();
				intent.putExtra(Constants.EXTRA_RECIPIENTS_IDS, ids);
				intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE, comType);
				setResult(RESULT_OK, intent);
				finish();

			}
				break;
			
			}
		}
	}
	
}
