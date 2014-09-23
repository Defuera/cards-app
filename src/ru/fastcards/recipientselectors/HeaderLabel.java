package ru.fastcards.recipientselectors;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.common.Recipient;


public class HeaderLabel {
	private String label;
	private List<Recipient> recipientSection;
	
	public HeaderLabel(String currletter) {
		System.out.println("new Header Label "+currletter);
		this.label = currletter;
		recipientSection = new ArrayList<Recipient>();
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<Recipient> getRecipientSection() {
		return recipientSection;
	}
	public void setRecipientSection(List<Recipient> recipientSection) {
		this.recipientSection = recipientSection;
	}
	public void addRecipient(Recipient rec) {
		System.out.println("add recipient to header "+label+" size "+recipientSection.size());
		recipientSection.add(rec);		
	}

}
