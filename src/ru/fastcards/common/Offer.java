package ru.fastcards.common;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.utils.Utils;


public class Offer extends Article {
 
	private final String description;
	private final long endDate;
	private List<Article> articlesList;
	
	public Offer(String id, String name, String purchaseId, String description,String cover, float price, boolean bought, String endDate) {
		this.setId(id);
		this.setName(name);
		this.setPurchaseId(purchaseId);
		this.setCoverImage(cover);
		this.setPrice(price);
		this.setBought(bought);
		this.description = description;
		this.endDate = Utils.formatDate(endDate, "yyyy-MM-dd");
		setArticlesList(new ArrayList<Article>());
	}

	public String getDescription() {
		return description;
	}

	public long getEndDate() {
		return endDate;
	}

	public List<Article> getArticlesList() {
		return articlesList;
	}

	public void setArticlesList(List<Article> articlesList) {
		this.articlesList = articlesList;
	}

	public void addArticle(Article article) {
		articlesList.add(article);	
	}
}
