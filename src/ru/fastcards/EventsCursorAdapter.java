package ru.fastcards;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class EventsCursorAdapter extends CursorAdapter implements SectionIndexer {
	private static final String TAG = "ContactsListAdapter";
	private LayoutInflater mInflater; // Stores the layout inflater
	private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance
	private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style
	private String mSearchTerm;
	private Context context;

	BitmapLoaderAsyncTask imageLoader;
	// private Cursor cursor;
	private String selectedItemId;

	public EventsCursorAdapter(Context context) {
		super(context, null, 0);
		
		this.context = context;

		mInflater = LayoutInflater.from(context);
		final String alphabet = context.getString(R.string.alphabet);
		mAlphabetIndexer = new AlphabetIndexer(null, 0, alphabet);
		highlightTextSpan = new TextAppearanceSpan(context, R.style.searchTextHiglight);
		imageLoader = new BitmapLoaderAsyncTask(context, null, true, true);
	}

	private int indexOfSearchQuery(String displayName) {
		if (!TextUtils.isEmpty(mSearchTerm)) {
			return displayName.toLowerCase(Locale.getDefault()).indexOf(mSearchTerm.toLowerCase(Locale.getDefault()));
		}
		return -1;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		// Inflates the list item layout.
		final View itemLayout = mInflater.inflate(R.layout.row_event, viewGroup, false);

		final ViewHolder holder = new ViewHolder();
		holder.image = (ImageView) itemLayout.findViewById(R.id.iv_event_icon);
		holder.eventName = (TextView) itemLayout.findViewById(R.id.tv_event_name);
		holder.month = (TextView) itemLayout.findViewById(R.id.tv_month);
		holder.day = (TextView) itemLayout.findViewById(R.id.tv_day);
		itemLayout.setTag(holder);

		return itemLayout;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();

		final String eventId = cursor.getString(cursor.getColumnIndex("_id"));
		final String displayName = cursor.getString(cursor.getColumnIndex("Name"));
		final String type = cursor.getString(cursor.getColumnIndex("Type"));
		final long date = cursor.getLong(cursor.getColumnIndex("Date"));		

		System.out.println("displayName " + displayName + " pos " + cursor.getPosition()+" date2 "+cursor.getInt(cursor.getColumnIndex("Date2")));
		final int startIndex = indexOfSearchQuery(displayName);

		if (eventId.equals(selectedItemId))
			view.setBackgroundColor(context.getResources().getColor(R.color.text_orange));
		
		holder.eventName.setText(displayName);
		
		setImage(holder.image, type, eventId);

		holder.day.setText(getDay(date));
		holder.month.setText(getMonth(date));
	}

	private String getMonth(long date) {
		SimpleDateFormat simpleD = new SimpleDateFormat("MMM");
		return simpleD.format(new Date(date));
	}
	
	public static long formatDate(String dateString, String pattern) {
		Date date = null;
		SimpleDateFormat simpleD;
		try {
			simpleD = new SimpleDateFormat(pattern);
			date = simpleD.parse(dateString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		if (date == null)
			return 0;
		Log.v(TAG, "formatDate "+dateString+" to "+date);
		return date.getTime();
	}

	private String getDay(long date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(date);
		return Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
	}

	private void setImage(ImageView imageView, String type, String eventId) {
		if (Constants.EVENT_TYPE_COMMON_HOLIDAYS.equals(type))
			imageView.setImageResource(R.drawable.icon_hdays_small);
		if (Constants.EVENT_TYPE_BIRTHDAY.equals(type)){
			String imageUrl = DataBaseHelper.getInstance(context).getRecipientPhoto(eventId);
			if (imageUrl != null){
				imageLoader.loadImageAsync(imageUrl, imageView, null);
			}else
				imageView.setImageResource(R.drawable.icon_bdays_small);
		}
		if (Constants.EVENT_TYPE_CUSTOM.equals(type))
			imageView.setImageResource(R.drawable.icon_my_small);
//		return 0;
		
	}

	@Override
	public Cursor swapCursor(Cursor newCursor) {
		return super.swapCursor(newCursor);

	}

	@Override
	public int getCount() {
		if (getCursor() == null) {
			return 0;
		}
		return super.getCount();
	}

	@Override
	public Object[] getSections() {
		return mAlphabetIndexer.getSections();
	}

	@Override
	public int getPositionForSection(int i) {
		if (getCursor() == null) {
			return 0;
		}
		return mAlphabetIndexer.getPositionForSection(i);
	}

	@Override
	public int getSectionForPosition(int i) {
		if (getCursor() == null) {
			return 0;
		}
		return mAlphabetIndexer.getSectionForPosition(i);
	}

	private class ViewHolder {
		public TextView day;
		public TextView month;
		public TextView eventName;
		public ImageView image;
	}

	public String getItemUuid(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		Log.v(TAG, "0 " + cursor.getString(0) + " 1 " + cursor.getString(1));
		return cursor.getString(0);
	}

	public void setSelectedItem(String position) {
		selectedItemId = position;
		
	}
}