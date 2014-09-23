package ru.fastcards.recipientselectors;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.R;
import ru.fastcards.ListContacts;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseGroupActivity extends ActionBarActivity {

	private static final String TAG = "ContactJoinFragment";
	private String groupId;

	private Context context;
//	private RecipientsGroup groupRec;
	private EditText nameEditText;
	private String previousNick;

	private DataBaseHelper dbHelper;
	private TextView countTv;
	protected List<ListContacts> groupsList = new ArrayList<ListContacts>();
	private ListView listView;
	private SimpleItemMultiChooserAdapter adapter;
	private List<ListContacts> selectedItemsList = new ArrayList<ListContacts>();;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		context = this;		
		groupId = getIntent().getStringExtra(Constants.EXTRA_ID);
		
		
		dbHelper = DataBaseHelper.getInstance(context);

		getRecipientGroups();
				
		listView = (ListView) findViewById(R.id.lv_recipients);
		listView.setOnItemClickListener(itemClickListener);
		adapter = new SimpleItemMultiChooserAdapter(context, groupsList);
		listView.setAdapter(adapter);
		
//		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

	}

	private void getRecipientGroups() {
		groupsList = dbHelper.getGroupsList();
	}

	private boolean returnItems() {
		Intent intent = new Intent();

//		Utils.setRecipientsExtra(selectedItemsList = new ArrayList<RecipientsGroup>();, intent);

		setResult(RESULT_OK, intent);
		finish();
		return false;
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ListContacts group = groupsList.get(position);
			group.setSelected(!group.isSelected());
			if (group.isSelected())
				selectedItemsList.add(group);
			else 
				selectedItemsList.remove(group);
			
			adapter.notifyDataSetChanged();
		}	
	};

	private void returnGroups() {
		String[] idsArray = new String[selectedItemsList.size()];
		for (int i = 0; i < selectedItemsList.size(); i++)
			idsArray[i] = selectedItemsList.get(i).getId();
		
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_RECIPIENTS_GROUP, idsArray);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.done, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done: {
			returnGroups();
//			String groupName = nameEditText.getText().toString();
//			System.out.println("groupName "+groupName+" "+groupName.length());
//			if (groupName.length() < 1)
//				Toast.makeText(context, getString(R.string.str_error_enter_name), Toast.LENGTH_SHORT).show();
//			else{
////				saveGroupName(groupName);
////				safeGroup();
//				setResult(RESULT_OK);
//				finish();
//			}
			return true;
		}

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}