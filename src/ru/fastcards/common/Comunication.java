package ru.fastcards.common;

public class Comunication {
	private final String uuid;
	private final String contactId;
	private String type;
	private boolean isPrimaty;
	
	/**
	 * is a contact information of a different type, could be a phone number, an email, or social network id;
	 */
	private String info;
	
	public Comunication(String uuid,  String contactId){
		this.uuid = uuid;
		this.contactId = contactId;
	}
	
	public Comunication(String uuid,  String contactId, String type, String info){
		this.uuid = uuid;
		this.contactId = contactId;
		this.type = type;
		this.info = info;
	}

	public String getUuid() {
		return uuid;
	}

	public String getContactId() {
		return contactId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isPrimaty() {
		return isPrimaty;
	}

	public void setPrimaty(boolean isPrimaty) {
		this.isPrimaty = isPrimaty;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String toString(){
		return " "+getType();
	}
	

}
