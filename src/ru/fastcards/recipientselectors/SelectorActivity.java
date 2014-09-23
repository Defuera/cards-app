package ru.fastcards.recipientselectors;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.SwipeDismissListViewTouchListener;
import ru.fastcards.TrackedActivity;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sromku.simple.fb.SimpleFacebook;

public abstract class SelectorActivity extends TrackedActivity{
	
	private static final String TAG = "SelectorActivity";
	private Context context;
	protected List<ISendableItem> itemsList = new ArrayList<ISendableItem>();
	protected ListView listView;
	protected ArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;	
	}
	
	protected void setListAdapter(ArrayAdapter mAdapter) {
		if (itemsList == null){
			itemsList = new ArrayList();
		}
		this.adapter = mAdapter;

		listView.setAdapter(adapter);		
		
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return position != (listView.getHeaderViewsCount() - 1);
					}

					@Override
					public void onDismiss(ListView listView, int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							adapter.remove(adapter.getItem(position - listView.getHeaderViewsCount()));
							onItemRemove();
						}
						if (itemsList.isEmpty()) {
							adapter.notifyDataSetChanged();
						}
					}
				});
		listView.setOnTouchListener(touchListener);
		listView.setOnScrollListener(touchListener.makeScrollListener());
	}
	
	public abstract void onItemRemove();
	
	protected void refreshAdapter() {
		adapter.notifyDataSetChanged();		
	}	
		
	void startSocialSelectorActivity(int requestCode) {
		Intent intent = new Intent(context,	SeceltMultipleContactsActivity.class);
		switch (requestCode) {
		case Constants.REQUEST_VK_RECIPIENTS:
			intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE,	Constants.COMUNICATION_TYPE_VK);
			break;
		case Constants.REQUEST_FB_RECIPIENTS:
			intent.putExtra(Constants.EXTRA_COMUNICATION_TYPE,	Constants.COMUNICATION_TYPE_FB);			
			break;
		default:
			break;
		}		
		startActivityForResult(intent, requestCode);
	}
	
	public void addRecipients(Intent data, int origin) {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		String [] idsArray = data.getStringArrayExtra(Constants.EXTRA_RECIPIENTS_IDS);
		for (String id : idsArray)
			itemsList.add(dbHelper.getRecipient(id));
//		itemsList.addAll(Utils.getRecipientsExtra(data, origin));
		adapter.notifyDataSetChanged();
	}
	
	private void addGroups(Intent data) {
		DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		String[] idsArray = data.getStringArrayExtra(Constants.EXTRA_RECIPIENTS_GROUP);
		for (String id : idsArray){
			itemsList.add(dbHelper.getContactsList(id));
		}
		adapter.notifyDataSetChanged();
	}	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    SimpleFacebook.getInstance().onActivityResult(this, requestCode, resultCode, data); 
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Constants.REQUEST_VK_AUTH: {
				Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						startSocialSelectorActivity(Constants.REQUEST_VK_RECIPIENTS);
						super.handleMessage(msg);
					}
				};
				Utils.importFriendsToDataBase(context, Constants.COMUNICATION_TYPE_VK, handler);
			}
				break;
			case Constants.REQUEST_VK_RECIPIENTS: {
				addRecipients(data, Constants.ORIGIN_VK);
			}
				break;
			case Constants.REQUEST_CONTACTS: {
				addRecipients(data, Constants.ORIGIN_CONTACTS);
			}
				break;
			case Constants.REQUEST_FB_RECIPIENTS: {
				addRecipients(data, Constants.ORIGIN_FB);
			}
				break;
			case Constants.REQUEST_RECIPIENTS_GROUP: {
				addGroups(data);
			}
				break;
			default:
				break;
			}
		}
	}




}
