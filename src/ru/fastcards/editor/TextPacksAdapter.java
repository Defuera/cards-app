package ru.fastcards.editor;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.TextPack;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TextPacksAdapter extends ArrayAdapter<TextPack> {

	private Context context;
	
	public TextPacksAdapter(Context context, List<TextPack> textList) {
		super(context, R.layout.row_text_pack, textList);
		this.context = context;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			final TextPack row = (TextPack) getItem(position);
			
			if (row.isBought()){
				view = inflater.inflate(R.layout.row_recently_used_text, parent,	false);
				if (position==0) return view;
			}
			else view = inflater.inflate(R.layout.row_text_pack, parent,	false);

		
		TextView TitleTextView = (TextView) view.findViewById(R.id.tv_row_title);
		
		TextView priceTextView = (TextView) view.findViewById(R.id.tv_row_additional);
		
		ImageView starImageView = (ImageView) view.findViewById(R.id.row_star);
		
		TitleTextView.setText(row.getName());
	
		if (!row.isBought()){
				starImageView.setImageResource(R.drawable.star);
				priceTextView.setText(Integer.toString((int)row.getPrice()));
				}

		return view;
	}
}
