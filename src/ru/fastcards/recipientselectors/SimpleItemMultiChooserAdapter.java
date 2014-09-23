package ru.fastcards.recipientselectors;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.SimpleItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleItemMultiChooserAdapter extends ArrayAdapter<SimpleItem> {
//	private List<?> listItems;
	private Context context;

	public SimpleItemMultiChooserAdapter(Context context, List categoryGroupList) {
		super(context, R.layout.row_simple_item, categoryGroupList);
		this.context=context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		
		//= new ViewHolder();
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.raw_simple_chooser_multiple, null);
			
			holder = new ViewHolder();
			holder.nameTv = (TextView) convertView.findViewById(R.id.list_item_title);
			holder.checkerIv = (ImageView) convertView.findViewById(R.id.iv_check);
			convertView.setTag(holder);
		}else{
	           holder = (ViewHolder) convertView.getTag();			
		}
		SimpleItem item = getItem(position);
		
		holder.nameTv.setText(item.getName());
		holder.checkerIv.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);
		
		return convertView;
	}
	
	private class ViewHolder {
		public ImageView checkerIv;
		TextView nameTv;
	}
}