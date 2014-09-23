package ru.fastcards.editor;

import ru.fastcards.R;
import ru.fastcards.utils.ImageManager;
import ru.fastcards.utils.ScreenParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PostCardFragmentSquare extends PostCardFragment{

	private View v;
	private LinearLayout workspace;
	private ImageView postcard_background;
	private EditText postcard_text;
	private FrameLayout background;
	private LineCountAnalyser lineCountAnalyser;
	private FrameLayout.LayoutParams workspaceParams;
	private LinearLayout.LayoutParams imageParams;
	private LinearLayout.LayoutParams textParams;
	private Bitmap picture_from_theme;
	
	private final String TAG="SquarePostCard";

	public PostCardFragmentSquare() {
		// TODO Auto-generated constructor stub
		ImageManager manager = new ImageManager(context);
		
		String filename=manager.createFileNameFromUrl(theme.getSquareThumb());
		
		picture_from_theme=manager.decodeBitmapFromFile(filename);
	}

//OVERRIDED FRAGMENTS METHODS************************************************************	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		RestoreText();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		v = inflater.inflate(R.layout.postcard_fragment_square, null);
		createSquarePostCard();
		return v;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		savePostcard();
		super.onPause();
	}
//*****************************************************************************************	

	//Save the fields in class Postcard
	@Override
	public void savePostcard() {
		card.setText(postcard_text.getText().toString());
	}
	
	private void createSquarePostCard(){
		
		workspace=(LinearLayout) v.findViewById(R.id.workspace);
		
		postcard_background=(ImageView) v.findViewById(R.id.postcard_background);
		postcard_background.setImageBitmap(picture_from_theme);
		
		//Поле ввода текста в открытку
			postcard_text=(EditText) v.findViewById(R.id.postcard_text);
		
		//Фон
		background=(FrameLayout) v.findViewById(R.id.background);
		
		lineCountAnalyser=new LineCountAnalyser(context,background, postcard_text);
		
		 createLayoutParams();
	     statePostcardText();
	     
	     textStyle();
	 	
		 postcard_background.setFocusableInTouchMode(true);
		 postcard_background.requestFocus();	
		 	
	 	setSquareListeners();
	}

	private void setSquareListeners(){
			postcard_background.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(postcard_text.getWindowToken(), 0);
					if (getEditableMode()){
						PostCardListener.onCloseEditorModeEvent();
						postcard_text.clearFocus();}
					return false;
				}
			});
	}
	
@Override
public void setTextPostCard(String text){
		postcard_text.setText(text);
}


	private void createLayoutParams(){
		float workspaceHeight=ScreenParams.screenHeight*9/12;
		float workspaceWidth=workspaceHeight*640/960;

		workspaceParams=new FrameLayout.LayoutParams((int)workspaceWidth,(int)workspaceHeight);
		imageParams=new LinearLayout.LayoutParams((int)workspaceWidth,(int)workspaceWidth);
		textParams=new LinearLayout.LayoutParams((int)workspaceWidth,(int)(workspaceHeight-workspaceWidth));
		
		background.setLayoutParams(workspaceParams); 
		workspace.setLayoutParams(workspaceParams); 	
	 	postcard_background.setLayoutParams(imageParams);
	 	postcard_text.setLayoutParams(textParams);

	}

	
	private void textStyle(){
	 	postcard_text.setTextColor(Color.BLACK);
	 	postcard_text.setTextSize(TypedValue.COMPLEX_UNIT_PX ,postcard_text.getLayoutParams().height/10);
	}
	private void statePostcardText(){
		postcard_text.setSelection(0);
		postcard_text.setLines(8);
		lineCountAnalyser.setLineCount(8);
	}

	private void RestoreText(){	
		postcard_text.setText(card.getText());
	}
	
	public boolean getEditableMode(){
		if (postcard_text==null) return false;
		return postcard_text.isFocused();
	}
	
	public String getText(){
		card.setText(postcard_text.getText().toString());
		return card.getText();
	}
}
