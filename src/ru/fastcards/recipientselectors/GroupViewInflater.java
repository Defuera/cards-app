package ru.fastcards.recipientselectors;

import ru.fastcards.R;
import ru.fastcards.ListContacts;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GroupViewInflater {

	private Context context;
//	private DataBaseHelper dbhelper;

	public GroupViewInflater(Context context) {
		this.context = context;
//		dbhelper = DataBaseHelper.getInstance(context);
	}
	

	public View getView(ListContacts item, View contentView) {		
		
		RelativeLayout container = (RelativeLayout) contentView.findViewById(R.id.rl_group_container);
//		container.setOnClickListener(l)
		
		TextView nameTv = (TextView) contentView.findViewById(R.id.tv_group_name);
		nameTv.setText(item.getName());
		TextView contactInfoTv = (TextView) contentView.findViewById(R.id.tv_info);
		contactInfoTv.setText(getInfoText(item.getSize(context)));
		
		return contentView;
	}

	private CharSequence getInfoText(int size) {
		return size+" "+context.getString(R.string.str_users);
	}
}