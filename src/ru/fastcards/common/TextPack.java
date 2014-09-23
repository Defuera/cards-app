package ru.fastcards.common;

/**
 * 
 * @author Denis V
 * @since 20.11.2013 add description field
 *Add interface Article implementation
 */
public class TextPack extends Article{

	private final String Uuid; 
	
	private String description;

//	private String categoryId;
//	
//	private String themeId;
	
	public TextPack(String id, String name) {
		this.Uuid = id;
		this.setName(name);
}
	public TextPack(String id, String name, String description,
			String categoryId,String themeId, String purchaseID, double price, boolean bought,
			String coverImage) {
		this.Uuid = id;
		this.setName(name);
		this.description = description;
//		this.setCategoryId(categoryId);
//		this.setThemeId(themeId);
		this.setPurchaseId(purchaseID);
		this.setPrice(price);
		this.setBought(bought);
		this.setCoverImage(coverImage);
		
	}
	public String getUuid() {
		return Uuid;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
//	public String getCategoryId() {
//		return categoryId;
//	}
//	public void setCategoryId(String categoryId) {
//		this.categoryId = categoryId;
//	}
//	
//	public String getThemeId(){
//		return themeId;
//	}
//	public void setThemeId(String themeId) {
//		this.themeId = themeId;
//	}

}
