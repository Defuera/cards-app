package ru.fastcards.send;

import java.util.Date;
import java.util.Random;

import ru.fastcards.R;
import ru.fastcards.ThemeSelectorActivity;
import ru.fastcards.utils.Constants;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReciver";

//    on
    
	@Override
    public void onReceive(Context context, Intent intent) {

//		Log.d(TAG, "startAlarm ");
    	
    	String title = intent.getStringExtra(Constants.EXTRA_TITLE);
    	String message= intent.getStringExtra(Constants.EXTRA_MESSAGE);
    	String categoryId= intent.getStringExtra(Constants.EXTRA_CATEGORY_ID);
    	String eventId= intent.getStringExtra(Constants.EXTRA_EVENT_ID);
    	int notificationId = intent.getIntExtra(Constants.EXTRA_NOTIFICATION, new Random(100).nextInt());
    	
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context).
				setSmallIcon(R.drawable.ic_notify)
				.setContentTitle(title) //nameEt.getText().toString()
				.setContentText(message)
				.setDefaults(Notification.DEFAULT_SOUND);

		Intent resultIntent = new Intent(context, ThemeSelectorActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId)
				.putExtra(Constants.EXTRA_EVENT_ID, eventId);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(ThemeSelectorActivity.class);
		stackBuilder.addNextIntent(resultIntent);


//		int notificationId = new Random(100).nextInt();// (int)(System.currentTimeMillis()/1000);
		
		PendingIntent pIntent = stackBuilder.getPendingIntent(notificationId, Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationBuilder.setContentIntent(pIntent);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		Notification notification = notificationBuilder.build();
    	Log.d(TAG, "onReceive on "+new Date()+ " notificationId "+notificationId+ " "+title+" "+notification+" hash "+notification.toString());
		
		mNotificationManager.notify(notificationId, notification);
    }
}
