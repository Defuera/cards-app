package ru.fastcards.editor;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Appeal;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TitlesAdapter extends ArrayAdapter{

	private Context context;
	private List<Appeal> appealsList;
	public TitlesAdapter(Context context, List<Appeal> appealsList) {
		super(context, R.layout.row_appeals, appealsList);
		this.context = context;
		this.appealsList = appealsList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v=inflater.inflate(R.layout.row_appeals, parent,	false);
		
		final Appeal row = (Appeal) getItem(position);
		
		TextView appeal_text=(TextView) v.findViewById(R.id.appeal_text);
		
		if (position==0) appeal_text.setText(context.getResources().getString(R.string.whithout_appeal));
		
		else appeal_text.setText(row.getMaleAppeal()+" / "+row.getFemaleAppeal());
		
		return v;
	}
}
