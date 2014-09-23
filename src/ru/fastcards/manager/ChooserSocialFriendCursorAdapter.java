package ru.fastcards.manager;


	import java.util.Locale;

import ru.fastcards.R;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.DataBaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

	public class ChooserSocialFriendCursorAdapter extends CursorSelectorAdapter implements
			SectionIndexer {
		private static final String TAG = "ChooserItemsCursorAdapter";
		private LayoutInflater mInflater; // Stores the layout inflater
		private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer
													// instance
		private TextAppearanceSpan highlightTextSpan; // Stores the highlight text
														// appearance style
		private String mSearchTerm;

		public ChooserSocialFriendCursorAdapter(Context context) {
			super(context);

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
			final View itemLayout = mInflater.inflate(R.layout.row_social_recipient, viewGroup, false);

			final ViewHolder holder = new ViewHolder();
			holder.nameTv = (TextView) itemLayout.findViewById(R.id.tv_name);
			holder.checkerIv = (ImageView) itemLayout.findViewById(R.id.iv_check);
			holder.recIconIv = (ImageView) itemLayout.findViewById(R.id.iv_icon_recipient);
					
			itemLayout.setTag(holder);

			return itemLayout;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final ViewHolder holder = (ViewHolder) view.getTag();
			
			String itemId = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FIELD_UUID));	
			
			final String displayName = cursor.getString(cursor.getColumnIndex(DataBaseHelper.FIELD_NAME));

			holder.nameTv.setText(displayName);
					
			holder.checkerIv.setVisibility(isItemSelected(getItemId(cursor.getPosition())) ?
					View.VISIBLE:
					View.INVISIBLE);
			
			String uri =  cursor.getString(cursor.getColumnIndex(DataBaseHelper.FIELD_THUMB));
			ProgressBar pb = (ProgressBar) view.findViewById(R.id.progress_bar);
			BitmapLoaderAsyncTask bl = new BitmapLoaderAsyncTask(context, null, true, true);
			bl.loadImageAsync(uri, holder.recIconIv, pb);
//			holder.iconIv
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
			public ImageView recIconIv;
			public ImageView checkerIv;
			TextView nameTv;
		}
		

	}