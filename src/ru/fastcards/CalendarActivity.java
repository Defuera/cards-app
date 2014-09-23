package ru.fastcards;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.fastcards.manager.ManageEventActivity;
import ru.fastcards.social.api.VkLoginActivity;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.TableCursorLoader;
import ru.fastcards.utils.Utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLoginListener;

public class CalendarActivity extends TrackedActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "CalendarActivity";
	private Context context;
	// private List<Event> eventsList = new ArrayList<Event>();
	private EventsCursorAdapter mAdapter;
	private ListView eventsListView;
	private String filter = null; //Constants.EVENT_TYPE_COMMON_HOLIDAYS;

	private Cursor cursor;
	private Account account;
	private SimpleFacebook mSimpleFacebook = SimpleFacebook.getInstance(this);

	private boolean selectionMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		context = this;
		account = Account.getInstance();

		setListView();

		getSupportLoaderManager().initLoader(0, null, this);
		
		checkForNewHolidays();
	}

	private void checkForNewHolidays() {
		Utils.checkForUpdate(context, Constants.VERSIONS_CALENDAR, new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == 1){
					refreshAdapter();
				}
				return false;
			}
		});
		
	}
	
	private void refreshAdapter() {
		getSupportLoaderManager().restartLoader(0, null, this);	
	}

	private void setListView() {
		eventsListView = (ListView) findViewById(R.id.lv_events);
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				eventsListView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					int position;
					@Override
					public boolean canDismiss(int position) {
						this.position = position;
						cursor.moveToPosition(position);
						return !cursor.getString(cursor.getColumnIndex("Type")).equals(Constants.EVENT_TYPE_COMMON_HOLIDAYS);//.getSposition != (listView.getHeaderViewsCount() - 1);
					}

					@Override
					public void onDismiss(ListView listView, int[] reverseSortedPositions) {
//						for (int position : reverseSortedPositions) {
						cursor.moveToPosition(position);
							DataBaseHelper.getInstance(context).removeEvent(cursor.getString(cursor.getColumnIndex("_id")));
							refreshAdapter();
//							onItemRemove();
//						}
//						if (itemsList.isEmpty()) {
//							adapter.notifyDataSetChanged();
//						}
					}
				});
		eventsListView.setOnTouchListener(touchListener);
		eventsListView.setOnScrollListener(touchListener.makeScrollListener());
		
		eventsListView.setOnItemClickListener(onEventClickListener);
