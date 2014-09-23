package ru.fastcards.manager;


import ru.fastcards.R;
import ru.fastcards.SwipeDismissListViewTouchListener;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.TableCursorLoader;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactGroupsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final String TAG = "ContactsListFragment";
	private Context context;
	private String mSearchTerm;
	private SimpleItemCursorAdapter mAdapter;
	private ListView listView;

    public ContactGroupsFragment() {}
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);
        mAdapter = new SimpleItemCursorAdapter(context);
    }
    
	private OnItemClickListener itemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startManageGroupActivity(mAdapter.getItemUuid(position));
		}		
	};
	private Cursor cursor;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
    	View contentView = inflater.inflate(R.layout.activity_listview, container, false);
    	listView = (ListView) contentView.findViewById(R.id.lv_recipients);    	
//    	emptyTv = (TextView) 
        return contentView; 
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);     
        setListView();
        
        ((ActionBarActivity) getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getLoaderManager().initLoader(0, null, this);
    }
    
	private void setListView() {
		listView.setOnItemClickListener(itemClickListener);
		listView.setAdapter(mAdapter);
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true; 
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							cursor.moveToPosition(position);
							
							removeGroup(cursor.getString(0));
						}
					}
				});
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling, we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
	}

	private void removeGroup(String groupId) {
		DataBaseHelper.getInstance(context).deleteContactsGroup(groupId);
		getLoaderManager().restartLoader(0, null, this);
	}
	
	private void startManageGroupActivity(String groupId) {
		Intent intent = new Intent(getActivity(), ManageGroupActivity.class);
		intent.putExtra(Constants.EXTRA_ID, groupId);
		startActivityForResult(intent, Constants.REQUEST_MODIFY_GROUP);
	}
    
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new TableCursorLoader(getActivity(), Constants.TABLE_LISTS, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		cursor = data;
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.add, menu);
	    super.onCreateOptionsMenu(menu,inflater);
	}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_add: {
				startManageGroupActivity(null);
				return true;
			}
			default:{
			}
				return super.onOptionsItemSelected(item);
			}
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == getActivity().RESULT_OK) {
				switch (requestCode) {
				case Constants.REQUEST_MODIFY_GROUP: {
					getLoaderManager().restartLoader(0, null, this);
					
				}
					break;
				default:
					break;
				}
			}
		}
}
