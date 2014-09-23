//package ru.fastcards.editor;
//
//import ru.fastcards.R;
//import ru.fastcards.utils.Constants;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//
//
//public class PostcardEditButtonsFragment extends Fragment{
//	private ImageButton buttonAppeal,buttonSignature;
//	private PostCardFragment fragment;
//	public PostcardEditButtonsFragment() {
//		// TODO Auto-generated constructor stub
//	}
//	  
//	  private OnClickListener onAppealClickListener;
//	  private OnClickListener onSignatureClickListener;
//	  
//	  @Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		if (fragment.getStateAppeal()==Constants.CLOSE)buttonAppeal.setImageResource(Constants.ADD);
//		else buttonAppeal.setImageResource(Constants.DELETE);
//		if (fragment.getStateSignature()==Constants.CLOSE) buttonSignature.setImageResource(Constants.ADD);
//		else buttonSignature.setImageResource(Constants.DELETE);
//	}
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		View v=inflater.inflate(R.layout.postcard_edit_buttons, null);
//		
//		buttonAppeal=(ImageButton) v.findViewById(R.id.button_appeal);
//		buttonSignature=(ImageButton) v.findViewById(R.id.button_signature);
//		
//		v.findViewById(R.id.layout_appeal).setOnClickListener(onAppealClickListener);
//		v.findViewById(R.id.layout_signature).setOnClickListener(onSignatureClickListener);
//		
//		buttonAppeal.setOnClickListener(onAppealClickListener);
//		buttonSignature.setOnClickListener(onSignatureClickListener);
//		
//		return v;
//	}
//	
//	public void setAppealClickListener(OnClickListener listener){
//		onAppealClickListener=listener;
//	}
//	
//	public void setSignatureClickListener(OnClickListener listener){
//		onSignatureClickListener=listener;
//	}
//	
//	public void setButtonTitleBackground(int resID){
//		buttonAppeal.setImageResource(resID);
//	}
//	
//	public void setButtonSignatureBackground(int resID){
//		buttonSignature.setImageResource(resID);
//	}
//	
//	public void setEditableFragment(PostCardFragment fragment){
//		this.fragment=fragment;
//	}
//}
