package ru.fastcards;

import java.util.List;

import ru.fastcards.common.Event;
import ru.fastcards.utils.Constants;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class EventsListAdapter extends BaseExpandableListAdapter {

	private List<EventsGroup> groupsList;
	private Context context;

	public EventsListAdapter(Context context, List<EventsGroup> groups) {
		this.context = context;
		groupsList = groups;
	}

	@Override
	public int getGroupCount() {
		return groupsList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupsList.get(groupPosition).getEventsList().size();
	}

	@Override
	public EventsGroup getGroup(int groupPosition) {
		return groupsList.get(groupPosition);
	}

	@Override
	public Event getChild(int groupPosition, int childPosition) {
		return groupsList.get(groupPosition).getEventsList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_events_list, null);
		}

		TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
		textGroup.setText(getGroup(groupPosition).getName());

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_events_list, null);
		}

		Event item = getChild(groupPosition, childPosition);
		int textColor = getCategoryColor(item.getType());

		TextView titleTV = (TextView) convertView.findViewById(R.id.tv_title);
		titleTV.setText(item.getName());
		titleTV.setTextColor(textColor);

		TextView infoTV = (TextView) convertView.findViewById(R.id.tv_info);
		infoTV.setText(item.getFomattedDate());
		infoTV.setTextColor(textColor);

		return convertView;
	}

	private int getCategoryColor(String type) {
		int color = context.getResources().getColor(android.R.color.white);
		if (!type.equals(Constants.EVENT_TYPE_BIRTHDAY)) {			
			if (type.equals(Constants.EVENT_TYPE_COMMON_HOLIDAYS))
				color = context.getResources().getColor(R.color.text_event_crab_red);
			if (type.equals(Constants.EVENT_TYPE_CUSTOM))
				color = context.getResources().getColor(R.color.text_event_orange);
		}
		return color;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

}