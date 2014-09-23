package ru.fastcards.recipientselectors;

import java.util.Arrays;
import java.util.List;

import ru.fastcards.R;
import ru.fastcards.SwipeDismissListViewTouchListener;
import ru.fastcards.TrackedActivity;
import ru.fastcards.common.Comunication;
import ru.fastcards.manager.CursorSelectorAdapter;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.MyCursorLoader;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectedContactsDisplayerAcivity extends TrackedActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = "SelectedContactsDisplayerAcivity	";
	// private View contentView;
	private ListView listView;

	private CursorSelectorAdapter mAdapter;
	private Context context;

	private List<String> idsArray;
	private String comType;
	private Cursor cursor;
	private DataBaseHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		// contentView = inflater.inflate(R.layout.activity_listview_progress, null, false);
		setContentView(R.layout.activity_listview_progress);
		context = this;
		dbHelper = DataBaseHelper.getInstance(context);
		// pb = (ProgressBar) contentView.findViewById(R.id.pb_loading);

		getExtras();

		setAdapter();
		setListView();
		getSupportLoaderManager().initLoader(0, null, this);
	}

	private void setAdapter() {
		mAdapter = new ChooserItemsInfoCursorAdapter(context, comType);
	}

	private void getExtras() {
		Intent intent = getIntent();
		idsArray = intent.getStringArrayListExtra(Constants.EXTRA_RECIPIENTS_IDS);
		comType = intent.getStringExtra(Constants.EXTRA_COMUNICATION_FILTER);
		Log.v(TAG, "getExtras comType "+comType);
	}

	private void setListView() {
		listView = (ListView) findViewById(R.id.lv_recipients);
		if (Constants.COMUNICATION_TYPE_PHONE.equals(comType) || Constants.COMUNICATION_TYPE_EMAIL.equals(comType))
			listView.setOnItemClickListener(itemClickListener);
		listView.setAdapter(mAdapter);
		
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return idsArray.size() > 1; 
					}

					@Override
					public void onDismiss(ListView listView,int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							cursor.moveToPosition(position);
							idsArray.remove(cursor.getString(0));
							refreshAdapter();
						}
					}

				});
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling, we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
	}


//	private void removeRecFromList(String recId) {
//		for (int i = 0; i < idsArray.length; i++)
//			if (recId.equals(idsArray[i])){
////				id
//			}
//		
//	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			cursor.moveToPosition(position);
			String recId = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FIELD_UUID));

			Log.d(TAG, "onItemClick " + recId);
			showSelectPrimaryContactDialog(recId);
		}
	};

	ArrayAdapter<Comunication> comListAdapter;// = new ComAdapter(context, comList);
	List<Comunication> comList;// = dbHelper.getComunicationsList(recId, comType);

	private void showSelectPrimaryContactDialog(final String recId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Select primary contact");

		ListView view = new ListView(context);
		comList = dbHelper.getComunicationsList(recId, comType);
		comListAdapter = new ComAdapter(context, comList);
		view.setAdapter(comListAdapter);

		builder.setView(view);
		final Dialog dialog = builder.create();		
		
		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dbHelper.setPrimaryComunication(recId, comType, comList.get(position).getUuid());
				comList = dbHelper.getComunicationsList(recId, comType);
				refreshAdapter();
				comListAdapter.notifyDataSetChanged();
				dialog.dismiss();				
			}
		});
		
		dialog.show();
	}

	class ComAdapter extends ArrayAdapter<Comunication> {

		public ComAdapter(Context context, List<Comunication> comList) {
			super(context, R.layout.raw_simple_chooser_multiple, comList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.raw_simple_chooser_multiple, null);

				holder = new ViewHolder();
				holder.comTv = (TextView) convertView.findViewById(R.id.list_item_title);
				holder.checkerIv = (ImageView) convertView.findViewById(R.id.iv_check);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Comunication item = getItem(position);

			holder.comTv.setText(item.getInfo());
			holder.checkerIv.setVisibility(item.isPrimaty() ? View.VISIBLE : View.INVISIBLE);

//			Log.v("ComAdapter", "getView  " + item.getInfo() + " " + item.isPrimaty());
			return convertView;
		}

		class ViewHolder {
			ImageView checkerIv;
			TextView comTv;
		}

	}

	private static final String COLUMNS = "Contacts._id, Contacts.Name, Contacts.ThumbUri, TableComunication.Type, TableComunication.URI_WHAT";

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// setProgressBar();

		// String where = Constants.TABLE_COMUNICATION+".Type" + " = "+"'" + filter + "' AND Contacts._id in (" + convertToCommaDelimitedString(idsArray) + ")";

		String where = "_id in (" + getCommaDelimitedIdsString() + ")";

		Log.d(TAG, "onCreateLoader where = " + where);
		// String query = "SELECT "+COLUMNS+" FROM "+Constants.TABLE_CONTACTS + " LEFT OUTER JOIN "+ Constants.TABLE_COMUNICATION +
		// " ON Contacts._id = TableComunication.ContactID WHERE "+where;
		String query = "SELECT _id,Name FROM " + Constants.TABLE_CONTACTS + " WHERE " + where;
		return new MyCursorLoader(context, query); // "Name" + " ASC"
	}

	private String getCommaDelimitedIdsString() {
		String idsString = "";
		for (String id : idsArray) {
			idsString += "'" + id + "',";
		}
		idsString = idsString.substring(0, idsString.length() - 1);
		Log.d(TAG, "idsString " + idsString);
		return idsString;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.v(TAG, "data.getCount() " + data.getCount());
		Log.i(TAG, "data.columns " + Arrays.toString(data.getColumnNames()));

		if (data != null && data.getCount() != 0) {
			mAdapter.swapCursor(data);
			cursor = data;
		}

	}

	private void refreshAdapter() {
		getSupportLoaderManager().restartLoader(0, null, this);
	};

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private void returnResult() {
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_ID, idsArray.toArray(new String[0]));
		setResult(RESULT_OK, intent);
		
		Log.d(TAG, "returnResult "+Arrays.toString(idsArray.toArray(new String[0])));
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.done, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done: {
			returnResult();
			setResult(RESULT_OK);
			return true;
		}

		default: {
		}
			return super.onOptionsItemSelected(item);
		}
	}

}