//		eventsListView.setOnItemLongClickListener(onEventLongClickListener);
		mAdapter = new EventsCursorAdapter(context);
		eventsListView.setAdapter(mAdapter);
	}
	

	private OnItemLongClickListener onEventLongClickListener = new OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//			view.setBackgroundColor(context.getResources().getColor(R.color.text_orange));
			String eventId = cursor.getString(cursor.getColumnIndex("_id"));
			Log.e(TAG, "event selected "+eventId);
			cursor.moveToPosition(position);
			mAdapter.setSelectedItem(eventId);
			refreshAdapter();
			selectionMode = true;
			return false;
		}
		
	};
	
	private OnItemClickListener onEventClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (!selectionMode){
				String categoryId = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FIELD_CATEGORY_ID));
				String eventId = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FIELD_UUID));
				startThemeSelectorActivity(categoryId, eventId);
			}
		}		
	};
	private MenuItem vkMenuItem;
	private MenuItem fbMenuItem;
	
	private void startThemeSelectorActivity(String categoryId, String eventId) {
		System.out.println("startShopActivity "+categoryId);
		Intent intent = new Intent(this, ThemeSelectorActivity.class);
		intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
		intent.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
		startActivity(intent);		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		SimpleDateFormat simpleD = new SimpleDateFormat("D");		
		int todayMillis = Integer.parseInt(simpleD.format(new Date()));
		int inMonthMillis = todayMillis + 30;
		
		Log.d(TAG, "todayMillis "+todayMillis);
		
		String where = null;
		where  = DataBaseHelper.FIELD_DATE_2 + " >= " + "'" + (todayMillis) + "' AND "+
		DataBaseHelper.FIELD_DATE_2 + " < " + "'" + (inMonthMillis)+ "'";
		
		// if (searchTerm != null && !searchTerm.equals("")){
		// searchTerm = searchTerm.toUpperCase();
		// where = DataBaseHelper.FIELD_NAME_SEARCH + " LIKE "+"'%" + searchTerm + "%'";
		// }

		if (filter != null) {
//			where = DataBaseHelper.FIELD_TYPE + " = " + "'" + filter + "'";
			where += " AND "+DataBaseHelper.FIELD_TYPE + " = " + "'" + filter + "'";
		}

		Log.v(TAG, "where "+where);
		return new TableCursorLoader(this, Constants.TABLE_EVENTS, where, "Date2" + " ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.cursor = data;
		Log.d(TAG, "onLoadFinished " + data.getCount()+" events loaded");
		if (data.getCount() == 0 && Constants.EVENT_TYPE_BIRTHDAY.equals(filter))
			checkForSocialNetwork();
		if (data != null)
			mAdapter.swapCursor(data);
	}

	private void checkForSocialNetwork() {
		if (!account.hasFbToken() || !account.hasVkToken())
			createLoginDialog(account.hasVkToken(), account.hasFbToken());		
	}

//	private void startLoginDialog() {
//		if (!account.hasFbToken() && !account.hasVkToken())
//			createLoginDialog(true, true);
//		else if (!account.hasVkToken())
//			createLoginDialog(true, false);
//				else if (!account.hasFbToken()) 
//					createLoginDialog(false, true);
//	}

	private void createLoginDialog(boolean loggedInVk, boolean loggedInFb) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getString(R.string.str_title_log_in));
		builder.setMessage(getString(R.string.str_calendar_import_friends_msg));
		
		builder.setNegativeButton(getString(R.string.str_cancel), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		if (!loggedInVk)
		builder.setNeutralButton(getString(R.string.str_vkontakte), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				logInVk();
			}
		});
		
		if (!loggedInFb)
		builder.setPositiveButton(getString(R.string.str_facebook), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSimpleFacebook.login(onFbLoginListener);
			}
		});

		builder.create().show();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	public void OnBdayFilterClick(View v) {
		filter = Constants.EVENT_TYPE_BIRTHDAY;
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	public void OnHdayFilterClick(View v) {
		filter = Constants.EVENT_TYPE_COMMON_HOLIDAYS;
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	public void OnPersonalFilterClick(View v) {
		filter = Constants.EVENT_TYPE_CUSTOM;
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	public void OnAllFilterClick(View v) {
		filter = null;
		getSupportLoaderManager().restartLoader(0, null, this);

	}

	private void startManageEventActivity(String eventId, int requestCode) {
		Intent intent = new Intent(this, ManageEventActivity.class);
		if (eventId != null)
			intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
		startActivityForResult(intent, requestCode);
		
	}
	
	private void setFbActionButton() {
		if (account.hasFbToken()){
			fbMenuItem.setTitle("Refresh fb friends");	
			fbMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					refreshFriendsData(Constants.COMUNICATION_TYPE_FB);
					return false;
				}
			});
		}
		else{
			fbMenuItem.setTitle("Import fb friends");
			fbMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					mSimpleFacebook.login(onFbLoginListener);
					return false;
				}
			});
		}		
	}

	OnLoginListener onFbLoginListener = new SimpleFacebook.OnLoginListener() {

		@Override
		public void onFail(String reason) {
			Log.w(TAG, reason);
		}

		@Override
		public void onException(Throwable throwable) {
			Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking() {
			Log.i(TAG, "In progress");
		}

		@Override
		public void onLogin() {
			// account.save(context);
			final Handler handler2 = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					refreshAdapter();
				}
			};
			Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_FB, handler2);
			setFbActionButton();
		}

		@Override
		public void onNotAcceptingPermissions() {
			Log.w(TAG, "User didn't accept read permissions");
		}

	};

	private void logInVk() {
		Intent intent = new Intent(context, VkLoginActivity.class);
		startActivityForResult(intent, Constants.REQUEST_VK_AUTH);
	}

	private void refreshFriendsData(final String comType) {
		final Handler handler2 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				refreshAdapter();
			}
		};
		
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Utils.importFriendsToDataBase(context, comType, handler2);
			}
		};
		Utils.deleteRecFromDataBase(context, comType, handler);
	}
	
	private void setVkActionButton() {
		if (account.hasVkToken()){
			vkMenuItem.setTitle("Refresh vk friends");		
			vkMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					refreshFriendsData(Constants.COMUNICATION_TYPE_VK);
					return false;
				}
			});
		}
		else{
			vkMenuItem.setTitle("Import vk friends");
			vkMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					logInVk();
					return false;
				}
			});
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.events, menu);		

		vkMenuItem = menu.getItem(1);
		setVkActionButton();
		fbMenuItem = menu.getItem(2);
		setFbActionButton();		

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add: {
			startManageEventActivity(null, Constants.REQUEST_CREATE_EVENT);
			return true;
		}
		default:{
		}
			return super.onOptionsItemSelected(item);
		}
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SimpleFacebook.getInstance().onActivityResult(this, requestCode, resultCode, data);
		System.out.println("onActivityResult "+requestCode+" "+resultCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_CREATE_EVENT:
			{
				if (resultCode == RESULT_OK) {
					refreshAdapter();
				}
			}
				break;
			case Constants.REQUEST_VK_AUTH: {
				Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						setVkActionButton();
						refreshAdapter();
						super.handleMessage(msg);
					}
				};
				Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_VK, handler);
			}
//			case Constants.REQUEST_MANAGE_EVENT:
//				if (resultCode == RESULT_OK) {
//					
//					Event event = getExtraEvent(data);
//					int location = findEvent(event.getId());
//					if (location != -1){
//						eventsList.set(location, event);
//						refreshEventsAdapter();
//					}
//				}
//				break;
			default:
				break;
			}
		}

	}
	
}
