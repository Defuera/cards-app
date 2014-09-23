package ru.fastcards.manager;


import java.util.Locale;

import ru.fastcards.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;
//import com.example.thepostcard.manager.ContactsListFragment.ContactsQuery;


public class ContactsListCursorAdapter extends CursorAdapter implements SectionIndexer {
    private static final String TAG = "ContactsListAdapter";
	private LayoutInflater mInflater; // Stores the layout inflater
    private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance
    private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style
	private String mSearchTerm;

//    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread

    public ContactsListCursorAdapter(Context context) {
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
        final View itemLayout =  mInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);

        final ViewHolder holder = new ViewHolder();
        holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
        holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);
//        holder.icon = (QuickContactBadge) itemLayout.findViewById(android.R.id.icon);
        itemLayout.setTag(holder);

        return itemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
       
//        final String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);
//        Log.d(TAG, "cursor.getColumnNames() "+Arrays.toString(cursor.getColumnNames()));
        final String displayName = cursor.getString(cursor.getColumnIndex("Name"));

        final int startIndex = indexOfSearchQuery(displayName);

        if (startIndex == -1) {
            holder.text1.setText(displayName);

            if (TextUtils.isEmpty(mSearchTerm)) {
                holder.text2.setVisibility(View.GONE);
            } else {
                holder.text2.setVisibility(View.VISIBLE);
            }
        } else {
            final SpannableString highlightedName = new SpannableString(displayName);

            highlightedName.setSpan(highlightTextSpan, startIndex,
                    startIndex + mSearchTerm.length(), 0);
            holder.text1.setText(highlightedName);

            holder.text2.setVisibility(View.GONE);
        }

    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
//    	mAlphabetIndexer.setCursor(newCursor);
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
        TextView text2;
//        QuickContactBadge icon;
    }

	public String getItemUuid(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		return cursor.getString(0);
	}
}