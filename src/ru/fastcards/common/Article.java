package ru.fastcards.common;

public class Article {
	private String purchaseId; 
	private String purchaseType;
	private String name;
	private String cover;
	private double price;
	private boolean bought;
	private String id;
	
	public Article(String id,String name, String purchaseId, String purchaseType,
			String cover,  float price, boolean bought) {
		this.id = id;
		this.name = name;
		this.purchaseId = purchaseId;
		this.purchaseType = purchaseType;
		this.cover = cover;
		this.price = price;
		this.setBought(bought);
	}
	
	public Article() {
	}
	public String getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}
	public String getPurchaseType() {
		return purchaseType;
	}
	public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCoverImage() {
		return cover;
	}
	public void setCoverImage(String cover) {
		this.cover = cover;
	}
	public boolean isBought() {
		return bought;
	}
	public void setBought(boolean bought) {
		this.bought = bought;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId (String id) {
		this.id=id;
	}
}
