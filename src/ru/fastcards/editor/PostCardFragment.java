package ru.fastcards.editor;

import ru.fastcards.common.Appeal;
import ru.fastcards.common.Postcard;
import ru.fastcards.common.Theme;
import ru.fastcards.utils.Constants;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;



public class PostCardFragment extends Fragment{
	
	
	static Context context;
	static Postcard card;
	static Theme theme;
	
	
public PostCardFragment() {
		// TODO Auto-generated constructor stub
	}

public static PostCardFragment newInstance(Context context,Postcard postcard,Theme theme){
	PostCardFragment pcf=new PostCardFragment();
	PostCardFragment.card=postcard;
	PostCardFragment.context=context;
	PostCardFragment.theme=theme;
	return pcf;
}
	
//Обработчик событий нажатия на открытки
public interface onPostCardListener {
	    public void onPostCardTouchEvent();
//	    public void onAppealClickEvent();
	    public void onStartSignatureActivityEvent();
	    public void onCloseEditorModeEvent();
	  }
	
	onPostCardListener PostCardListener;
	  
public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	        	PostCardListener = (onPostCardListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement PostCardListener");
	        }
	  }

public void setTextPostCard(String text){}

//public int getStateAppeal(){
//	return card.getStateAppeal();
//}
//
//public int getStateSignature(){
//	return card.getStateSignature();
//}


public void setStateSignature(int state){
}

public void setAppeal(Appeal appeal){}

public void savePostcard(){}

public void setStateAppeal(int state){
}

int textFieldLines(){
//	if (card.getStateAppeal()==Constants.OPEN
//			&& card.getStateSignature()==Constants.OPEN) return 4;
//	if (card.getStateAppeal()==Constants.CLOSE
//			&& card.getStateSignature()==Constants.CLOSE) return 8;
//	else 
	return 8;
}

public void onEditableMode(){}

public String getText(){
	return card.getText();
}
public void ZoomText(){
}
public boolean getEditableMode(){
	return false;
};

public void hideSoftInput(){}
}
