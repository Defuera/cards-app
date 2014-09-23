package ru.fastcards.editor;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Appeal;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class AppealFragment extends Fragment{
	
	private ListView titles_list;
	private List<Appeal> listAppeal;
	private TitlesAdapter titles_Adapter;
	
	public AppealFragment() {
		// TODO Auto-generated constructor stub
	}

	private OnItemClickListener onAppealItemClick;
	  
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.titles_fragment, null);
		titles_list=(ListView) v.findViewById(R.id.titles_list);

		titles_list.setOnItemClickListener(onAppealItemClick);
		
		return v;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (listAppeal!=null && !listAppeal.isEmpty()){
			titles_Adapter=new TitlesAdapter(getActivity(), listAppeal);
			titles_list.setAdapter(titles_Adapter);
			}
	}

	public void setAppealList(List<Appeal> listAppeal){
		this.listAppeal=listAppeal;
		}
		
	public void setOnItemAppealClick(OnItemClickListener listener){
		onAppealItemClick=listener;
	}
	

}
