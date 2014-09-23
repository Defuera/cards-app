package ru.fastcards.recipientselectors;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.ListContacts;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.common.Recipient;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RecipientsSelectorAdapter extends ArrayAdapter<ISendableItem> {

	private List<ISendableItem> listItems;
	private Context context;
	private RecipientViewInflater recipientInflater;
	private GroupViewInflater groupInflater;

	public RecipientsSelectorAdapter(Context context, List<ISendableItem> listItems){
		super(context, R.layout.row_recipient, listItems);
		this.listItems = listItems;
		this.context = context;
		
		recipientInflater = new RecipientViewInflater(context, this);
		groupInflater = new GroupViewInflater(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		switch (getItemViewType(position)) {
		case 0:
		{
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.row_group, parent, false);	
			}
			convertView = groupInflater.getView((ListContacts) getItem(position), convertView);			
		}
			break;
		case 1:
		{
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.row_recipient, parent, false);	
			}
			convertView = recipientInflater.getRecipientView((Recipient) getItem(position), convertView);
		}
			break;
		default:
			break;
		}
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
	    return 2;
	}

	@Override
	public int getItemViewType(int position) {
	if(listItems.get(position).isGroup())
	    return 0;
	else
	    return 1;
	}
}
