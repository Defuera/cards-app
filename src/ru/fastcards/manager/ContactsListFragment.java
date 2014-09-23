package ru.fastcards.manager;


import ru.fastcards.R;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.TableCursorLoader;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnQueryTextListener{

	
	   private static final String TAG = "ContactsListFragment";
	private Context context;
	private boolean mIsSearchResultView;
	private String mSearchTerm;
	private ListView listView;
	
	private SimpleItemCursorAdapter mAdapter;
    private boolean mSearchQueryChanged;
	private View contentView;
//	private OnContactsInteractionListener mOnContactSelectedListener;
    // Bundle key for saving previously selected search result item
//    private static final String STATE_PREVIOUSLY_SELECTED_KEY =    "com.example.postcards.SELECTED_ITEM";

	
    /**
     * Fragments require an empty constructor.
     */
    public ContactsListFragment() {}
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);
        mAdapter = new SimpleItemCursorAdapter(context);

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	contentView = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        return contentView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = (ListView) contentView.findViewById(R.id.listview);
//        setListAdapter(mAdapter);
        listView.setOnItemClickListener(itemClickListener);
        listView.setAdapter(mAdapter);


//        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        getLoaderManager().initLoader(0, null, this);
    }
    

	private OnItemClickListener itemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startManageContactActivity(mAdapter.getItemUuid(position));
		}	
	};
	private String searchTerm;
	private MenuItem searchMenuItem;
    
	private void startManageContactActivity(String itemUuid) {
		Intent intent = new Intent(getActivity(), ManageContactActivity.class);
		intent.putExtra(Constants.EXTRA_ID, itemUuid);
		startActivityForResult(intent, Constants.REQUEST_MODIFY_CONTACT);
	}	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String where  = null;
		if (searchTerm != null && !searchTerm.equals("")){
			searchTerm = searchTerm.toUpperCase();
			where = DataBaseHelper.FIELD_NAME_SEARCH + " LIKE "+"'%" + searchTerm + "%'";
		}
    	
        return new TableCursorLoader(getActivity(), Constants.TABLE_CONTACTS, where, "Name" + " ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    	if (data != null)
            mAdapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.search, menu);
	    
		searchMenuItem = menu.findItem(R.id.action_search);	      
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);	     
		searchView.setOnQueryTextListener(this);
	    
	    super.onCreateOptionsMenu(menu,inflater);
	}
	

	@Override
	public boolean onQueryTextChange(String newText) {
		searchTerm = newText;
		Log.i(TAG, "searchTetm "+searchTerm);
		
      getLoaderManager().restartLoader(0, null, this);
		return false;
	}

	
	@Override
	public boolean onQueryTextSubmit(String arg0) {
	     if (searchMenuItem != null) {
         }
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
//			proceedMenuItem.setVisible(true);
			switch (requestCode) {
			case Constants.REQUEST_MODIFY_CONTACT: {
				getLoaderManager().restartLoader(0, null, this);

			}

				break;
			default:
				break;
			}
		}
	}

}
