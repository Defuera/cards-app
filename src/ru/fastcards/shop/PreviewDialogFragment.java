package ru.fastcards.shop;

import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;


public class PreviewDialogFragment extends DialogFragment{

	private OnClickListener onBuyClickListener;
	private OnClickListener onChooseClickListener;
	private LinearLayout.LayoutParams title_params;
	private LinearLayout.LayoutParams content_params;
	private DialogFragment dialog;
	
	public PreviewDialogFragment() {
		// TODO Auto-generated constructor stub
		dialog=this;
	}
	
	void createLayoutParams(){
		 DisplayMetrics metrics = new DisplayMetrics();
		 getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 title_params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,metrics.heightPixels/10);
		 content_params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,(metrics.heightPixels*7)/10);
	}
	
	public void setOnBuyClickListener(OnClickListener onClickListener){
		onBuyClickListener=onClickListener;
	}
	

	
	public void setOnChooseClickListener(OnClickListener onClickListener){
		onChooseClickListener=onClickListener;
	}
	
	private OnClickListener onCancelClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.dismiss();
		}
	};
	
	public void refreshDialogButtons(){	
		dismiss();
	}
	
	public LinearLayout.LayoutParams getTitleParams(){
		return title_params;
	}
	
	public LinearLayout.LayoutParams getContentParams(){
		return content_params;
	}
	
	public OnClickListener getOnOkClickListener(){
		return onCancelClickListener;
	}
	
	public OnClickListener getOnBuyClickListener(){
		return onBuyClickListener;
	}
	
	public OnClickListener getOnCancelClickListener(){
		return onCancelClickListener;
	}
	
	public OnClickListener getOnChooseClickListener(){
		return onChooseClickListener;
	}
	
	

}
