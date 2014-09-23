package ru.fastcards.editor;


import ru.fastcards.R;
import ru.fastcards.utils.ImageManager;
import ru.fastcards.utils.ScreenParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class PostCardFragmentPrint extends PostCardFragment{

	private Bitmap front_image;
	private Bitmap back_image;
	private View v;
	private LinearLayout workspace;
	private EditText postcardText;
	private FrameLayout background;
	private LineCountAnalyser lineCountAnalyser;
	private ImageView frontImage;
	private ImageView backImage;
	private android.widget.LinearLayout.LayoutParams imageParams;
	private FrameLayout.LayoutParams backgroundParams;
	private LinearLayout.LayoutParams textParams,textZoomParams;
	private LinearLayout.LayoutParams signParams,signZoomParams;
	
	private FrameLayout reverce_side;
	
	
	private final String TAG="PrintPostCard";
	private RelativeLayout signature_container;
	private ImageView signature_image;
	private ImageView signature_background;
	private String frontfilename;
	private String backfilename;
	private ImageManager manager;
	private LayoutParams workspaceParams;
	private android.widget.LinearLayout.LayoutParams workspaceZoomParams;

	public PostCardFragmentPrint() {
		// TODO Auto-generated constructor stub
		manager = new ImageManager(context);
		
		frontfilename=manager.createFileNameFromUrl(theme.getPostCardFrontThumb());
		backfilename=manager.createFileNameFromUrl(theme.getPostCardBackThumb());
		
		front_image=manager.decodeBitmapFromFile(frontfilename);
		back_image=manager.decodeBitmapFromFile(backfilename);
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
		v=inflater.inflate(R.layout.postcard_fragment_print, null);
		createPrintPostCard();
		return v;
	}

	@Override
	public void onPause() {
	// TODO Auto-generated method stub	
		savePostcard();
		super.onPause();
	}
//*****************************************************************************************
	
@Override
public void savePostcard() {
	card.setText(postcardText.getText().toString());
//	if (card.getSignatureImage()!=null) card.setStateSignature(Constants.OPEN);
	card.setPrintSignature(card.getSignatureImage());
}

private void createPrintPostCard(){
	
		front_image=manager.decodeBitmapFromFile(frontfilename);
		back_image=manager.decodeBitmapFromFile(backfilename);
	
		frontImage=(ImageView) v.findViewById(R.id.postcard_front_image);
		frontImage.setImageBitmap(front_image);
		
		backImage=(ImageView)v.findViewById(R.id.postcard_back_image);
		backImage.setImageBitmap(back_image);

		reverce_side=(FrameLayout)v.findViewById(R.id.reverse_side);
		
		workspace=(LinearLayout) v.findViewById(R.id.workspace_print);

		//Поле для ввода текстовой подписи
		signature_container=(RelativeLayout)v.findViewById(R.id.signature_container);
		
		
		//Для перехода на окно с рисованием подписи
		
		signature_background=(ImageView) v.findViewById(R.id.signature_background);
		
		signature_image=(ImageView) v.findViewById(R.id.signature_image);
		
		
		//Поле ввода текста в открытку
		postcardText=(EditText) v.findViewById(R.id.postcard_text);
		
		
		//Фон-пока что не нужнен
		background=(FrameLayout) v.findViewById(R.id.background);
		
		lineCountAnalyser=new LineCountAnalyser(context,background, postcardText);

	     
		createLayoutParams();
	     
	    statePostcardText();
	    
	    frontImage.setFocusableInTouchMode(true);
	    frontImage.requestFocus();

	    setListeners();	
	}
	
	
private void setListeners(){
	signature_background.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PostCardListener.onStartSignatureActivityEvent();
			}
		});
	 	
	frontImage.setOnTouchListener(new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			close();
			return false;
		}
	});
	backImage.setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			close();
			return false;
		}
	});
}

private void close(){
	InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(postcardText.getWindowToken(), 0);
	if (getEditableMode()){
		setWithoutZoomParams();
		PostCardListener.onCloseEditorModeEvent();
		postcardText.clearFocus();}
}

