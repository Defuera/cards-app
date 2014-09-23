
package ru.fastcards.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class LineCountAnalyser extends TextView implements OnGlobalLayoutListener, OnKeyListener {
	private int lineHeight;
	private int lineCount,maxLineCount;
	private LayoutParams params;
	EditText editInput;
	private String value="";
	Watcher watcher=new Watcher();
	
	public LineCountAnalyser(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}
	
	public LineCountAnalyser(Context context,ViewGroup container,EditText editInput) {
		super(context);
		// TODO Auto-generated constructor stub
		this.editInput=editInput;
		params=new LayoutParams(editInput.getLayoutParams().width,LayoutParams.WRAP_CONTENT);
		lineHeight=editInput.getLineHeight();
		this.setLayoutParams(params);
		this.setTextSize(editInput.getTextSize()/2);
		this.getViewTreeObserver().addOnGlobalLayoutListener(this);
		this.setVisibility(TextView.INVISIBLE);

		editInput.addTextChangedListener(watcher);
		editInput.setOnKeyListener(this);
		editInput.setFilters(new InputFilter[]{ new MyInputFilter() });

		container.addView(this);
		
	}
	public void setTextForAnalyse(String value){
		if (value.length()==0) value="";
		else this.value+=value.charAt(value.length()-1);
		this.setText(value);
	}
	@Override
	public void onGlobalLayout() {
		// TODO Auto-generated method stub
		Rect r = new Rect();
	     //создаём прямоугольник r с координатами видимого пространства
	     this.getDrawingRect(r);
	     int heightDiff = (r.bottom - r.top);
	     lineCount=(int)(heightDiff/lineHeight);
	}
	
	public int getLineCount(){
		return lineCount;
	}
	
	public void setLineCount(int count){
		maxLineCount=count;
	}	



	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction()==KeyEvent.ACTION_DOWN){
			if (keyCode==KeyEvent.KEYCODE_ENTER){
				if (lineCount==maxLineCount) return true;
				else return false;
			}
			if (keyCode==KeyEvent.KEYCODE_DEL){
			}
		}
		return false;
	}
	
	private class Watcher implements TextWatcher{

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			value=editInput.getText().toString();
			LineCountAnalyser.this.setTextForAnalyse(value);
		}
	}

	private class MyInputFilter implements InputFilter {
		  public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
			  if (lineCount>maxLineCount) return "";
			  else return null;
		  } 
		}
}
