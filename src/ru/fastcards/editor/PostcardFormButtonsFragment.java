package ru.fastcards.editor;

import ru.fastcards.R;
import ru.fastcards.utils.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class PostcardFormButtonsFragment extends Fragment implements OnClickListener{
	private ImageButton vertical,square,print;
	private int selected_postcard;
	
	public interface onFormChangeListener {
		public void onFormChangeEvent(int form);
	  }
	
	private onFormChangeListener formChangeListener;
	
	 @Override
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	        	formChangeListener = (onFormChangeListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + "onFormChangeListener");
	        }
	  }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v;
		v=inflater.inflate(R.layout.postcard_form_buttons, null);
		
		
		vertical=(ImageButton) v.findViewById(R.id.vertical_form_button);
		square=(ImageButton) v.findViewById(R.id.square_form_button);
		print=(ImageButton) v.findViewById(R.id.print_form_button);
		
		vertical.setOnClickListener(this);
		square.setOnClickListener(this);
		print.setOnClickListener(this);
		
		switch (selected_postcard){
		case Constants.POSTCARD_VERTICAL: vertical.setEnabled(false); break;
		case Constants.POSTCARD_SQUARE: square.setEnabled(false); break;
		case Constants.POSTCARD_HORIZONTAL: print.setEnabled(false); break;
		}
		
		return v;
	}

	public void setSelectedPostcard(int selected_postcard){
		this.selected_postcard=selected_postcard;
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		vertical.setEnabled(true);
		square.setEnabled(true);
		print.setEnabled(true);
		
		switch(v.getId()){			
		case R.id.vertical_form_button:
			formChangeListener.onFormChangeEvent(Constants.POSTCARD_VERTICAL);
			vertical.setEnabled(false);
			break;
		case R.id.square_form_button:
			formChangeListener.onFormChangeEvent(Constants.POSTCARD_SQUARE);
			square.setEnabled(false);
			break;
		case R.id.print_form_button:
			formChangeListener.onFormChangeEvent(Constants.POSTCARD_HORIZONTAL);
			print.setEnabled(false);
			break;
		}
	}

}
