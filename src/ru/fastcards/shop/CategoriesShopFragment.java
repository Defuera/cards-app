//package ru.fastcards.shop;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import ru.fastcards.OnCategoriesClickListener;
//import ru.fastcards.R;
//import ru.fastcards.SimpleItemAdapter;
//import ru.fastcards.common.Category;
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
//public class CategoriesShopFragment extends Fragment{
//
//
//	private Context context;
//	private List<Category> categoriesList=new ArrayList<Category>();
//	private SimpleItemAdapter adapter;
//	private String title;
//	private String group_id;
//	private ListView list;
//	private ProgressDialog dialog;
//
//	
//	private OnCategoriesClickListener categoryClickListener;
//
//	public void onAttach(Activity activity) {
//	    super.onAttach(activity);
//	        try {
//	        	categoryClickListener = (OnCategoriesClickListener) activity;
//	        } catch (ClassCastException e) {
//	            throw new ClassCastException(activity.toString() + " must implement onCategoryClickListener");
//	        }
//	  }
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//		Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		
//		context=getActivity();
//		
//		View v=inflater.inflate(R.layout.categories_shop_fragment, null);
//		list=(ListView) v.findViewById(R.id.list_categories);
//		list.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int position,
//					long id) {
//				// TODO Auto-generated method stub
//				categoryClickListener.onCategoryClick(categoriesList.get(position).getName(),
//				categoriesList.get(position).getId());
//			}
//		});
//		createCategoryList();
//		return v;
//		
//	}	
//	
//	private void createCategoryList(){
//		final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
//		categoriesList = dbHelper.getCategoriesList(group_id);
//		if (categoriesList.isEmpty()){
//			dialog = new ProgressDialog(context);
//			dialog.setMessage(getString(R.string.loading_categories));
//			dialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
//				
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					categoryClickListener.onCancel();
//				}
//			});
//			dialog.show();
//		}else{
//			adapter = new SimpleItemAdapter(getActivity(), categoriesList);
//			list.setAdapter(adapter);
//		}
//		Utils.checkForUpdate(context, Constants.VERSIONS_CATEGORIES, new Handler.Callback() {
//			@Override
//			public boolean handleMessage(Message msg) {
//				if (msg.what == 1){
//					categoriesList = dbHelper.getCategoriesList(group_id);
//					if (dialog != null){
//						dialog.dismiss();
//						adapter = new SimpleItemAdapter(context, categoriesList);	
//						list.setAdapter(adapter);				
//					}else
//						adapter.notifyDataSetChanged();
//				}
//				return false;
//			}
//		});
//	}
//	
//	public void setGroupId(String ID){
//		this.group_id=ID;
//	}
//
//	public void setTitle(String title){
//		this.title=title;
//	}
//	
//	public String getTitle(){
//		return title;
//	}
//}
