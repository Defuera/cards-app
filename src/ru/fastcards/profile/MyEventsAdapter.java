package ru.fastcards.profile;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Event;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyEventsAdapter extends ArrayAdapter<Event>{
	
//	private List<Event> listItems;
	private Context context;

	public MyEventsAdapter(Context context, List<Event> listItems) {
		super(context, R.layout.row_events_list,listItems);
		this.context=context;
//		this.listItems=listItems;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_events_list, null);
		}
		final Event item = getItem(position);
		
		TextView titleTv = (TextView) convertView.findViewById(R.id.tv_title);
		titleTv.setText(item.getName());

		TextView dateTv = (TextView) convertView.findViewById(R.id.tv_info);
		dateTv.setText(item.getFomattedDate());
		
		return convertView;
	}

}
