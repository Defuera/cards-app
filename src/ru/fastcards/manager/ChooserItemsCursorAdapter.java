package ru.fastcards.manager;

import java.util.Locale;
import java.util.UUID;

import ru.fastcards.R;
import android.content.Context;
import android.database.Cursor;
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

public class ChooserItemsCursorAdapter extends CursorSelectorAdapter implements SectionIndexer {
	private static final String TAG = "ChooserItemsCursorAdapter";
	private LayoutInflater mInflater;
	private AlphabetIndexer mAlphabetIndexer; 
	private String mSearchTerm;

	public ChooserItemsCursorAdapter(Context context) {
		super(context);

		mInflater = LayoutInflater.from(context);
		final String alphabet = context.getString(R.string.alphabet);
		mAlphabetIndexer = new AlphabetIndexer(null, 0, alphabet);

//		highlightTextSpan = new TextAppearanceSpan(context, R.style.searchTextHiglight);
	}

	private int indexOfSearchQuery(String displayName) {
		if (!TextUtils.isEmpty(mSearchTerm)) {
			return displayName.toLowerCase(Locale.getDefault()).indexOf(mSearchTerm.toLowerCase(Locale.getDefault()));
		}
		return -1;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		final View itemLayout = mInflater.inflate(R.layout.raw_simple_chooser_multiple, viewGroup, false);

		final ViewHolder holder = new ViewHolder();
		holder.nameTv = (TextView) itemLayout.findViewById(R.id.list_item_title);
		holder.checkerIv = (ImageView) itemLayout.findViewById(R.id.iv_check);

		itemLayout.setTag(holder);

		return itemLayout;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		String itemId = cursor.getString(cursor.getColumnIndex("_id"));
		final String displayName = cursor.getString(cursor.getColumnIndex("Name"));
//		String info = cursor.getString(cursor.getColumnIndex("URI_WHAT")); 
		holder.nameTv.setText(displayName);

		holder.checkerIv.setVisibility(isItemSelected(getItemId(cursor.getPosition())) ? View.VISIBLE : View.INVISIBLE);
//		Log.d(TAG, "checkerIv.setVisibility "+ (isItemSelected(getItemId(cursor.getPosition())) ? View.VISIBLE : View.INVISIBLE));
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
		public ImageView checkerIv;
		TextView nameTv;
	}
}