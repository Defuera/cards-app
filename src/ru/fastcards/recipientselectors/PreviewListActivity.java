package ru.fastcards.recipientselectors;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.ListContacts;
import ru.fastcards.R;
import ru.fastcards.SimpleItemAdapter;
import ru.fastcards.SwipeDismissListViewTouchListener;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.common.SimpleItem;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

	public class PreviewListActivity extends SelectorActivity {

		private static final String TAG = "ContactJoinFragment";
		private String groupId;

		private Context context;
		private ListContacts groupRec;
//		private EditText nameEditText;
//		private String previousNick;

		private DataBaseHelper dbHelper;
		private TextView countTv;
//		private String flag;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_preview_list_contacts);
			context = this;		
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);	
				
			getExtras();		
			
			dbHelper = DataBaseHelper.getInstance(context);
			if (groupId != null) {
				groupRec = dbHelper.getContactsList(groupId);
			getRecipients();
			}
			
			initializeUi();
			
			listView = (ListView) findViewById(R.id.list_view);
			
			setAdapter(new SimpleItemAdapter(context, itemsList));	
		}
		
		private void setAdapter(ArrayAdapter<SimpleItem> mAdapter) {
			if (itemsList == null){
				itemsList = new ArrayList();
			}
			this.adapter = mAdapter;

			listView.setAdapter(adapter);		
		}

		private void getExtras() {
			groupId = getIntent().getStringExtra(Constants.EXTRA_ID);
//			flag = getIntent().getStringExtra(Constants.EXTRA_FLAG);
			
		}

		private void getRecipients() {
			itemsList = (List<ISendableItem>) (List<?>) dbHelper.getRecipientsListByGroupId(groupId);
		}

		private void initializeUi() {
			TextView nameTv = (TextView) findViewById(R.id.tv_title);
			if (groupRec != null)
				nameTv.setText(groupRec.getName());

			countTv = (TextView) findViewById(R.id.tv_contacts_count);
			setCountTv();
		}


		private void setCountTv() {
			int size = 0;
			if (itemsList != null)
				size = itemsList.size();
			countTv.setText(size + " "	+ context.getString(R.string.str_users));

		}

		private void deleteGroup() {
			Toast.makeText(context, "sgfjasdjfasdj", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void addRecipients(Intent data, int origin) {
			super.addRecipients(data, origin);
			setCountTv();
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {

		    case android.R.id.home:
		    	finish();
		        return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public void onItemRemove() {}
		
	}