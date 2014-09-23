package ru.fastcards.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

public class MyCursorLoader extends CursorLoader {

	private DataBaseHelper db;
	private String query;

	public MyCursorLoader(Context context, String query) {
		super(context);
		this.query = query;
		db = DataBaseHelper.getInstance(context);
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = db.executeQuery(query);// db.getTableCursor(tableName, where, order);
		return cursor;
	}

}
