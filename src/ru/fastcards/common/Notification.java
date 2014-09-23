package ru.fastcards.common;


public class Notification implements SimpleItem {

	private final String id;
	private final String name;
	private boolean selected;

	public Notification(String id, String name){
		this.id = id;
		this.name = name;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return id;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
