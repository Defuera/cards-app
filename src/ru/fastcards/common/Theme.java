package ru.fastcards.common;

public class Theme extends Article {


	private String categoryId;
	private String author;
	private String eCardImage;
	private String squareImage;
	private String postCardFrontImage;
	private String postCardBackImage;
	private String eCardThumb;
	private String squareThumb;
	private String postCardFrontThumb;
	private String postCardBackThumb;
	private int eTextTop;
	private int eTextLeft;
	private int pTextTop;
	private int pTextLeft;
	private int textColorRed;
	private int textColorGreen;
	private int textColorBlue;

	public Theme(String id, String name) {
		this.setId(id);
		this.setName(name);
	}

	public Theme(String id, String name, String categoryId, String purchaseId) {
		this.setId(id);
		this.setName(name);
		this.setCategoryId(categoryId);
		this.setPurchaseId(purchaseId);
	}

	public Theme(String id, String name, String categoryId, String purchaseID,
			String author, double price, boolean bought, String coverImage,
			String eCardImage, String eCardThumb, String squareImage,
			String squareThumb, String postCardFrontImage,
			String postCardFrontThumb, String postCardBackImage,
			String postCardBackThumb, int eTextTop, int eTextLeft,
			int pTextTop, int pTextLeft, int textColorRed,
			int textColorGreen, int textColorBlue) {
		this.setId(id);
		this.setName(name);
		this.setPurchaseId(purchaseID);
		this.setCoverImage(coverImage);
		this.setPrice(price);
		this.setBought(bought);

		this.categoryId=categoryId;
		this.author = author;
		this.eCardImage = eCardImage;
		this.eCardThumb = eCardThumb;
		this.squareImage = squareImage;
		this.squareThumb = squareThumb;
		this.postCardFrontImage = postCardFrontImage;
		this.postCardFrontThumb = postCardFrontThumb;
		this.postCardBackImage = postCardBackImage;
		this.postCardBackThumb = postCardBackThumb;
		this.eTextTop = eTextTop;
		this.eTextLeft = eTextLeft;
		this.pTextTop = pTextTop;
		this.pTextLeft = pTextLeft;
		this.textColorRed = textColorRed;
		this.textColorGreen = textColorGreen;
		this.textColorBlue = textColorBlue;
	}


	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPostCardBackImage() {
		return postCardBackImage;
	}

	public void setPostCardBackImage(String postCardBackImage) {
		this.postCardBackImage = postCardBackImage;
	}

	public String getPostCardFrontImage() {
		return postCardFrontImage;
	}

	public void setPostCardFrontImage(String postCardFrontImage) {
		this.postCardFrontImage = postCardFrontImage;
	}

	public String getSquareImage() {
		return squareImage;
	}

	public void setSquareImage(String squareImage) {
		this.squareImage = squareImage;
	}

	public String getECardImage() {
		return eCardImage;
	}

	public void setECardImage(String eCardImage) {
		this.eCardImage = eCardImage;
	}

	public int getETextTop() {
		return eTextTop;
	}

	public void setETextTop(int eTextTop) {
		this.eTextTop = eTextTop;
	}

	public int getETextLeft() {
		return eTextLeft;
	}

	public void setETextLeft(int eTextLeft) {
		this.eTextLeft = eTextLeft;
	}

	public int getpTextTop() {
		return pTextTop;
	}

	public void setPTextTop(int pTextTop) {
		this.pTextTop = pTextTop;
	}

	public int getpTextLeft() {
		return pTextLeft;
	}

	public void setPTextLeft(int pTextLeft) {
		this.pTextLeft = pTextLeft;
	}

	public int getTextColorRed() {
		return textColorRed;
	}

	public void setTextColorRed(int textColorRed) {
		this.textColorRed = textColorRed;
	}

	public int getTextColorGreen() {
		return textColorGreen;
	}

	public void setTextColorGreen(int textColorGreen) {
		this.textColorGreen = textColorGreen;
	}

	public int getTextColorBlue() {
		return textColorBlue;
	}

	public void setTextColorBlue(int textColorBlue) {
		this.textColorBlue = textColorBlue;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;

	}

	public String getECardThumb() {
		return eCardThumb;
	}

	public void setECardThumb(String eCardThumb) {
		this.eCardThumb = eCardThumb;
	}

	public String getSquareThumb() {
		return squareThumb;
	}

	public void setSquareThumb(String squareThumb) {
		this.squareThumb = squareThumb;
	}

	public String getPostCardFrontThumb() {
		return postCardFrontThumb;
	}

	public void setPostCardFrontThumb(String postCardFrontThumb) {
		this.postCardFrontThumb = postCardFrontThumb;
	}

	public String getPostCardBackThumb() {
		return postCardBackThumb;
	}

	public void setPostCardBackThumb(String postCardBackThumb) {
		this.postCardBackThumb = postCardBackThumb;
	}

}
