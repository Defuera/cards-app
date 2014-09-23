package ru.fastcards.send;

import java.util.Date;

import ru.fastcards.R;
import ru.fastcards.ThemeSelectorActivity;
import ru.fastcards.utils.Constants;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class ReminderService extends IntentService {
    private static final int NOTIF_ID = 1;
	private static final String TAG = "ReminderService";

    public ReminderService(){
        super("FastCardsReminder");
    }
    
    @Override
      protected void onHandleIntent(Intent intent) {
    	String title = intent.getStringExtra(Constants.EXTRA_TITLE);
    	String message= intent.getStringExtra(Constants.EXTRA_MESSAGE);
    	String categoryId= intent.getStringExtra(Constants.EXTRA_CATEGORY_ID);
    	String eventId= intent.getStringExtra(Constants.EXTRA_EVENT_ID);
    	
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext()).
				setSmallIcon(R.drawable.btn_email_small)
				.setContentTitle(title) //nameEt.getText().toString()
				.setContentText(message)
				.setDefaults(Notification.DEFAULT_SOUND);

		Intent resultIntent = new Intent(this, ThemeSelectorActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				.putExtra(Constants.EXTRA_CATEGORY_ID, categoryId)
				.putExtra(Constants.EXTRA_EVENT_ID, eventId);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(ThemeSelectorActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		int notificationId = (int)(System.currentTimeMillis()/1000);
		
		PendingIntent pIntent = stackBuilder.getPendingIntent(notificationId, Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationBuilder.setContentIntent(pIntent);

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
    	Log.d(TAG, "onHandleIntent on "+new Date()+ " notificationId "+notificationId+ " "+title);
		
		mNotificationManager.notify(notificationId, notificationBuilder.build());
    }

}