private void createLayoutParams(){
	
	float backgroundHeight=ScreenParams.screenHeight*9/12;
	float backgroundWidth=backgroundHeight*1252/1843;
	
	
	float imageWidth=backgroundWidth;
	float imageHeight=backgroundWidth*1252/1843;

	
	float textWidth=imageWidth*530/1843;
	float textHeight=imageHeight*330/1252;
	
	float scaledWidth=imageWidth/1843;
	float scaledHeight=imageHeight/1252;
	
	float leftMargin=theme.getpTextLeft()*scaledWidth;
	float topMargin=theme.getpTextTop()*scaledHeight;
	

	
	//Params for all workspace
	backgroundParams=new FrameLayout.LayoutParams((int)backgroundWidth,(int)backgroundHeight);
	
	//Params for image
	imageParams=new LinearLayout.LayoutParams((int)imageWidth,(int)imageHeight);//Временное решение
	imageParams.gravity=Gravity.CENTER_HORIZONTAL;
	
	textParams=new LinearLayout.LayoutParams((int)(textWidth),(int)(textHeight));
	textParams.leftMargin=(int)leftMargin;
	textParams.topMargin=(int)(topMargin-textHeight);
	
	workspaceParams=new FrameLayout.LayoutParams((int)(textWidth),(int)(textHeight));
	workspaceParams.leftMargin=(int)leftMargin;
	workspaceParams.topMargin=(int)(topMargin-textHeight);
	
	signParams=new LinearLayout.LayoutParams((int)(ScreenParams.screenHeight/12),(int)(ScreenParams.screenWidth/12));
	signParams.leftMargin=(int)leftMargin;
	
	textZoomParams=new LinearLayout.LayoutParams((int)(textWidth*2),(int)(textHeight*2));
	textZoomParams.leftMargin=(int)leftMargin;
	
	workspaceZoomParams=new LinearLayout.LayoutParams((int)(textWidth*2),(int)(textHeight*2));
	workspaceZoomParams.leftMargin=(int)leftMargin;
	
	signZoomParams=new LinearLayout.LayoutParams((int)(ScreenParams.screenHeight/6),(int)(ScreenParams.screenWidth/6));
	signZoomParams.leftMargin=(int)leftMargin;
	
	background.setLayoutParams(backgroundParams);
		
     //Set Layout params
	frontImage.setLayoutParams(imageParams);
	reverce_side.setLayoutParams(imageParams);
	
	setWithoutZoomParams();
	
 	textStyle();
	
}

private void setWithoutZoomParams(){
//	postcardText.setLayoutParams(textParams);
	workspace.setLayoutParams(textParams);
	signature_container.setLayoutParams(signParams);
	textStyle();
}

private void setZoomParams(){
//	postcardText.setLayoutParams(textZoomParams);
	workspace.setLayoutParams(textZoomParams);
	signature_container.setLayoutParams(signZoomParams);
	textStyle();
}

public Bitmap getHorisontalBitmap(Bitmap largeBitmap){
	
	postcardText.setDrawingCacheEnabled(false);
	postcardText.setText(null);
	
	int textWidth = 530;
	int textHeight = 330;

	int width=largeBitmap.getWidth();
	int height=largeBitmap.getHeight();
	
	Bitmap bitmap=Bitmap.createBitmap(width, height,Config.ARGB_8888);
	
	Canvas canvas=new Canvas(bitmap);
	canvas.drawColor(Color.WHITE);

	
	Rect dst = new Rect(0, 0, width, height);
	canvas.drawBitmap(largeBitmap, null, dst, new Paint());

	postcardText.setText(card.getText());
	postcardText.setCursorVisible(false);
	postcardText.buildDrawingCache();
	Bitmap textBitmap=Bitmap.createBitmap(postcardText.getDrawingCache());
	
	int left=theme.getpTextLeft();
	int top=theme.getpTextTop()-textHeight;
	int right=theme.getpTextLeft()+textWidth;
	int bottom=theme.getpTextTop();
	
	Rect textRect = new Rect(left, top, right, bottom);
	canvas.drawBitmap(textBitmap, null, textRect, new Paint());
	
	int signLeft=theme.getpTextLeft();
	int signTop=theme.getpTextTop();
	int signRight=theme.getpTextLeft()+textWidth/2;
	int signBottom=theme.getpTextTop()+textHeight/2;
	
	Rect signRect = new Rect(signLeft, signTop, signRight , signBottom);
	
	Bitmap signatureBitmap=card.getSignatureImage();
	if (signatureBitmap!=null)
		canvas.drawBitmap(signatureBitmap, null, signRect, new Paint());
	
	
	return textBitmap;
}



private void textStyle(){
	float textSize=workspace.getLayoutParams().height/12;
	int unit=TypedValue.COMPLEX_UNIT_PX;
	 postcardText.setTextColor(Color.BLACK);
	 postcardText.setTextSize(unit,textSize);
	 
	 }

private void RestoreText(){
	postcardText.setText(card.getText());
	signature_image.setImageBitmap(card.getSignatureImage());
}


@Override
public void setTextPostCard(String text){
		postcardText.setText(text);
}


private void statePostcardText(){
	postcardText.setSelection(0);
	postcardText.setLines(8);
	lineCountAnalyser.setLineCount(textFieldLines());
}

public boolean getEditableMode(){
	if (postcardText==null) return false;
	return postcardText.isFocused();
}

public void ZoomText(){
	setZoomParams();
}

public String getText(){
	card.setText(postcardText.getText().toString());
	return card.getText();
}
}