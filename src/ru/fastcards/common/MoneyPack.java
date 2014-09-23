package ru.fastcards.common;

/**
 * 
 * @author Denis V. On 22.11.2013
 *
 */
public class MoneyPack {
	private final String id; 
	private final String name;
	private final String playId;
	private String price;
	/**
	 * Number of stars in this pack
	 */
	private final int count;
	private final String imageName;
	
	public MoneyPack(String id, String playId, String name,int count,String imageName){
		this.id = id;
		this.name = name;
		this.count = count;
		this.imageName = imageName;
		this.playId = playId;
	}

	public String toString(){		
		return "MoneyPack "+id+" "+name+" "+playId;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public String getImage() {
		return imageName;
	}

	public String getPlaySku() {
		return playId;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
