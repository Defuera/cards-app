package ru.fastcards.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.fastcards.utils.Utils;


import android.util.Log;

public class Event  implements Comparable<Event>{
	private String uuid;
	private String name;
	private long date;
	private String categoryId;
	private int notificationDate;
	private int repeat;
	private String type;
	private String TAG = "Event";
	
	public Event(String name, String birthday){
		this.setName(name);
		
	}

	@Override
	public String toString() {
		return name+" "+new Date(date)+"\n";
	}

	public Event(String id) {
		this.uuid = id;
	}
	
	public String getId() {
		return uuid;
	}

	public void setId(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String category) {
		this.categoryId = category;
	}

	public int getNotification() {
		return notificationDate;
	}

	public void setNotification(int notificationDate) {
		this.notificationDate = notificationDate;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int compareTo(Event another) {
		Date date1 = Utils.clearDate(new Date(this.date));
		Date date2 = Utils.clearDate(new Date(another.date)); 
		return date1.compareTo(date2);
	}
	


	public String getFomattedDate() {
		Date date = new Date(this.date);
		SimpleDateFormat simpleD = new SimpleDateFormat("dd MMM");		
		return simpleD.format(date);
	}


}
