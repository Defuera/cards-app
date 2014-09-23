package ru.fastcards.common;

public class Category implements SimpleItem{
	private final String id;
	private final String name;
	private final String groupId;
	private boolean selected;
	
	public Category(String id, String name, String groupId) {
		this.id = id;
		this.name = name;
		this.groupId = groupId;
	}
	public String getId() {
		return id;
	}
//	public void setId(String id) {
//		this.id = id;
//	}
	public String getName() {
		return name;
	}
//	public void setName(String name) {
//		this.name = name;
//	}
	public String getGroupId() {
		return groupId;
	}
//	public void setGroupId(String groupId) {
//		this.groupId = groupId;
//	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}


}
