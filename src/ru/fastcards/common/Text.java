package ru.fastcards.common;

public class Text {

	private final String uuid;
	private final String name;
	private final String textString;

	public Text(String textId, String textName, String textString) {
		this.uuid = textId;
		this.name = textName;
		this.textString = textString;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getTextString() {
		return textString;
	}

}
