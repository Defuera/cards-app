package ru.fastcards.utils;

import java.util.concurrent.TimeUnit;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;


public class TableCursorLoader extends CursorLoader {

    
    private DataBaseHelper db;
	private String tableName;
	private String where;
	private String order;

	public TableCursorLoader(Context context, String tableName, String where, String order) {
      super(context);
      this.tableName = tableName;
      this.where = where;
      this.order = order;
      db = DataBaseHelper.getInstance(context);
    }
    
    @Override
    public Cursor loadInBackground() {
    	Cursor cursor = db.getTableCursor(tableName, where, order);
      return cursor;
    }
    
  }
