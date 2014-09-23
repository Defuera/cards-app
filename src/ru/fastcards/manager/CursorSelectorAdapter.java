package ru.fastcards.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;

public abstract class CursorSelectorAdapter extends CursorAdapter{
	
	public CursorSelectorAdapter(Context context) {
		super(context, null, 0);
	}

	protected Set<Long> choosedItems = new HashSet<Long>();
	
	public void selectItem(long id){
		choosedItems.add(id);
	}
	
	public boolean isItemSelected(long id){
		return choosedItems.contains(id);
	}
	
	public void unselectItem(long id){
		choosedItems.remove(id);
	}
	
	public void clearSelected(){
		choosedItems.clear();
	}
	
	@Override
	public long getItemId(int position) {
//		Log.i(TAG, "getItem(position) "+getItem(position).getClass());
		Cursor cursor = (Cursor) getItem(position);
		String uuid = cursor.getString(cursor.getColumnIndex("_id"));
		long longId = UUID.fromString(uuid).getMostSignificantBits();
		return longId;
	}
	
}
