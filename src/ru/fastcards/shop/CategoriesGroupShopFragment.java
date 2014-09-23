//package ru.fastcards.shop;
//
//import java.util.List;
//
//import ru.fastcards.OnCategoriesClickListener;
//import ru.fastcards.R;
//import ru.fastcards.SimpleItemAdapter;
//import ru.fastcards.common.SimpleItem;
//import ru.fastcards.utils.Constants;
//import ru.fastcards.utils.DataBaseHelper;
//import ru.fastcards.utils.Utils;
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//
//
//public class CategoriesGroupShopFragment extends Fragment{
//
//
//	private List<?> itemList;
//	private ListView list;
//	private SimpleItemAdapter adapter;
//	private Context context;
//		
//	OnCategoriesClickListener categoryGroupClickListener;
//	private ProgressDialog dialog;
//  
//	public void onAttach(Activity activity) {
//		    super.onAttach(activity);
//		        try {
//		        	categoryGroupClickListener = (OnCategoriesClickListener) activity;
//		        } catch (ClassCastException e) {
//		            throw new ClassCastException(activity.toString() + " must implement onCategoryGroupClickListener");
//		        }
//		  }
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		context=getActivity();
//		View v=inflater.inflate(R.layout.categories_shop_fragment, null);
//		list=(ListView) v.findViewById(R.id.list_categories);
//		list.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int position,
//					long id) {
//				// TODO Auto-generated method stub
//				SimpleItem item=(SimpleItem)itemList.get(position);
//				categoryGroupClickListener.onCategoryClick(item.getName(), item.getId());
//			}
//		});
//		createList();
//		return v;
//	}
//
//	private void createList(){
//			final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
//			itemList = dbHelper.getCategoryGroupsList();
//			if (itemList.isEmpty()){
//				dialog = new ProgressDialog(context);
//				dialog.setMessage(getString(R.string.loading_postcards));
//				dialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
//					
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						getActivity().finish();
//					}
//				});
//				dialog.show();
//				
//			}else{
//				adapter = new SimpleItemAdapter(getActivity(), itemList);
//				list.setAdapter(adapter);
//			}
//			Utils.checkForUpdate(context, Constants.VERSIONS_GROUPS, new Handler.Callback() {
//				@Override
//				public boolean handleMessage(Message msg) {
//					if (msg.what == 1){
//						itemList = dbHelper.getCategoryGroupsList();
//						if (dialog != null){
//							dialog.dismiss();
//							adapter = new SimpleItemAdapter(context, itemList);	
//							list.setAdapter(adapter);				
//						}else
//							adapter.notifyDataSetChanged();
//					}
//					return false;
//				}
//			});
//}}
