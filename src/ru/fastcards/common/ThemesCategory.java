package ru.fastcards.common;

import java.util.List;

public class ThemesCategory {
	private final Category category;
	private final List<Theme> themesList;
	
	public ThemesCategory(Category category, List<Theme> themesList){
		this.category = category;
		this.themesList = themesList;
	}

	public Category getCategory() {
		return category;
	}

	public List<Theme> getThemesList() {
		return themesList;
	}

}
