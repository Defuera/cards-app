package ru.fastcards.shop;

import ru.fastcards.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class BottomPanelShopFragment extends Fragment implements OnClickListener{
	
	private ImageButton[] bottomButtons=new ImageButton[4]; 
	private TextView[] bottomTextViews=new TextView[4];
	private View v;
	
	private static final int orange_text=Color.rgb(255, 204, 0);
	
	public interface onBottomButtonClickListener{
		public void onBottomButtonClick(int ID);
	}

	private onBottomButtonClickListener bottonButtonClickListener;
	
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	        	bottonButtonClickListener = (onBottomButtonClickListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement onBottomButtonClickListener");
	        }
	  }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v=inflater.inflate(R.layout.bottom_panel_shop_fragment, null);
		createButtonArray();
		createTextArray();
		return v;
	}
	
	private void createButtonArray(){
		bottomButtons[0]=(ImageButton) v.findViewById(R.id.selection);
		bottomButtons[0].setOnClickListener(this);
		bottomButtons[0].setEnabled(false);	
		bottomButtons[1]=(ImageButton) v.findViewById(R.id.catalog);
		bottomButtons[1].setOnClickListener(this);
		bottomButtons[2]=(ImageButton) v.findViewById(R.id.purchase);
		bottomButtons[2].setOnClickListener(this);	
		bottomButtons[3]=(ImageButton) v.findViewById(R.id.bundle);
		bottomButtons[3].setOnClickListener(this);
	}
	
	private void createTextArray(){
		bottomTextViews[0]=(TextView) v.findViewById(R.id.selection_text);
		bottomTextViews[1]=(TextView) v.findViewById(R.id.catalog_text);
		bottomTextViews[2]=(TextView) v.findViewById(R.id.purchase_text);
		bottomTextViews[3]=(TextView) v.findViewById(R.id.bundle_text);
		
		bottomTextViews[0].setOnClickListener(this);
		bottomTextViews[1].setOnClickListener(this);
		bottomTextViews[2].setOnClickListener(this);
		bottomTextViews[3].setOnClickListener(this);
		
		bottomTextViews[0].setTextColor(orange_text);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		for (int i=0;i<4;i++){
			if (v.equals(bottomButtons[i])||v.equals(bottomTextViews[i]))
			{
				bottonButtonClickListener.onBottomButtonClick(i);
				changeColorText(i);
			}
		}
		}
	
	public void changeColorText(int ID){
		for (int i=0;i<4;i++){
			bottomButtons[i].setEnabled(true);
			bottomTextViews[i].setTextColor(Color.WHITE);
			if (i==ID){
				bottomButtons[i].setEnabled(false);
				bottomTextViews[i].setTextColor(orange_text);
			}
		}
		
	}
}
