package ru.fastcards.common;

public class Project implements SimpleItem{
	private final String id;
	private final String name;
	private final String themeId;
	private String eventId;
	private String text;
	private String appealsId;
	private String signatureText;
	private String signatureBitmapUri;
	private boolean selected;
	
	public Project(String id, String name, String themeId){
		this.id = id;
		this.name = name;
		this.themeId = themeId;
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
	public String getThemeId() {
		return themeId;
	}
//	public void setThemeId(String themeId) {
//		this.themeId = themeId;
//	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getAppealsId() {
		return appealsId;
	}
	public void setAppealsId(String appealsId) {
		this.appealsId = appealsId;
	}
	public String getSignatureText() {
		return signatureText;
	}
	public void setSignatureText(String signatureText) {
		this.signatureText = signatureText;
	}
	public String getSignatureBitmapUri() {
		return signatureBitmapUri;
	}
	public void setSignatureBitmapUri(String signatureBitmapUri) {
		this.signatureBitmapUri = signatureBitmapUri;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
//	FIELD_UUID, id); //UUID.randomUUID().toString()
//	cv.put(FIELD_NAME, name);
//	cv.put(FIELD_THEME_ID, themeId);
//	cv.put(FIELD_EVENT_ID, eventId); //
//	cv.put(FIELD_TEXT, text);
//	cv.put(FIELD_APPEALS_ID, appealId);
//	cv.put(FIELD_SIGNATURE_TEXT, sigText);
//	cv.put(FIELD_SIGNATURE_BITMAP_ID
}
