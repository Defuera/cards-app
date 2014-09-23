package ru.fastcards.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ru.fastcards.R;
import ru.fastcards.TrackedActivity;
import ru.fastcards.common.Comunication;
import ru.fastcards.common.Recipient;
import ru.fastcards.recipientselectors.OnContactInfoClickListener;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.TableCursorLoader;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ManageContactActivity extends TrackedActivity implements	LoaderManager.LoaderCallbacks<Cursor>, OnQueryTextListener {

	private static final String TAG = "ManageContactFragment";
	private View contentView;
	private ListView listView;

	private ChooserItemsCursorAdapter mAdapter;
	private Context context;
	private Recipient recipient;

	private MenuItem joinMenuAction;
	private Cursor cursor;
	private Set<String> selectedRecipientsIds = new HashSet<String>();
	private TextView genderTv;
	private EditText nickEt;
	private String previousNick;

	private DataBaseHelper dbHelper;
	private List<Comunication> comList;
	private LinearLayout infoLayout;
	private ImageView contactInfoIv;
	private TextView contactInfoTv;
	
	private boolean mSearchQueryChanged;
	private MenuItem searchMenuItem;
	private String searchTerm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.fragment_manage_contact, null,false);
		setContentView(contentView);
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;
		
		dbHelper = DataBaseHelper.getInstance(context);

		String recioientId = getIntent().getStringExtra(Constants.EXTRA_ID);
		recipient = DataBaseHelper.getInstance(this).getRecipient(recioientId);
		initializeUi();

		mAdapter = new ChooserItemsCursorAdapter(context);

		setListView();

	}

	private void setListView() {
		listView = (ListView) contentView.findViewById(R.id.list_view);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(itemClickListener);
		listView.setAdapter(mAdapter);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				nickEt.clearFocus();
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

//		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportLoaderManager().initLoader(0, null, this);

	}

	private void initializeUi() {

		ImageView recImage = (ImageView) contentView.findViewById(R.id.iv_recipient_image);
		BitmapLoaderAsyncTask loader = new BitmapLoaderAsyncTask(context, null,	true, true);
		loader.loadImageAsync(recipient.getImageUri(), recImage, null);

		LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.ll_container);
		ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nickEt.clearFocus();
				hideKeybord();
			}
		});

		nickEt = (EditText) contentView	.findViewById(R.id.et_recipient_name_modified);
		nickEt.setText(recipient.getNickName() != null ? recipient
				.getNickName() : recipient.getName());
		nickEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d(TAG, "edittext has focus " + hasFocus);
				saveUserNickName(nickEt.getText().toString());
			}
		});
		nickEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE){
					nickEt.clearFocus();
//					saveUserNickName(nickEt.getText().toString());
					hideKeybord();
				}
				return false;
			}
		});
		
		TextView nameTv = (TextView) contentView.findViewById(R.id.tv_recipient_name);
		nameTv.setText(recipient.getName());

		genderTv = (TextView) contentView.findViewById(R.id.tv_recipient_gender);
		setGenderTv();

		comList = dbHelper.getComunicationsList(recipient.getId(), null);
		Comunication primary = comList.get(0);
		for (Comunication com : comList)
			if (com.isPrimaty())
				primary = com;

		contactInfoIv = (ImageView) contentView	.findViewById(R.id.iv_contact_icon);
		contactInfoIv.setImageBitmap(getContactInfoBitmap(primary.getType()));

		contactInfoTv = (TextView) contentView.findViewById(R.id.tv_recipient_name);
		contactInfoTv.setText(recipient.getName());// getContactInfoText(primary,
													// item.getName()));

		infoLayout = (LinearLayout) contentView	.findViewById(R.id.ll_contact_info);
		infoLayout.setOnClickListener(new OnContactInfoClickListener(context, recipient, comList, contactInfoIv, contactInfoTv));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			// nickEt.
			Toast.makeText(context, "keyboard visible", Toast.LENGTH_SHORT)
					.show();
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			Toast.makeText(context, "keyboard hidden", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void saveUserNickName(String newNick) {
		if (!newNick.equals(previousNick) && newNick != "") {
			previousNick = newNick;
			recipient.setNickName(newNick);
			recipient.changeNickName(context, newNick);
		}
	}

	private void setGenderTv() {
		Resources res = getResources();
		genderTv.setText(recipient.getGender() == 1 ? getString(R.string.str_male)
				: getString(R.string.str_female));
		genderTv.setBackgroundColor(recipient.getGender() == 0 ? res
				.getColor(R.color.bg_text_gender_female) : res
				.getColor(R.color.bg_text_gender_male));

		genderTv.setOnClickListener(onGendeClickListener(recipient));

	}

	//The same as in RecipientSelectorActivity
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			cursor.moveToPosition(position);
			String uuid = cursor.getString(cursor.getColumnIndex("_id"));
			long longId = UUID.fromString(uuid).getMostSignificantBits();
			
			if (nickEt.hasFocus()) {
				nickEt.clearFocus();
				hideKeybord();
			} else {
				if (mAdapter.isItemSelected(longId)) {
					mAdapter.unselectItem(longId);
					selectedRecipientsIds.remove(uuid);
				} else {
					mAdapter.selectItem(longId);
					selectedRecipientsIds.add(uuid);
				}

				if (selectedRecipientsIds.isEmpty())
					joinMenuAction.setVisible(false);
				else
					joinMenuAction.setVisible(true);

				mAdapter.notifyDataSetChanged();
			}
		}
	};

	private void hideKeybord() {
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(contentView.getWindowToken(), 0);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String where  = DataBaseHelper.FIELD_UUID + " != " + "'" + recipient.getId() + "'";
		if (searchTerm != null && !searchTerm.equals("")){
			searchTerm = searchTerm.toUpperCase();
			where += " AND "+DataBaseHelper.FIELD_NAME_SEARCH + " LIKE "+"'%" + searchTerm + "%'";
		}
		
		Log.d(TAG, "onCreateLoader where = "+where);
		return new TableCursorLoader(this, Constants.TABLE_CONTACTS, where,	"Name" + " ASC"); //"Name" + " ASC"
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null)
			cursor = data;
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private void joinContacts() {
		for (String id : selectedRecipientsIds) {
			rewriteComunication(id);
		}
		mAdapter.clearSelected();
		selectedRecipientsIds.clear();
		comList = dbHelper.getComunicationsList(recipient.getId(), null);
		infoLayout.setOnClickListener(new OnContactInfoClickListener(context,
				recipient, comList, contactInfoIv, contactInfoTv));
		getSupportLoaderManager().restartLoader(0, null, this);

	}

	private void rewriteComunication(String id) {
		dbHelper.rewriteComunications(id, recipient.getId());
		dbHelper.deleteRecipient(id);
	}

	private Bitmap getContactInfoBitmap(String comType) {
		System.out.println("getContactInfoBitmap " + comType);
		Bitmap bmp = null;
		if (comType.equals(Constants.COMUNICATION_TYPE_VK)) {
			return BitmapFactory.decodeResource(context.getResources(),
					R.drawable.btn_vk_medium);
		}
		if (comType.equals(Constants.COMUNICATION_TYPE_PHONE)) {
			return BitmapFactory.decodeResource(context.getResources(),
					R.drawable.btn_tel_book_medium);

		}
		if (comType.equals(Constants.COMUNICATION_TYPE_EMAIL)) {
			return BitmapFactory.decodeResource(context.getResources(),
					R.drawable.btn_email_medium);

		}
		if (comType.equals(Constants.COMUNICATION_TYPE_FB)) {
			return BitmapFactory.decodeResource(context.getResources(),
					R.drawable.btn_fb_medium);

		}
//		if (comType.equals(Constants.COMUNICATION_TYPE_CONTACTS_ID)) {
//			return BitmapFactory.decodeResource(context.getResources(),
//					R.drawable.;
//
//		}

		return bmp;
	}

	private OnClickListener onGendeClickListener(final Recipient item) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				item.changeGender(context);
				System.out.println("onGenderClickListener");
				setGenderTv();
			}

		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_join, menu);
		joinMenuAction = menu.getItem(1);
		// doneMenuAction = menu.getItem(2);		
	       
		searchMenuItem = menu.findItem(R.id.action_search_menu);	      
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);	     
		searchView.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_join: {
			joinContacts();
			setResult(RESULT_OK);
			return true;
		}
	    case android.R.id.home:
	    	finish();
	        return true;

		default: {
		}
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		searchTerm = newText;
		Log.i(TAG, "searchTetm "+searchTerm);
		
      getSupportLoaderManager().restartLoader(0, null, this);
		return false;
	}

	
	@Override
	public boolean onQueryTextSubmit(String arg0) {
	     if (searchMenuItem != null) {
         }
		return false;
	}

}
