package ru.fastcards.recipientselectors;

import ru.fastcards.R;
import ru.fastcards.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class RecipientSelectorActivity extends SendYourselfActivity {
	private Context context;

	private String TAG = "RecipientsSelectorActivity";
	String text;
	String sigText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_recipient_select);
		
		loadRecipients(getIntent());
		listView = (ListView) findViewById(R.id.lv_recipients);
		listView.addHeaderView(getHeaderView());
		
		listView.setOnItemClickListener(itemClickListener);
				
		setListAdapter(new RecipientsSelectorAdapter(this, itemsList));	
	}
		
	private void loadRecipients(Intent data) {
		itemsList = Utils.getIIsGroupDataExtra(context, data);//RecipientsExtra(data, -1);
		}
	
	private boolean returnRecipients() {
		Intent intent = new Intent();

		Utils.setIIsGroupDataExtra(itemsList, intent);

		setResult(RESULT_OK, intent);
		finish();
		return false;
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.recipients_selector, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_return_recipients: {
			returnRecipients();
		}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	
}