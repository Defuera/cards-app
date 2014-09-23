package ru.fastcards.common;


public interface ISendableItem extends SimpleItem {
	public boolean isGroup();
	public boolean isSelected();

	public String getId();

	public String getName();
}
