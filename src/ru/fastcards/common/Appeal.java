package ru.fastcards.common;

/**
 * 
 * @author Denis V on 20.11.2013
 *
 */
public class Appeal {
	
	private final String uuid;
	private final String maleAppeal;
	private final String femaleAppeal;
	
	public Appeal(String id, String maleAppeal, String femaleAppeal){
		this.uuid = id;
		this.femaleAppeal = femaleAppeal;
		this.maleAppeal = maleAppeal;
		
	}

	public String getUuid() {
		return uuid;
	}

	public String getMaleAppeal() {
		return maleAppeal;
	}

	public String getFemaleAppeal() {
		return femaleAppeal;
	}

	public String get(int gender) {
		
		return gender == 1 ? maleAppeal : femaleAppeal;
	}




}
