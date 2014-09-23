package ru.fastcards.manager;

import java.util.Locale;

import ru.fastcards.R;
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
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SimpleItemCursorAdapter  extends CursorAdapter implements SectionIndexer {
	 private static final String TAG = "ContactsListAdapter";
		private LayoutInflater mInflater; // Stores the layout inflater
	    private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance
	    private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style
		private String mSearchTerm;
//		private Cursor cursor;

	    public SimpleItemCursorAdapter(Context context) {
	        super(context, null, 0);

	        mInflater = LayoutInflater.from(context);
	        final String alphabet = context.getString(R.string.alphabet);
	        mAlphabetIndexer = new AlphabetIndexer(null, 0, alphabet);
	        highlightTextSpan = new TextAppearanceSpan(context, R.style.searchTextHiglight);

	    }

	    private int indexOfSearchQuery(String displayName) {
	        if (!TextUtils.isEmpty(mSearchTerm)) {
	            return displayName.toLowerCase(Locale.getDefault()).indexOf(
	                    mSearchTerm.toLowerCase(Locale.getDefault()));
	        }
	        return -1;
	    }

	    @Override
	    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
	        // Inflates the list item layout.
	        final View itemLayout =  mInflater.inflate(R.layout.row_simple_item, viewGroup, false);

	        final ViewHolder holder = new ViewHolder();
	        holder.text1 = (TextView) itemLayout.findViewById(R.id.tv_item_name);
	        itemLayout.setTag(holder);

	        return itemLayout;
	    }

	    @Override
	    public void bindView(View view, Context context, Cursor cursor) {
	        final ViewHolder holder = (ViewHolder) view.getTag();
	       
	        final String displayName = cursor.getString(cursor.getColumnIndex("Name"));

	        System.out.println("displayName "+displayName+" pos "+cursor.getPosition());
	        final int startIndex = indexOfSearchQuery(displayName);

	            holder.text1.setText(displayName);


	    }

	    @Override
	    public Cursor swapCursor(Cursor newCursor) {
//	    	cursor = newCursor;
	    	Log.d(TAG, "swapCursor " + newCursor.getCount());
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
	        TextView text1;
	    }

		public String getItemUuid(int position) {
			Cursor cursor = getCursor();
			cursor.moveToPosition(position);
			Log.v(TAG, "0 "+cursor.getString(0)+" 1 "+cursor.getString(1));
			return cursor.getString(0);
		}
	}