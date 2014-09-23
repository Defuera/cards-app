package ru.fastcards.send;

import java.util.Calendar;
import java.util.Random;

import ru.fastcards.utils.Constants;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmService {
	private static final String TAG = "AlarmService";
	private Context context;
	private PendingIntent mAlarmSender;
	private long dateMillis;

	public AlarmService(Context context, String title, String message, String categoryId, String eventId, int notificationId, long dateMillis) {
		this.context = context;
		this.dateMillis = dateMillis;
		Intent intent = new Intent(context, AlarmReceiver.class);
	    
    	intent.putExtra(Constants.EXTRA_TITLE, title);
    	intent.putExtra(Constants.EXTRA_MESSAGE, message);
    	intent.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId);
    	intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
    	intent.putExtra(Constants.EXTRA_NOTIFICATION, notificationId);

		Log.d(TAG, "PendingIntent "+notificationId);
		mAlarmSender = PendingIntent.getBroadcast(context, notificationId, intent,  Intent.FLAG_ACTIVITY_NEW_TASK);
	}

	public void startAlarm() {
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, dateMillis, mAlarmSender);
	}
}