package ru.fastcards;

import java.util.List;

import ru.fastcards.common.SimpleItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SimpleItemAdapter extends ArrayAdapter<SimpleItem> {
//	private List<?> listItems;
	private Context context;

	public SimpleItemAdapter(Context context, List categoryGroupList) {
		super(context, R.layout.row_simple_item, categoryGroupList);
		this.context=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_simple_item, null);
		}
		SimpleItem item = getItem(position);

		TextView categoryGroupTv = (TextView) convertView.findViewById(R.id.tv_item_name);
		categoryGroupTv.setText(item.getName());
		return convertView;
	}
}