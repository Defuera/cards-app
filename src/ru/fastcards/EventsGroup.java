package ru.fastcards;

import java.util.List;

import ru.fastcards.common.Event;

public class EventsGroup {
	private String name;
	private List<Event> eventsList;
	
	public EventsGroup(String name, List<Event> eventsList2) {
		this.name = name;
		this.eventsList = eventsList2;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Event> getEventsList() {
		return eventsList;
	}
	public void setEventsList(List<Event> eventsList) {
		this.eventsList = eventsList;
	}
	public void addEvent(Event event) {
		eventsList.add(event);
		
	}
	
	/**
	 * Deletes all events from group
	 */
	public void clearEventsList() {
		eventsList.clear();
		
	}
	public void removeEvent(int index) {
		eventsList.remove(index);
		
	}

}
