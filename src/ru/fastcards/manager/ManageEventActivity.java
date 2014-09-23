package ru.fastcards.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import ru.fastcards.ListContacts;
import ru.fastcards.ListSelectorActivity;
import ru.fastcards.R;
import ru.fastcards.TrackedActivity;
import ru.fastcards.common.Category;
import ru.fastcards.common.CategoryGroup;
import ru.fastcards.common.Event;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.recipientselectors.RecipientSelectorActivity;
import ru.fastcards.send.AlarmService;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ManageEventActivity extends TrackedActivity {
	private static final String TAG = "CreateEventActivity";
	private EditText nameEt;
	private TextView eventRecTv;
	private TextView categoryTv;
	private TextView notificationTv;

	private DatePicker datePicker;

	// private int notification;
	private Context context;

	private int notificationId = -1;
	private List<ISendableItem> recList = new ArrayList<ISendableItem>();
	private Category category;

	private String eventId;
	private String eventType;

	private static final long dayInMillis = (1000 * 60 * 60 * 24);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_create_event);

		nameEt = (EditText) findViewById(R.id.et_name);
		nameEt.setOnFocusChangeListener(onEditExtFocusChangedListener);

		eventRecTv = (TextView) findViewById(R.id.tv_recipients);
		categoryTv = (TextView) findViewById(R.id.tv_category);
		notificationTv = (TextView) findViewById(R.id.tv_push);

		datePicker = (DatePicker) findViewById(R.id.dp_event_date);

		getExtras();
	}

	private void getExtras() {
		Intent intent = getIntent();
		eventId = intent.getStringExtra(Constants.EXTRA_EVENT_ID);
		if (eventId != null) {
			loadEvent(eventId);
		}

	}

	private void loadEvent(String eventId) {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		Event event = dbHelper.getEventById(eventId);
		eventType = event.getType();
		nameEt.setText(event.getName());

		recList = (List<ISendableItem>) (List<?>) dbHelper.getRecipientsListByEventId(eventId);

		setEventRecTv();

		Calendar calendar = new GregorianCalendar();// .getInstance();
		calendar.setTimeInMillis(event.getDate());

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		datePicker.updateDate(year, month, day);

		category = dbHelper.getCategoryById(event.getCategoryId());
		categoryTv.setText(category.getName());

		setNotificationTextView(event.getNotification());
	}

	private void setEventRecTv() {
		if (recList.size() == 1)
			eventRecTv.setText(recList.get(0).getName());
		else if (recList.size() > 1)
			eventRecTv.setText(getString(R.string.str_users) + " - " + getMultipleItemsSize());
		else
			eventRecTv.setText(getString(R.string.str_recipient));
	}

	private int getMultipleItemsSize() {
		int size = 0;
		for (ISendableItem item : recList) {
			if (item.isGroup())
				size += ((ListContacts) item).getSize(context);
			else
				size++;
		}
		return size;
	}

	private OnFocusChangeListener onEditExtFocusChangedListener = new OnFocusChangeListener() {

		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}
	};
	private String message;

	public void onCategoryTvClick(View v) {
		startGroupCategorySelectorActivity();
	}

	public void onRecipientsTvClick(View v) {
		Intent intent = new Intent(this, RecipientSelectorActivity.class);
		Utils.setIIsGroupDataExtra(recList, intent);
		startActivityForResult(intent, Constants.REQUEST_RECIPIENTS);
	}

	public void onPushTvClick(View v) {
		startSelectPushSelectorActivity();
	}

	private void startSelectPushSelectorActivity() {
		Intent intent = new Intent(context, ListSelectorActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra(Constants.EXTRA_TYPE, Constants.EXTRA_NOTIFICATION);
		startActivityForResult(intent, Constants.REQUEST_NOTIFICATION);
	}

	private void startGroupCategorySelectorActivity() {
		new AsyncTask<String, String, List<CategoryGroup>>() {

			@Override
			protected List<CategoryGroup> doInBackground(String... params) {
				List<CategoryGroup> categoryGroupsList = new ArrayList<CategoryGroup>();
				DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
				categoryGroupsList.addAll(dbHelper.getCategoryGroupsList());
				return categoryGroupsList;
			}

			@Override
			protected void onPostExecute(List<CategoryGroup> categoriesList) {
				Intent intent = new Intent(context, ListSelectorActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.putExtra(Constants.EXTRA_TYPE, Constants.EXTRA_CATEGORY_GROUP);
				startActivityForResult(intent, Constants.REQUEST_CATEGORY_GROUP);
			}
		}.execute();
	}

	private void saveEventContacts(String eventId) {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		dbHelper.clearEventContact(eventId);
		for (ISendableItem item : recList)
			dbHelper.saveEventContact(item.getId(), eventId, item.isGroup());
	}

	private String getCategory() {
		return category.getId();
	}

	private long getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);

		return calendar.getTimeInMillis();
	}

	private void returnEvent(String eventId) {
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_NAME, nameEt.getText().toString());
		intent.putExtra(Constants.EXTRA_DATE, getDate());
		intent.putExtra(Constants.EXTRA_CATEGORY_ID, getCategory());
		intent.putExtra(Constants.EXTRA_PERIODICITY, notificationId);
		intent.putExtra(Constants.EXTRA_ID, eventId);
		intent.putExtra(Constants.EXTRA_TYPE, eventType);
		setResult(RESULT_OK, intent);
		finish();
	}

	// private void startAlarm() {
	// AlarmManager alarmManager = (AlarmManager)
	// this.getSystemService(this.ALARM_SERVICE);
	// // Calendar calendar = Calendar.getInstance();
	// // calendar.set(int year, int month, int date, int hour, int minute, int
	// second);
	// long when = getNotificationTime();// calendar.getTimeInMillis(); //
	// notification time
	//
	// Intent intent = new Intent(this, ReminderService.class);
	//
	// message = nameEt.getText().toString();
	// intent.putExtra(Constants.EXTRA_TITLE, nameEt.getText().toString());
	// intent.putExtra(Constants.EXTRA_MESSAGE, message);
	// intent.putExtra(Constants.EXTRA_CATEGORY_ID, category.getId());
	// intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
	//
	// PendingIntent pendingIntent = PendingIntent.getService(this, new
	// Random(1000).nextInt(), intent, Intent.FLAG_ACTIVITY_NEW_TASK);
	//
	// Calendar calendar = Calendar.getInstance();
	// // calendar.set(2013, 12, 13, 18, 52, 00); )
	//
	// calendar.add(Calendar.SECOND, 10);
	// when = calendar.getTimeInMillis();;
	//
	// System.out.println(" startAlarm "+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+" name "+nameEt.getText());
	// alarmManager.set(AlarmManager.RTC, when, pendingIntent);
	// }

	private void createNotification() {
		message = nameEt.getText().toString();
		int notificationId = (int) System.currentTimeMillis() / 1000;
		AlarmService as = new AlarmService(context, nameEt.getText().toString(), message, category.getId(), eventId, notificationId, getNotificationTime());
		as.startAlarm();
	}

	private long getNotificationTime() {
		long date = getDate();
		Log.d(TAG, "given date is " + new Date(date));
		switch (notificationId) {
		case 1:
			date -= dayInMillis * 1;
			break;
		case 2:
			date -= dayInMillis * 2;
			break;
		case 3:
			date -= dayInMillis * 7;
			break;
		}
		// 11:00 in the morning
		date += 1000 * 60 * 60 * 11;
		date = Utils.clearDate(date);
		Log.v(TAG, "notification date set to  " + new Date(date));
		return date;
	}

	private void startSelectCategoryActivity(String categoryGroupId) {
		Intent intent = new Intent(context, ListSelectorActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra(Constants.EXTRA_TYPE, Constants.EXTRA_CATEGORY_ID);
		intent.putExtra(Constants.EXTRA_CATEGORY_GROUP, categoryGroupId);
		startActivityForResult(intent, Constants.REQUEST_CATEGORY);
	}

	private void setNotificationTextView(int notificationId) {
		if (notificationId == -1 ){
			notificationTv.setText(R.string.str_push);
		}else{
			String[] notificationsArray = context.getResources().getStringArray(R.array.array_notification_timers);
			notificationTv.setText(notificationsArray[notificationId]);
		}

	}

	private void setRecipientList(Intent data) {
		recList = Utils.getIIsGroupDataExtra(context, data);
		setEventRecTv();
	}

	private void getCategoryFromDbAndSet(String id) {
		System.out.println("setCategory id " + id);
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		category = dbHelper.getCategoryById(id);
		Log.d(TAG, "set category " + category.getName());
		categoryTv.setText(category.getName());
	}

	private void safeEvent() {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		if (eventId == null) {
			// Creating new event
			eventId = UUID.randomUUID().toString();

			if (eventType == null)
				eventType = Constants.EVENT_TYPE_CUSTOM;
			dbHelper.saveEvent(eventId, nameEt.getText().toString(), getDate(), getCategory(), notificationId, eventType);
		} else {
			// update existing Event
			dbHelper.updateEvent(eventId, nameEt.getText().toString(), getDate(), getCategory(), notificationId);
		}
		if (!recList.isEmpty())
			saveEventContacts(eventId);
		if (notificationId != -1)
			createNotification();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.shop, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.done: {			
			if (category == null || category.getId() == null)
				Toast.makeText(context, getString(R.string.str_error_choose_category), Toast.LENGTH_SHORT).show();
			else {
				safeEvent();
				returnEvent(eventId);
			}
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
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constants.REQUEST_CATEGORY_GROUP:
			if (resultCode == RESULT_OK) {
				String categoryGroupId = data.getStringExtra(Constants.EXTRA_ID);
				startSelectCategoryActivity(categoryGroupId);
			}
			break;
		case Constants.REQUEST_CATEGORY:
			if (resultCode == RESULT_OK) {
				String categoryId = data.getStringExtra(Constants.EXTRA_ID);
				getCategoryFromDbAndSet(categoryId);
			}
			break;
		case Constants.REQUEST_RECIPIENTS:
			if (resultCode == RESULT_OK) {
				setRecipientList(data);
			}
			break;
		case Constants.REQUEST_NOTIFICATION:
			if (resultCode == RESULT_OK) {
				notificationId = Integer.parseInt(data.getStringExtra(Constants.EXTRA_ID));
				setNotificationTextView(notificationId);
			}
			break;
		default:
			break;
		}

	}

}
