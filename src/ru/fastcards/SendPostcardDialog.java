package ru.fastcards;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SendPostcardDialog extends DialogFragment {

	private static int cardsCount;
	private static SendPostcardDialog instance;
	
	public static SendPostcardDialog getInstanse(int cardsCount){
		SendPostcardDialog.cardsCount = cardsCount;
		if (instance == null){
			instance = new SendPostcardDialog();
		}
		return instance;
	}
	
	private View contentView;
	private Button sentButton;
	private Button saveButton;
	private Button sentLaterButton;
	private OnClickListener sendButtoClickListener;
	private OnClickListener sendLaterClickListener;
	private OnClickListener saveButtonClickListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_send_postcards, container, false);

		sentButton = (Button) contentView.findViewById(R.id.btn_send);
		sentButton.setOnClickListener(sendButtoClickListener);
		
		saveButton = (Button) contentView.findViewById(R.id.btn_save);
		saveButton.setOnClickListener(saveButtonClickListener);
		
//		sentLaterButton = (Button) contentView.findViewById(R.id.btn_send_later);
//		sentLaterButton.setOnClickListener(sendLaterClickListener);
		
		TextView countTv = (TextView) contentView.findViewById(R.id.tv_count);
		countTv.setText(Integer.toString(cardsCount));
		return contentView;
	}

	public void setOnSendButtonClickListener(OnClickListener onClickListener) {
		sendButtoClickListener = onClickListener;
	}

//	public void setOnSendLaterClickListener(OnClickListener onClickListener) {
//		sendLaterClickListener = onClickListener;
//		
//	}

	public void setOnSaveButtonClickListener(OnClickListener onClickListener) {
		saveButtonClickListener = onClickListener;
		
	}
}
