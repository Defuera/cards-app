package ru.fastcards.editor;

import ru.fastcards.R;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.ImageManager;
import ru.fastcards.utils.ScreenParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;


public class PostCardFragmentVertical extends PostCardFragment{
	private View v;
	private FrameLayout workspace;
	private ImageView postcardBackground;
//	private TextView title;
//	private EditText signature;
	private ImageView signatureImage;
	private EditText postcardText;
	private FrameLayout background;
	private LineCountAnalyser lineCountAnalyser;
//	private TextWidthAnalyser widthAnalyser;
//	private LayoutParams all_closed;
	private android.widget.FrameLayout.LayoutParams workspace_params;
	private android.widget.FrameLayout.LayoutParams image_params;
	private LayoutParams opened;
	private android.widget.FrameLayout.LayoutParams opened_image;	
//	private FrameLayout.LayoutParams closed_image;		
//	private LayoutParams closed;
//	private LayoutParams one_opened;
//	private LayoutParams all_opened;
	private Bitmap pictureFromTheme;
//	private LinearLayout titleContainer;
	private RelativeLayout signatureContainer;
//	private TextView recepient;
	
	private final int textColor=Color.rgb(theme.getTextColorRed(), theme.getTextColorGreen(), theme.getTextColorBlue());
	
	private ImageView signature_background;
	
	
//	private final String TAG="VerticalPostCard";
	
	public PostCardFragmentVertical() {
		ImageManager manager = new ImageManager(context);	
		String filename=manager.createFileNameFromUrl(theme.getECardThumb());		
		pictureFromTheme = manager.decodeBitmapFromFile(filename);
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

		v = inflater.inflate(R.layout.postcard_fragment_vertical, null);
		createVerticalPostCard();
		return v;
	}
	
	@Override
	public void onPause() {
			// TODO Auto-generated method stub
			savePostcard();
			super.onPause();
	}
//*****************************************************************************************		
	
	private void createVerticalPostCard(){
		postcardBackground=(ImageView) v.findViewById(R.id.postcard_background);
		postcardBackground.setImageBitmap(pictureFromTheme);
		
		workspace=(FrameLayout) v.findViewById(R.id.workspace_vertical);
		
		//Container for title fields
//		titleContainer=(LinearLayout)v.findViewById(R.id.title_container);
		//Поле для ввода обращения
//		title=(TextView) v.findViewById(R.id.asking);
//		recepient=(TextView) v.findViewById(R.id.recepient);

		//Container for signature
		signatureContainer=(RelativeLayout) v.findViewById(R.id.signature_container);
		
		
//		signature=(EditText) v.findViewById(R.id.signature_text);
		
		signature_background=(ImageView) v.findViewById(R.id.signature_background);
		
		//Для перехода на окно с рисованием подписи
		signatureImage=(ImageView) v.findViewById(R.id.signature_image);
		
		//Поле ввода текста в открытку
		postcardText=(EditText) v.findViewById(R.id.postcard_text);
		
		
		//Фон-пока что не нужнен
		background=(FrameLayout) v.findViewById(R.id.background);
		
		lineCountAnalyser=new LineCountAnalyser(context,background, postcardText);
		
	    createLayoutParams();
	    statePostcardText();

	 	textStyle();
	 	
	 	postcardBackground.setFocusableInTouchMode(true);
	 	postcardBackground.requestFocus();	
	   
	    setListeners();	
	}
	
