package ru.fastcards.shop;

import ru.fastcards.PurseDialogFragment;
import ru.fastcards.R;
import ru.fastcards.utils.Account;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WealthView extends LinearLayout {

	protected static final String TAG = "WealthView";
	private static TextView myWealth;
	private FragmentActivity context;
	private static WealthView instanse;

	public static WealthView getInstanse(FragmentActivity context) {
		if (instanse==null) 
			instanse=new WealthView(context);
		instanse.context=context;
		return instanse;
	}

	private WealthView(FragmentActivity context) {
		super(context);
		Log.d("CurrensyView", "CurrensyView called");
		inflate(context, R.layout.wealth_view, this);
		this.context = context;
		initializeWealthView();
	}

	private void initializeWealthView() {
		myWealth = (TextView) findViewById(R.id.tv_money);
		refreshWealth();
		
		this.setOnClickListener(onWealthViewClickListener);
	}

	public void refreshWealth() {
		myWealth.setText(Integer.toString((int) Account.getInstance().getWealth()));
	}


	private OnClickListener onWealthViewClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			Log.d(TAG, "click action wealth");
			//change to custom dialog
			new PurseDialogFragment().show(context.getSupportFragmentManager(), TAG);
			
		}
	};	
}
