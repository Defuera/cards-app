package ru.fastcards.profile;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.SocialNetworkItem;
import ru.fastcards.utils.Account;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SocialNetworksAdapter extends ArrayAdapter<SocialNetworkItem> {

	private Context context;
	private Account account = Account.getInstance();

	public SocialNetworksAdapter(Context context, List<SocialNetworkItem> networksList) {
		super(context, R.layout.raw_social_network, networksList);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.raw_social_network, null);
		}
		
		SocialNetworkItem item = getItem(position);
		
		ImageView image = (ImageView) convertView.findViewById(R.id.iv_icon);
		image.setImageResource(item.getImageResource());
		
		TextView titleTv = (TextView) convertView.findViewById(R.id.tv_title);
		titleTv.setText(item.getTitle());
		
		TextView userTv = (TextView) convertView.findViewById(R.id.tv_user_name);
		userTv.setText(item.getUserName());
		
		return convertView;
	}

}