	public void hideSoftInput(){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(postcardText.getWindowToken(), 0);

	}
	private void setListeners(){
		
//		 	titleContainer.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					//Закрываеми клавиатуру
//					hideSoftInput();
//					PostCardListener.onAppealClickEvent();
//				}
//			});
		 	
		 	signature_background.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					PostCardListener.onStartSignatureActivityEvent();
				}
			});
		 	
			postcardBackground.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(postcardText.getWindowToken(), 0);
					if (getEditableMode()){
//						closeEditableMode();
						PostCardListener.onCloseEditorModeEvent();
						postcardText.clearFocus();
//						signature.clearFocus();
						}
					
					return false;
				}
			});
		}
	
	public boolean getEditableMode(){
		if (postcardText==null)/*||signature==null)*/ return false;
		return postcardText.isFocused();//||signature.isFocused();
	}
	private void createLayoutParams(){
		
		float imageHeight=ScreenParams.screenHeight*9/12;
		float imageWidth=imageHeight*640/960;
		
		float layoutWidth=imageWidth*530/640;
		float layoutHeight=imageHeight*330/960;
		
		float scaledWidth=imageWidth/640;
		float scaledHeight=imageHeight/960;
		
		float leftMargin=theme.getETextLeft()*scaledWidth;
		float topMargin=theme.getETextTop()*scaledHeight;
		
		//Params for image
		image_params=new FrameLayout.LayoutParams((int)imageWidth,(int)imageHeight);
		image_params.gravity=Gravity.CENTER_HORIZONTAL;
		
		//Params for all workspace
		workspace_params=new FrameLayout.LayoutParams((int)layoutWidth,(int)layoutHeight);
		workspace_params.leftMargin=(int)leftMargin;
		workspace_params.topMargin=(int)topMargin;
		
		LayoutParams editTextParams = new LinearLayout.LayoutParams((int)layoutWidth,(int)layoutHeight);
		
		opened=new LinearLayout.LayoutParams((int)layoutWidth/2,(int)layoutHeight/4);
		opened.gravity=Gravity.CENTER_HORIZONTAL;
		
//		closed=new LinearLayout.LayoutParams((int)layoutWidth,0);
//		closed.gravity=Gravity.CENTER_HORIZONTAL;
		
		
		opened_image=new FrameLayout.LayoutParams((int)(imageHeight/4),(int)(imageWidth/5));
		opened_image.leftMargin=(int)((layoutWidth/2)+((layoutWidth/2)-(imageHeight/4)));
		opened_image.gravity=Gravity.BOTTOM;
		
//		closed_image=new FrameLayout.LayoutParams(0,0);
//		
//		one_opened=new LinearLayout.LayoutParams((int)layoutWidth,((int)layoutHeight/4)*3);
//		all_opened=new LinearLayout.LayoutParams((int)layoutWidth,(int)layoutHeight/2);
		
		
		
	     //Set Layout params
		background.setLayoutParams(image_params);
	    postcardBackground.setLayoutParams(image_params);
	    workspace.setLayoutParams(workspace_params);
//	    titleContainer.setLayoutParams(closed);
//	    signature.setLayoutParams(closed);
	    signatureContainer.setLayoutParams(opened_image);
	 	postcardText.setLayoutParams(editTextParams);
//	    postcardText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

	}
	
	private void textStyle(){
		float textSize=workspace.getLayoutParams().height/10;
		int unit=TypedValue.COMPLEX_UNIT_PX;
//		signature.setTextColor(textColor);
		postcardText.setTextColor(textColor);
//		title.setTextColor(textColor);
//		recepient.setTextColor(textColor);
		
		 postcardText.setTextSize(unit,textSize);
//		 title.setTextSize(unit,textSize);
//		 recepient.setTextSize(unit,textSize);
//		 signature.setTextSize(unit,textSize);
		 
//		 widthAnalyser=new TextWidthAnalyser(context,background,signature);
		 } 
	
	private void RestoreText(){
		createLayoutParams();
		postcardText.setText(card.getText());
//		if (card.getStateAppeal()==Constants.OPEN){
//			titleContainer.setLayoutParams(opened);
//			if (card.getAppeal()!=null){
//			title.setText(card.getAppeal().getMaleAppeal());
//			}
//		}
//		else {
//			titleContainer.setLayoutParams(closed);
//		}
//		if (card.getStateSignature()==Constants.OPEN){
////			signature.setText(card.getSignature());
		if (card.getSignatureBitmapUri()!=null) 
			loadSignatureImage();
		else 
			 signatureImage.setImageBitmap(card.getSignatureImage());
//			
////			signature.setLayoutParams(opened);
//			signatureContainer.setLayoutParams(opened_image);
//		}
//		else {
////			signature.setLayoutParams(closed);
//			signatureContainer.setLayoutParams(closed_image);
//			}
		postcardText.setCursorVisible(true);
		statePostcardText();	
	}
	
