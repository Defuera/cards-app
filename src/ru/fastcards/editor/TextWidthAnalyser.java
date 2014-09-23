//package ru.fastcards.editor;
//
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.text.InputFilter;
//import android.text.Spanned;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.widget.EditText;
//import android.widget.TextView;
//
//
//public class TextWidthAnalyser extends LineCountAnalyser{
//	private int lineWidth,maxLineWidth;
//	
//	public TextWidthAnalyser(Context context,ViewGroup container,EditText editInput) {
//		super(context);
//		this.editInput=editInput;
//		// TODO Auto-generated constructor stub
//		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
//		this.setVisibility(View.INVISIBLE);
//		this.setTextSize(editInput.getTextSize());
//		this.getViewTreeObserver().addOnGlobalLayoutListener(this);
//		this.setVisibility(TextView.INVISIBLE);
//		maxLineWidth=editInput.getLayoutParams().width-30;//correction-вы€влена методом тыка
//		
//		editInput.addTextChangedListener(watcher);
//		editInput.setFilters(new InputFilter[]{ new MyInputFilter() });
//
//		container.addView(this);
//	}
//	
//	@Override
//	public void onGlobalLayout() {
//		// TODO Auto-generated method stub
//		Rect r = new Rect();
//	     //создаЄм пр€моугольник r с координатами видимого пространства
//	     this.getDrawingRect(r);
//	     lineWidth=r.right - r.left;
//	}
//	
//	private class MyInputFilter implements InputFilter {
//		  public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
//			  if (lineWidth>maxLineWidth) return "";
//			  else return null;
//		  } 
//}
//	}