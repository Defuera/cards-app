//
//
//package ru.fastcards;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//
//import ru.fastcards.common.CategoryGroup;
//import ru.fastcards.common.Event;
//import ru.fastcards.common.SimpleItem;
//import ru.fastcards.manager.ManageEventActivity;
//import ru.fastcards.utils.Constants;
//import ru.fastcards.utils.DataBaseHelper;
//import ru.fastcards.utils.Utils;
//import android.app.NotificationManager;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnCancelListener;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView.OnItemLongClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.ExpandableListView;
//import android.widget.ExpandableListView.OnChildClickListener;
//import android.widget.ExpandableListView.OnGroupClickListener;
//import android.widget.ListView;
//
//public class EventsActivity extends TrackedActivity {
//	private static final String TAG = "EventsActivity";
//	private Context context;
//	private ExpandableListView eventsListView;
//	private ListView categoryGroupListView;
//	private List<Event> eventsList = new ArrayList<Event>();
//	private List<EventsGroup> eventGroupsList = new ArrayList<EventsGroup>();
//	private List<CategoryGroup> categoryGroupsList = new ArrayList<CategoryGroup>();
//	private BaseExpandableListAdapter eventAdapter;
//	private ArrayAdapter<SimpleItem> categoryGroupAdapter;
//	private ProgressDialog dialog;	
//
//	private static final long dayInMillis = (1000*60*60*24);
//	private static final Date yesterday = new Date(System.currentTimeMillis() - dayInMillis);
//	private static final Date inMonth= new Date(System.currentTimeMillis() + (dayInMillis*30));
//	private static final Date inWeek= new Date(System.currentTimeMillis() + (dayInMillis*7));
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_events);
//		context = this;
//
//		loadCategoriesGroups();
//		
//		setEventsExpendableListView();		
//		
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		switch (item.getItemId())
//		{
//			case android.R.id.home:
//				super.onBackPressed();
//				break;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//		return false;
//	}
//
//	private void setEventsExpendableListView() {
//		eventsListView = (ExpandableListView) findViewById(R.id.explv_events_list);
//
//		getEventsList();
//
//		createEventGroups();
//		sortEventsByGroups();
//        
//        eventAdapter = new EventsListAdapter(getApplicationContext(), eventGroupsList);
//        eventsListView.setAdapter(eventAdapter);
//        for (int i = 0; i < eventGroupsList.size(); i++){
//        eventsListView.expandGroup(i);
//        }
//        
//        eventsListView.setOnGroupClickListener(new OnGroupClickListener() {
//        	  @Override
//        	  public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) { 
//        	    return true; // This way the expander cannot be collapsed
//        	  }
//        	});
//        
//        eventsListView.setOnChildClickListener(new OnChildClickListener() {
//		
//			@Override
//			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//				Event event = eventGroupsList.get(groupPosition).getEventsList().get(childPosition);
//				startThemeSelectorActivity(event.getCategoryId(), event.getId());
//				return false;
//			}
//		});		
//        
//        eventsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//                    final int groupPosition = ExpandableListView.getPackedPositionGroup(id);
//                    final int childPosition = ExpandableListView.getPackedPositionChild(id);
//    				final Event clickedEvent = eventGroupsList.get(groupPosition).getEventsList().get(childPosition);
//    				
//    				if (!Constants.EVENT_TYPE_COMMON_HOLIDAYS.equals(clickedEvent.getType())){
//            		PopupMenu pum = new PopupMenu(context, view);
//                    pum.inflate(R.menu.events);
//                    pum.show();
//                    pum.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//                		
//                		@Override
//                		public boolean onMenuItemClick(MenuItem item) {
//                			switch (item.getItemId()) {
//							case R.id.delete:
//							{
//								deleteEvent(childPosition, groupPosition);
//							}
//								break;
//							case R.id.edit:
//							{
//								startManageEventActivity(clickedEvent.getId(), Constants.REQUEST_MANAGE_EVENT);
//							}
//								break;
//							default:
//								break;
//							}                		
//                			return false;
//                		}
//                	});
//    				} else{
//    					view.setClickable(false);
//    				}
//                    return true;
//                }
//                return false;
//            }
//        });
//	}
//
//	/**
//	 * Deletes event from DataBase, refreshes adapter.
//	 * @param childPosition
//	 * @param groupPosition
//	 */
//	private void deleteEvent(int childPosition, int groupPosition) {
//		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
//		String eventId = eventGroupsList.get(groupPosition).getEventsList().get(childPosition).getId(); 
////				eventsList.get(childPosition).getUuid();
//		dbHelper.removeEvent(eventId);
//		
//		deleteNotification(dbHelper, eventId);
//		eventsList.remove(childPosition);
//		
//		eventGroupsList.get(groupPosition).removeEvent(childPosition);
//		eventAdapter.notifyDataSetChanged();
//		
//	}
//	
//	private void deleteNotification(DataBaseHelper dbHelper, String eventId) {
//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		mNotificationManager.cancel(dbHelper.getNotificationId(eventId));
//		
//	}
//
//	private void createEventGroups() {
//		eventGroupsList.add(new EventsGroup(getResources().getString(R.string.str_this_week), new ArrayList<Event>()));
//		eventGroupsList.add(new EventsGroup(getResources().getString(R.string.str_this_month), new ArrayList<Event>()));
//	}
//
//	private void sortEventsByGroups() {
//		
//		for (Event event : eventsList){
//			if (isDateThisWeek(event.getDate())){
////				Log.d(TAG, event.toString());
//				eventGroupsList.get(0).addEvent(event);
//			}
//			else {
//				if (isDateThisMonth(event.getDate())){
////					Log.d(TAG, event.toString());
//					eventGroupsList.get(1).addEvent(event);
//				}
//			}
//		}
//	}
//
//
//	/**
//	 * Checks whether the date is in range of given period (number of days) from today including today 
//	 * (F.E. if today is 1th of Jan and period = 7days true will be returned for 1st-7th of Jan)
//	 * @param date to compare
//	 * @param period number of days
//	 * @return
//	 */
//	@Deprecated 
//	public boolean isDateInPeriod(long dateMillis, int period) {
//		Date givenDate = new Date(dateMillis);
//		long dayInMillis = (1000*60*60*24);
//		Date yesterday = new Date(System.currentTimeMillis() - dayInMillis);
//		Date inPeriod= new Date(System.currentTimeMillis() + (dayInMillis*period));
//		
//		givenDate =  Utils.clearDate(givenDate);
//
//		return givenDate.after(yesterday) && givenDate.before(inPeriod);
//	}
//	
//	public boolean isDateThisWeek(long dateMillis) {
//		Date givenDate = new Date(dateMillis);
//		
//		givenDate =  Utils.clearDate(givenDate);
//		return givenDate.after(yesterday) && givenDate.before(inWeek);
//	}
//
//	public boolean isDateThisMonth(long dateMillis) {
//		Date givenDate = new Date(dateMillis);
//		
//		givenDate =  Utils.clearDate(givenDate);
//		return givenDate.after(yesterday) && givenDate.before(inMonth);
//	}
//	
//	private void loadCategoriesGroups() {
//		final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
//		categoryGroupsList = dbHelper.getCategoryGroupsList();
//		
//		if (categoryGroupsList.isEmpty()){
//			dialog = new ProgressDialog(context);
//			dialog.setMessage(getString(R.string.loading_events));			
//			dialog.setOnCancelListener(new OnCancelListener() {
//				
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					finish();
//				}
//			});
//			dialog.show();
//		}else{
//			categoryGroupAdapter = new SimpleItemAdapter(context, categoryGroupsList);
//			initGroupsListView();
//		}
//		Utils.checkForUpdate(context, Constants.VERSIONS_GROUPS, new Handler.Callback() {
//			@Override
//			public boolean handleMessage(Message msg) {
//				if (msg.what == 1){
//					categoryGroupsList = dbHelper.getCategoryGroupsList();
//					if (dialog != null){
//						dialog.dismiss();
//						categoryGroupAdapter = new SimpleItemAdapter(context, categoryGroupsList);	
//						initGroupsListView();				
//					}else
//						categoryGroupAdapter.notifyDataSetChanged();
//				}
//				return false;
//			}
//		});
//	}
//
//	/**
//	 * Sets adapter to categoryGroupListView;
//	 */
//	private void initGroupsListView() {
//		categoryGroupListView = (ListView) findViewById(R.id.lv_category_groups);
//		categoryGroupListView.setOnItemClickListener(onCategoryGroupClickListener);
//		categoryGroupListView.setAdapter(categoryGroupAdapter);		
//	}
//
//	private void getEventsList() {
//		final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
//		eventsList = dbHelper.getEventsList();
//		if (!eventsList.isEmpty()) //eventsList!= null && 
//			sortEvents(eventsList);		
//		Utils.checkForUpdate(context, Constants.VERSIONS_CALENDAR, new Handler.Callback() {
//			@Override
//			public boolean handleMessage(Message msg) {
//				if (msg.what == 1){
//					eventsList = dbHelper.getEventsList();
//					refreshEventsAdapter();
//				}
//				return false;
//			}
//		});
//	}
//
//	private void sortEvents(List<Event> events) {
//		Collections.sort(events, new Comparator<Event>() {
//			@Override
//			public int compare(Event event1, Event event2) {
//				return event1.compareTo(event2);
//			}
//		});
//	}
//
//	private OnItemClickListener onCategoryGroupClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//
//			startSelectCategoryActivity(position);
//		}		
//	};
//	
//	private void startSelectCategoryActivity(int index) {
//		CategoryGroup group = categoryGroupsList.get(index);
//
//		
//		Intent intent = new Intent(context, ListSelectorActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//		intent.putExtra(Constants.EXTRA_TYPE, Constants.EXTRA_CATEGORY_ID);
//		intent.putExtra(Constants.EXTRA_CATEGORY_GROUP, group.getId());
//		startActivityForResult(intent, Constants.REQUEST_CATEGORY_GROUP);
//		
//	}
//
//	public void onAddEventButtonClick(View v) {
//		startManageEventActivity(null, Constants.REQUEST_CREATE_EVENT);
//	}
//
//	private void startManageEventActivity(String eventId, int requestCode) {
//		Intent intent = new Intent(this, ManageEventActivity.class);
//		if (eventId != null)
//			intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
//		startActivityForResult(intent, requestCode);
//		
//	}
//
//	private void startThemeSelectorActivity(String categoryId, String eventId) {
//		System.out.println("startShopActivity "+categoryId);
//		Intent intent = new Intent(this, ThemeSelectorActivity.class);
//		intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
//		intent.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
//		startActivity(intent);		
//	}
//	
//	private void setActionBar() {
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setTitle(getResources().getString(R.string.title_events_activity));
//	}
//
//	private Event getExtraEvent(Intent data) {
//		Event event = new Event(data.getStringExtra(Constants.EXTRA_ID));
//		event.setName(data.getStringExtra(Constants.EXTRA_NAME));
//		event.setCategoryId(data.getStringExtra(Constants.EXTRA_CATEGORY_ID));
//		event.setDate(data.getLongExtra(Constants.EXTRA_DATE,0));
//		event.setRepeat(data.getIntExtra(Constants.EXTRA_PERIODICITY,-1));
//		event.setNotification(data.getIntExtra(Constants.EXTRA_PUSH, -1));
//		
//		event.setType(data.getStringExtra(Constants.EXTRA_TYPE));
//		
//		return event;
//		
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		System.out.println("onActivityResult "+requestCode+" "+resultCode);
//		switch (requestCode) {
//		case Constants.REQUEST_CREATE_EVENT:
//		{
//			if (resultCode == RESULT_OK) {
//				Event event = getExtraEvent(data);
//
//				eventsList.add(event);
//				
//				
//				refreshEventsAdapter();
//			}
//		}
//			break;
//		case Constants.REQUEST_CATEGORY_GROUP:
//		{
//			if (resultCode == RESULT_OK) {
//				String categoryId = data.getStringExtra(Constants.EXTRA_ID);
//				startThemeSelectorActivity(categoryId, null);
//			}
//		}
//			break;
//		case Constants.REQUEST_MANAGE_EVENT:
//			if (resultCode == RESULT_OK) {
//				
//				Event event = getExtraEvent(data);
//				int location = findEvent(event.getId());
//				if (location != -1){
//					eventsList.set(location, event);
//					refreshEventsAdapter();
//				}
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//	private void refreshEventsAdapter() {
//		sortEvents(eventsList);
//		for (EventsGroup group : eventGroupsList)
//			group.clearEventsList();
//		
//		sortEventsByGroups();
//		eventAdapter.notifyDataSetChanged();
//		
//	}
//	
//	private int findEvent(String id) {
//		for (int i = 0; i < eventsList.size(); i++){
////			Log.d(TAG, "findEvent "+i+" eventsList.get(i).getId() "+eventsList.get(i).getId());
//			if (eventsList.get(i).getId().equals(id))
//				return i;
//		}
//		return -1;
//	}
//
//}
