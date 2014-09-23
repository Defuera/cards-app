package ru.fastcards.common;

public class CategoryGroup implements SimpleItem{
		private final String id;
		private final String name;
		private final String coverImage;
		private boolean selected;
		
		public CategoryGroup(String id, String name, String coverImage){
			this.id = id;
			this.name = name;
			this.coverImage = coverImage;
		}
		public String getId() {
			return id;
		}
//		public void setId(String id) {
//			this.id = id;
//		}
		public String getName() {
			return name;
		}
//		public void setName(String name) {
//			this.name = name;
//		}
		public boolean isSelected() {
			return selected;
		}
		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
		public String getCoverImage(){
			return coverImage;
		}
		
		@Override
		public String toString() {
		// TODO Auto-generated method stub
		if (name.equals("Все"))  return "Каталог";
		if (name.equals("All")) return "Catalog";		
		return name;
		}



}
