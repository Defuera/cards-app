package ru.fastcards.manager;

import java.util.ArrayList;
import java.util.List;

import ru.fastcards.R;
import ru.fastcards.SimpleItemAdapter;
import ru.fastcards.SwipeDismissListViewTouchListener;
import ru.fastcards.TrackedActivity;
import ru.fastcards.common.SimpleItem;
import ru.fastcards.editor.EditorActivity;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ProjectsManagerActivity extends TrackedActivity {
	protected static final String TAG = "ListSelectorActivity";
	private List<String[]> categories = new ArrayList<String[]>();
	// private String[] category;
	private Context context;
	// private String[] arrayIds;
	private List<SimpleItem> projectsList;
	private SimpleItemAdapter adapter;
	private ProgressDialog dialog;
	private ListView listView;

	DataBaseHelper dbHelper;// = DataBaseHelper.getInstance(context);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		context = this;
		
		dbHelper = DataBaseHelper.getInstance(context);
		setListView();
		Log.d(TAG, "ListSelectorActivity CREATED");

		getItems(getIntent().getStringExtra(Constants.EXTRA_TYPE));

	}

	private void setListView() {
		listView = (ListView) findViewById(R.id.lv_recipients);
		listView.setOnItemClickListener(onItemClickListener);
		
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;//position != (listView.getHeaderViewsCount() - 1);
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							onItemRemove(position);
							adapter.remove(adapter.getItem(position));//adapter.getItem(position - listView.getHeaderViewsCount()));
							
						}
						if (projectsList.isEmpty()) {
							adapter.notifyDataSetChanged();
						}
					}

				});
		listView.setOnTouchListener(touchListener);
		listView.setOnScrollListener(touchListener.makeScrollListener());
	}

	private void onItemRemove(int position) {
		dbHelper.deleteProject(projectsList.get(position).getId());
	}
	
	private void getItems(String type) {
		projectsList = (List<SimpleItem>) (List<?>) dbHelper.getProjectsList();

		adapter = new SimpleItemAdapter(context, projectsList);
		if (projectsList.isEmpty())
			finishActivity();
		else
			listView.setAdapter(adapter);
	}

	private void finishActivity() {
		finish();
		Toast.makeText(context, this.getResources().getString(R.string.str_empty_projects_error), Toast.LENGTH_SHORT).show();
		
	}

	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(context, EditorActivity.class);
			intent.putExtra(Constants.EXTRA_PROJECT_ID, projectsList.get(position).getId());		
			startActivity(intent);
		}
	};
	
}
