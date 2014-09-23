package ru.fastcards.common;


public class Banner extends Article{

	private final String id;
	private final String purchaseId;
	private final String purchaseTypeId;
	private final String cover;

	public Banner(String id, String purchaseId, String purchaseType, String cover) {
		this.id = id;
		this.purchaseId = purchaseId;
		this.purchaseTypeId = purchaseType;
		this.cover = cover;
	}

	public String getId() {
		return id;
	}

	public String getPurchaseId() {
		return purchaseId;
	}

	public String getPurchaseType() {
		return purchaseTypeId;
	}

	public String getCover() {
		return cover;
	}
}
