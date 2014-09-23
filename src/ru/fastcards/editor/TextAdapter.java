package ru.fastcards.editor;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Text;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TextAdapter extends ArrayAdapter<Text> {
	
	public TextAdapter(Context context, List<Text> textList) {
		super(context, R.layout.row_text, textList);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View view = inflater.inflate(R.layout.row_text, null);
		
		final Text item = (Text) getItem(position);
		
		TextView text_view = (TextView) view.findViewById(R.id.tv_text);
		text_view.setText(item.getName());
		return view;
	}
}
