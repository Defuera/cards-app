package ru.fastcards.manager;

import java.util.List;
import java.util.UUID;

import ru.fastcards.R;
import ru.fastcards.ListContacts;
import ru.fastcards.SimpleItemAdapter;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.common.Recipient;
import ru.fastcards.recipientselectors.AddContactsFragment;
import ru.fastcards.recipientselectors.SelectorActivity;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ManageGroupActivity extends SelectorActivity {

	private static final String TAG = "ContactJoinFragment";
	private String groupId;

	private Context context;
	private ListContacts groupRec;
	private EditText nameEditText;
	private String previousNick;

	private DataBaseHelper dbHelper;
	private TextView countTv;
//	private String flag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_group);
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
		
		listView.setOnItemClickListener(itemClickListener);
		
		setListAdapter(new SimpleItemAdapter(context, itemsList));	
	}

	private void getExtras() {
		groupId = getIntent().getStringExtra(Constants.EXTRA_ID);
		
	}

	private void getRecipients() {
		itemsList = (List<ISendableItem>) (List<?>) dbHelper.getRecipientsListByGroupId(groupId);
	}

	private void initializeUi() {
		FragmentManager mgr = getSupportFragmentManager();
		Fragment addContactsFragment = mgr.findFragmentById(R.id.fr_add_contacts);
		
		((AddContactsFragment) addContactsFragment).hideGroups();

		nameEditText = (EditText) findViewById(R.id.et_title);
		if (groupRec != null)
			nameEditText.setText(groupRec.getName());

		nameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d(TAG, "edittext has focus " + hasFocus);
				saveGroupName(nameEditText.getText().toString());
			}
		});
		nameEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE){
					nameEditText.clearFocus();
//					saveGroupName(nameEditText.getText().toString());
					hideKeybord(nameEditText);
				}
				return false;
			}
		});
		nameEditText.clearFocus();
		
		countTv = (TextView) findViewById(R.id.tv_contacts_count);
		setCountTv();
	}
	
	private void hideKeybord(View view) {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@Override
	public void onItemRemove() {
		setCountTv();		
	}

	private void setCountTv() {
		int size = 0;
		if (itemsList != null)
			size = itemsList.size();
//		if (groupRec != null)
//			groupRec.setSize(size);
		countTv.setText(size + " "	+ context.getString(R.string.str_users));

	}

	private void saveGroupName(String newName) {
		if (!newName.equals(previousNick) && newName != "" && groupRec != null) {
			previousNick = newName;
			groupRec.changeName(context, newName);
		}
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startManageContactActivity(((Recipient)itemsList.get(position)).getId());
		}	
	};
	
	private void startManageContactActivity(String itemUuid) {
		Intent intent = new Intent(this, ManageContactActivity.class);
		intent.putExtra(Constants.EXTRA_ID, itemUuid);
		startActivityForResult(intent, Constants.REQUEST_MODIFY_CONTACT);
	}

	private void safeGroup() {
		if (groupId == null) {
			groupId = UUID.randomUUID().toString();
			dbHelper.createNewGroupRecipients(groupId, nameEditText.getText().toString(), itemsList.size());
		} else {
			dbHelper.updateContactsGroup(groupId, nameEditText.getText().toString(), itemsList.size());
		}

		dbHelper.clearGroup(groupId);
		
		for (ISendableItem rec : itemsList) {
			dbHelper.addRecipientToGroup(UUID.randomUUID().toString(), groupId,	rec.getId());
		}
	}

	private void showDeleteGroupDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Delete group?");
		builder.setMessage("Are you shure you want to delete this group?");
		builder.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteGroup();
			}
		});
		builder.create().show();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.done, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done: {
			String groupName = nameEditText.getText().toString();
			System.out.println("groupName "+groupName+" "+groupName.length());
			if (groupName.length() < 1)
				Toast.makeText(context, getString(R.string.str_error_enter_gr_name), Toast.LENGTH_SHORT).show();
			else{
				saveGroupName(groupName);
				safeGroup();
				setResult(RESULT_OK);
				finish();
			}
			return true;
		}
		case R.id.action_delete: {
			showDeleteGroupDialog();
			return true;
		}
	    case android.R.id.home:
	    	finish();
	        return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
}