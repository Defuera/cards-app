package ru.fastcards.manager;

import ru.fastcards.R;
import ru.fastcards.TrackedActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;

//implements ContactsListFragment.OnContactsInteractionListener

public class ContactsManagerActivity extends TrackedActivity {
	protected static final String TAG_CONTACTS = "FragmentContacts";
	protected static final String TAG_GROUPS = "FragmentGroups";
	private static final String TAG_MANAGE_GROUP = "FragmentManageGroup";
	private static final String TAG = "ContactsManagerActivity";
	// extends FragmentActivity implements
	// ContactsListFragment.OnContactsInteractionListener
	private String searchTerm;
	private boolean searchQueryChanged;
	private ContactsListFragment contactsFragment;

	private ContactGroupsFragment groupsFragment;
	private ActionBar actionBar;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_contacts_manager);
		contactsFragment = new ContactsListFragment();		
		groupsFragment = new ContactGroupsFragment();
		setActionBar();

	}

	private void setActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setTitle(getResources().getString(R.string.str_title_contacts_manager));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab contactsTab = actionBar.newTab();
		contactsTab.setText(getString(R.string.str_contacts));
		contactsTab.setTabListener(contactsTabListener);
		actionBar.addTab(contactsTab);

		Tab groupsTab = actionBar.newTab();
		groupsTab.setText(getString(R.string.str_lists));
		groupsTab.setTabListener(groupsTabListener);
		actionBar.addTab(groupsTab);
	}

	private TabListener contactsTabListener = new TabListener() {

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
				ft = getSupportFragmentManager().beginTransaction();
				
				ft.replace(R.id.ll_contacts_fragment_container,	contactsFragment, TAG_CONTACTS);
				ft.commit();
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

	};

	private TabListener groupsTabListener = new TabListener() {

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft = getSupportFragmentManager().beginTransaction();

			ft.replace(R.id.ll_contacts_fragment_container, groupsFragment, TAG_GROUPS);
			ft.commit();
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}
	};
}
