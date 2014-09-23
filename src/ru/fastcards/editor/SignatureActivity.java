package ru.fastcards.editor;



import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ru.fastcards.R;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.ScreenParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignatureActivity extends ActionBarActivity implements OnClickListener {
	private LinearLayout paintContainer;
	private SimplePaint simplePaint; 
	private TextView hint;
	private Bitmap bitmap=null;
	private Button ok,clear;
	private float width=ScreenParams.screenHeight*11/12;
	private float height=ScreenParams.screenWidth*11/12;
	private int form;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signature);
		
	
		red=getIntent().getExtras().getInt(Constants.RED);
		green=getIntent().getExtras().getInt(Constants.GREEN);
		blue=getIntent().getExtras().getInt(Constants.BLUE);
		form=getIntent().getExtras().getInt(Constants.EXTRA_POSTCARD_SELECTED);
		
		hint=(TextView) findViewById(R.id.hint);
		
		ok=(Button) findViewById(R.id.button_signature_ok);
		ok.setOnClickListener(this);
		
		clear=(Button) findViewById(R.id.button_clear);
		clear.setOnClickListener(this);
		
		RelativeLayout.LayoutParams paintParams=new RelativeLayout.LayoutParams((int)height,(int)width);
		paintParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		paintContainer=(LinearLayout) findViewById(R.id.paint_container);
		paintContainer.setLayoutParams(paintParams);
		
		simplePaint=new SimplePaint(this);
		
		paintContainer.addView(simplePaint);
		
		try {
			bitmap=Bitmap.createBitmap((int)width,(int)height,Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
		}
		
		setActionBar();

	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: {
			/*********************/
			Intent intent=new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
			return true;
			/*********************/
		}
		default:{
		}
			return super.onOptionsItemSelected(item);
		}
	}
	int red,green,blue;

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.button_signature_ok:
				resultBitmap();
				break;

		case R.id.button_clear:
				clearBitmap();
			break;
		}
 	}
	
	private void setActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.title_editor_activity));
	}
	
	private void resultBitmap(){
		
		Canvas canvas = new Canvas(bitmap);
		Matrix matrix=new Matrix();
		matrix.setRotate(90,width/2,width/2);
		canvas.setMatrix(matrix);
		
		simplePaint.draw(canvas); 
		
		Bitmap changeColorBitmap=changeColorBitmap(bitmap);
		
		Intent intent=new Intent();
		ByteArrayOutputStream streamV = new ByteArrayOutputStream();
		changeColorBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamV);
		byte[] byteArrayV = streamV.toByteArray();
		 
		intent.putExtra(Constants.SIGNATURE_VERTICAL, byteArrayV);

		ByteArrayOutputStream streamP = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, streamP);
		byte[] byteArrayP = streamP.toByteArray();
		
		intent.putExtra(Constants.SIGNATURE_PRINT, byteArrayP);
		
		setResult(RESULT_OK, intent);

		finish();
	}
	
	private Bitmap changeColorBitmap(Bitmap bitmap){
		
		Bitmap changeColorBitmap=Bitmap.createBitmap((int)width,(int)height,bitmap.getConfig());
		int origPixels[]=new int[(int)width*(int)height];
		bitmap.getPixels(origPixels, 0,(int) width, 0, 0,(int) width,(int) height);
		
		int changePixels[]=new int[(int)width*(int)height];
		
		for (int i=0;i<origPixels.length;i++){
			changePixels[i]=Color.argb(origPixels[i],origPixels[i]+red, origPixels[i]+green,
					origPixels[i]+blue);
		}
		
		changeColorBitmap.setPixels(changePixels, 0,(int) width, 0, 0,(int) width,(int) height);
		
		return changeColorBitmap;
	}
	
	private void clearBitmap(){
		simplePaint.clearCanvas();
		hint.setVisibility(View.VISIBLE);
	}
	
	private class SimplePaint extends View implements OnTouchListener{
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		private float initX,initY,x,y;
		ArrayList<Float> coordinates=new ArrayList<Float>();
		boolean cleared=false;
		static final int transparent=Color.TRANSPARENT;

		public SimplePaint(Context context) {
			// TODO Auto-generated constructor stub
			super(context);
			paint.setColor(transparent);
			this.setOnTouchListener(this);
			this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

		}
		

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			float[] pts=new float[coordinates.size()];
			for (int i=0;i<pts.length;i++){
				pts[i]=coordinates.get(i);
			}
			if (pts.length!=0) {
				paint.setStrokeWidth(5);
				paint.setColor(Color.BLACK);
				canvas.drawLines(pts, paint);
				canvas.save();
			}
			if (cleared) {
				canvas.drawColor(transparent);
				this.cleared=false;
			}

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
				hint.setVisibility(View.INVISIBLE);	
				initX=event.getX();
				initY=event.getY();
					break;
				case (MotionEvent.ACTION_MOVE):
					coordinates.add(initX);
					coordinates.add(initY);
					x=event.getX();
					y=event.getY();
					antialiasing();
					coordinates.add(x);
					coordinates.add(y);
					initX=x;
					initY=y;
					invalidate();
					break;
				case (MotionEvent.ACTION_UP):
					break;
			}
			return true;
		}
		
		private void clearCanvas(){
			cleared=true;
			coordinates.clear();
			invalidate();
		}
		
		private void antialiasing(){
			
			coordinates.add((x+initX)/2-(x-initX)*9/20);
			coordinates.add((y+initY)/2-(y-initY)*9/20);
			coordinates.add((x+initX)/2-(x-initX)*9/20);
			coordinates.add((y+initY)/2-(y-initY)*9/20);
			coordinates.add((x+initX)/2-(x-initX)*8/20);
			coordinates.add((y+initY)/2-(y-initY)*8/20);
			coordinates.add((x+initX)/2-(x-initX)*8/20);
			coordinates.add((y+initY)/2-(y-initY)*8/20);
			coordinates.add((x+initX)/2-(x-initX)*7/20);
			coordinates.add((y+initY)/2-(y-initY)*7/20);
			coordinates.add((x+initX)/2-(x-initX)*7/20);
			coordinates.add((y+initY)/2-(y-initY)*7/20);
			coordinates.add((x+initX)/2-(x-initX)*6/20);
			coordinates.add((y+initY)/2-(y-initY)*6/20);
			coordinates.add((x+initX)/2-(x-initX)*6/20);
			coordinates.add((y+initY)/2-(y-initY)*6/20);
			coordinates.add((x+initX)/2-(x-initX)*5/20);
			coordinates.add((y+initY)/2-(y-initY)*5/20);
			coordinates.add((x+initX)/2-(x-initX)*5/20);
			coordinates.add((y+initY)/2-(y-initY)*5/20);
			coordinates.add((x+initX)/2-(x-initX)*4/20);
			coordinates.add((y+initY)/2-(y-initY)*4/20);
			coordinates.add((x+initX)/2-(x-initX)*4/20);
			coordinates.add((y+initY)/2-(y-initY)*4/20);
			coordinates.add((x+initX)/2-(x-initX)*3/20);
			coordinates.add((y+initY)/2-(y-initY)*3/20);
			coordinates.add((x+initX)/2-(x-initX)*3/20);
			coordinates.add((y+initY)/2-(y-initY)*3/20);
			coordinates.add((x+initX)/2-(x-initX)*2/20);
			coordinates.add((y+initY)/2-(y-initY)*2/20);
			coordinates.add((x+initX)/2-(x-initX)*2/20);
			coordinates.add((y+initY)/2-(y-initY)*2/20);
			coordinates.add((x+initX)/2-(x-initX)*1/20);
			coordinates.add((y+initY)/2-(y-initY)*1/20);
			coordinates.add((x+initX)/2-(x-initX)*1/20);
			coordinates.add((y+initY)/2-(y-initY)*1/20);
			
			
			coordinates.add((x+initX)/2);
			coordinates.add((y+initY)/2);
			coordinates.add((x+initX)/2);
			coordinates.add((y+initY)/2);	
			
			
			coordinates.add((x+initX)/2+(x-initX)*1/20);
			coordinates.add((y+initY)/2+(y-initY)*1/20);
			coordinates.add((x+initX)/2+(x-initX)*1/20);
			coordinates.add((y+initY)/2+(y-initY)*1/20);
			coordinates.add((x+initX)/2+(x-initX)*2/20);
			coordinates.add((y+initY)/2+(y-initY)*2/20);
			coordinates.add((x+initX)/2+(x-initX)*2/20);
			coordinates.add((y+initY)/2+(y-initY)*2/20);
			coordinates.add((x+initX)/2+(x-initX)*3/20);
			coordinates.add((y+initY)/2+(y-initY)*3/20);
			coordinates.add((x+initX)/2+(x-initX)*3/20);
			coordinates.add((y+initY)/2+(y-initY)*3/20);
			coordinates.add((x+initX)/2+(x-initX)*4/20);
			coordinates.add((y+initY)/2+(y-initY)*4/20);
			coordinates.add((x+initX)/2+(x-initX)*4/20);
			coordinates.add((y+initY)/2+(y-initY)*4/20);
			coordinates.add((x+initX)/2+(x-initX)*5/20);
			coordinates.add((y+initY)/2+(y-initY)*5/20);
			coordinates.add((x+initX)/2+(x-initX)*5/20);
			coordinates.add((y+initY)/2+(y-initY)*5/20);
			coordinates.add((x+initX)/2+(x-initX)*6/20);
			coordinates.add((y+initY)/2+(y-initY)*6/20);
			coordinates.add((x+initX)/2+(x-initX)*6/20);
			coordinates.add((y+initY)/2+(y-initY)*6/20);
			coordinates.add((x+initX)/2+(x-initX)*7/20);
			coordinates.add((y+initY)/2+(y-initY)*7/20);
			coordinates.add((x+initX)/2+(x-initX)*7/20);
			coordinates.add((y+initY)/2+(y-initY)*7/20);
			coordinates.add((x+initX)/2+(x-initX)*8/20);
			coordinates.add((y+initY)/2+(y-initY)*8/20);
			coordinates.add((x+initX)/2+(x-initX)*8/20);
			coordinates.add((y+initY)/2+(y-initY)*8/20);
			coordinates.add((x+initX)/2+(x-initX)*9/20);
			coordinates.add((y+initY)/2+(y-initY)*9/20);
			coordinates.add((x+initX)/2+(x-initX)*9/20);
			coordinates.add((y+initY)/2+(y-initY)*9/20);
		}

	}
}
