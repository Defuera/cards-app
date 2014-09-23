package ru.fastcards;

import ru.fastcards.common.ISendableItem;
import ru.fastcards.common.SimpleItem;
import ru.fastcards.utils.DataBaseHelper;

import android.content.Context;
import android.widget.Toast;

public class ListContacts implements SimpleItem, ISendableItem{
	private final String id;
	private String name;
	private boolean selected;
//	private int size;
	
	public ListContacts(String id, String name){
		this.id = id;
		this.name = name;
//		this.setSize(size);
	}
	
	@Override
	public String toString(){
		return "ListContacts"+name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void changeName(Context context, String name) {
//		Toast.makeText(context, "changeGroupName "+name, Toast.LENGTH_SHORT).show();
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		this.name = name;
		dbHelper.renameContactsGroup(this.id, name);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isGroup() {
		return true;
	}

	public int getSize(Context context) {
		return DataBaseHelper.getInstance(context).getListSize(this.id);
	}
//
//	public void setSize(int size) {
//		this.size = size;
//	}
}
