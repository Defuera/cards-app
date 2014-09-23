package ru.fastcards.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Event;
import ru.fastcards.manager.ManageEventActivity;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MyEventsActivity extends ActionBarActivity {

	private Context context;
	private ListView my_events_list;
	private TextView empty_events_text;
	private List<Event> eventsList = new ArrayList<Event>();
	private MyEventsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_events);
		
		context=this;
		createMyEventsList();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()){
			case android.R.id.home:
				super.onBackPressed();
				break;
			
			case R.id.action_add:
				startManageEventActivity(null, Constants.REQUEST_CREATE_EVENT);
			break;
			}
		return true;
	}
	

	private void createMyEventsList(){
		my_events_list=(ListView) findViewById(R.id.my_events_list);
		empty_events_text=(TextView)findViewById(R.id.empty_events_text);
		loadEventsListFromDb();

		if (!eventsList.isEmpty()) empty_events_text.setVisibility(View.INVISIBLE);
		adapter = new MyEventsAdapter(context, eventsList);
        my_events_list.setAdapter(adapter);
        
        my_events_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				startManageEventActivity(eventsList.get(position).getId(), Constants.REQUEST_MANAGE_EVENT);
			}
		});
	}
	
	private void loadEventsListFromDb() {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		List<Event> allEvents = dbHelper.getEventsList();
		createCustomEventsList(allEvents);
		sortEvents(eventsList);
	}
	
	private void createCustomEventsList(List<Event> allEvents){
		for (Event e:allEvents){
			if (e.getType().equals(Constants.EVENT_TYPE_CUSTOM))
				eventsList.add(e);
		}
	}
	
	private void sortEvents(List<Event> events) {
		Collections.sort(events, new Comparator<Event>() {
			@Override
			public int compare(Event event1, Event event2) {
				return event1.compareTo(event2);
			}
		});
	}
	
	private void startManageEventActivity(String eventId, int requestCode) {
		Intent intent = new Intent(this, ManageEventActivity.class);
		if (eventId != null)
			intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
		startActivityForResult(intent, requestCode);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("onActivityResult "+requestCode+" "+resultCode);

		switch (requestCode) {
		case Constants.REQUEST_CREATE_EVENT:
			if (resultCode == RESULT_OK) {
//				String categoryGroupId = data.getStringExtra(Constants.EXTRA_ID);
				
				Event event = getExtraEvent(data);

				eventsList.add(event);
				
				if (!eventsList.isEmpty()) empty_events_text.setVisibility(View.INVISIBLE);
				
				adapter.notifyDataSetChanged();
				sortEvents(eventsList);
			}
			break;
		case Constants.REQUEST_MANAGE_EVENT:
			if (resultCode == RESULT_OK) {
				Event event = getExtraEvent(data);
				int location = findEvent(event.getId());
				if (location != -1){
					eventsList.set(location, event);
					adapter.notifyDataSetChanged();
				}
			}
			break;

		default:
			break;
		}
		
		}

	private int findEvent(String id) {
		for (int i = 0; i < eventsList.size(); i++){
			if (eventsList.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	private Event getExtraEvent(Intent data) {
		Event event = new Event(data.getStringExtra(Constants.EXTRA_ID));
		event.setName(data.getStringExtra(Constants.EXTRA_NAME));
		event.setCategoryId(data.getStringExtra(Constants.EXTRA_CATEGORY_ID));
		event.setDate(data.getLongExtra(Constants.EXTRA_DATE,0));
		event.setRepeat(data.getIntExtra(Constants.EXTRA_PERIODICITY,-1));
		event.setNotification(data.getIntExtra(Constants.EXTRA_PUSH, -1));
		
		event.setType(data.getStringExtra(Constants.EXTRA_TYPE));
		
		return event;
		
	}


}
