package ru.fastcards;

import ru.fastcards.shop.StarsShopActivity;
import ru.fastcards.utils.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

public class PurseDialogFragment extends DialogFragment{

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Account account=Account.getInstance();
		
		AlertDialog.Builder builder=new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light));
		builder.setTitle(null);
		
		View contentView=getActivity().getLayoutInflater().inflate(R.layout.my_purse_dialog_content, null);
		TextView balance=(TextView) contentView.findViewById(R.id.balance);
		
		balance.setText(""+(int)account.getWealth());
		builder.setView(contentView);
		
		TextView credit=(TextView) contentView.findViewById(R.id.tv_credit);
		String star=starString((int)account.getWealth());
		credit.setText(star);
		
		builder.setPositiveButton(getResources().getString(R.string.str_yes), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
//				Intent intent=new Intent(getActivity(), PurseReplenishmentActivity.class);
				Intent intent=new Intent(getActivity(), StarsShopActivity.class);
				startActivity(intent);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.str_no), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});		
		return builder.create();
	}
	
	//Create string star
	private String starString(int wealth){
		String money=Integer.toString(wealth);
		int lastSymbol=Integer.parseInt(""+money.charAt(money.length()-1));
		if (money.length()==1){
			if (lastSymbol==1) return getString(R.string.str_star_one);
		}
		else {
			if (lastSymbol==1) return getString(R.string.str_star_end_one);
			if (lastSymbol==2||lastSymbol==3||lastSymbol==4) return getString(R.string.str_star_end_two);
			else return getString(R.string.str_star_end_five);
		}
		return getString(R.string.str_star_end_five);
	} 
}
