package ru.fastcards.common;

public class SocialNetworkItem{

	private final String userName;
	private final String title;
	private final int imageResource;

	public SocialNetworkItem(String title, String userName, int imageResource){
		this.title = title;
		this.userName = userName;
		this.imageResource = imageResource;
	}
	
	public String getTitle() {
		return title;
	}

	public int getImageResource() {
		return imageResource;
	}

	public String getUserName() {
		return userName;
	}



}