//	@Override
//	public void setStateAppeal(int state){
//		card.setStateAppeal(state);
//		switch (state){
//		case Constants.OPEN:
//			titleContainer.setLayoutParams(opened);
//			break;
//		case Constants.CLOSE:
//			titleContainer.setLayoutParams(closed);
//		}
//		statePostcardText();}
//	
//	@Override
//	public void setStateSignature(int state){
//		card.setStateSignature(state);
//		switch (state){
//		case Constants.OPEN:
////			signature.setLayoutParams(opened);
//			signatureContainer.setLayoutParams(opened_image);
//			break;
//		case Constants.CLOSE:
////			signature.setLayoutParams(closed);
//			signatureContainer.setLayoutParams(closed_image);
//		}
//		statePostcardText();
//	}

//	public void setAppeal(Appeal appeal){
//		card.setAppeal(appeal);
//		title.setText(appeal.getMaleAppeal());
//	}

	@Override
	public void setTextPostCard(String text){
		postcardText.setText(text);
	}
	
	private void statePostcardText(){
//		switch(textFieldLines()){
//		case 8: 
//			postcardText.setLayoutParams(all_closed);
//			break;
//		case 6: 
//			postcardText.setLayoutParams(one_opened);
//			break;
//		case 4: 
//			postcardText.setLayoutParams(all_opened);
//			break;
//		}
		postcardText.setSelection(0);
		postcardText.setLines(textFieldLines());
		lineCountAnalyser.setLineCount(textFieldLines());
	}
	

	//Save the fields in class Postcard
	@Override
	public void savePostcard() {

//		if (card.getStateAppeal()==Constants.CLOSE)
//			card.setAppeal(null);
//		
//		switch (card.getStateSignature()){
//		case Constants.OPEN:
////			card.setSignature(signature.getText().toString());
//			break;
//		case Constants.CLOSE:
//			card.setSignature(null);
//			break;
//		}
		card.setText(postcardText.getText().toString());
	}
	
	public ImageView getSignatureImageView(){
		return signatureImage;
	}
	
	public String getText(){
		card.setText(postcardText.getText().toString());
		return card.getText();
	}
	
	private void loadSignatureImage(){
		ImageManager manager=new ImageManager(context);
		String filename=manager.createFileNameFromUrl(card.getSignatureBitmapUri());
		
		BitmapLoaderAsyncTask loader=new BitmapLoaderAsyncTask(context, null, false, false);
		loader.loadImageAsync(filename, signatureImage, null);
	}

	public Bitmap getVerticalPostcard(Bitmap largeBitmap){

		postcardText.setDrawingCacheEnabled(false);
		postcardText.setText(null);

		int textWigth = 530;
		int textHeight = 330;

		int width=largeBitmap.getWidth();
		int height=largeBitmap.getHeight();
		
		Bitmap bitmap=Bitmap.createBitmap(width, height,Config.ARGB_8888);
		
		Canvas canvas=new Canvas(bitmap);
		
		Rect dst = new Rect(0, 0, width, height);
		canvas.drawBitmap(largeBitmap, null, dst, new Paint());

		postcardText.setCursorVisible(false);
		postcardText.setText(card.getText());
		postcardText.buildDrawingCache();
		Bitmap textBitmap=Bitmap.createBitmap(postcardText.getDrawingCache());
		
		int left=theme.getETextLeft();
		int top=theme.getETextTop();
		int right=theme.getETextLeft()+textWigth;
		int bottom=theme.getETextTop()+textHeight;
		
		Rect textRect = new Rect(left, top, right, bottom);
		canvas.drawBitmap(textBitmap, null, textRect, new Paint());
		
		int signLeft=textWigth/2;
		int signTop=textHeight/2;
		
		Rect signRect = new Rect(right-signLeft, bottom-signTop,right, bottom);
		
		Bitmap signatureBitmap=card.getSignatureImage();
		if (signatureBitmap!=null)
			canvas.drawBitmap(signatureBitmap, null, signRect, new Paint());

		return bitmap;
	}
}
