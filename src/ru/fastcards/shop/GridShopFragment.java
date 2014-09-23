package ru.fastcards.shop;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Article;
import ru.fastcards.common.Theme;
import ru.fastcards.common.ThemesCategory;
import ru.fastcards.utils.Constants;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class GridShopFragment extends Fragment {
	
	private GridView gridView;
	private Context context;
	private List<Article> listArticle;
	private onShopItemClickListener shopItemClickListener;
	private ArticleAdapter adapter;
	private ListView listView;
	private CategoriesScrollAdapter scrollAdapter;
	private List<ThemesCategory> listCategories;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context=getActivity();
		View v=inflater.inflate(R.layout.shop_grid_view, null);
		gridView=(GridView) v.findViewById(R.id.grid);
		gridView.setOnItemClickListener(listener);
		listView=(ListView) v.findViewById(R.id.list);
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
			try {
				shopItemClickListener=(onShopItemClickListener)activity;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new ClassCastException(activity.toString() + " must implement shopItemClickListener");
			}
	}
	
	public void setListArticle(List<?> list){
		
		clearList();
		
		listArticle=(List<Article>)list;
		
		if (listArticle!=null){
			adapter=new ArticleAdapter(context, listArticle);
			gridView.setAdapter(adapter);
		}
		else 
			Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_SHORT).show();
	}
	
	public void setThemesCategories(Context context,List<ThemesCategory> list){
		
		clearGrid();
		
		listCategories=list;
		scrollAdapter=new CategoriesScrollAdapter(context, R.layout.fragment_actions,listCategories);
		listView.setAdapter(scrollAdapter);
	}
	
	private void clearGrid(){
		if (listArticle!= null){
			listArticle.clear();
			adapter.notifyDataSetChanged();
		}
	}

	private void clearList(){
		if (listCategories!=null){
			listCategories.clear();
			scrollAdapter.notifyDataSetChanged();
		}
	}

	
	private OnItemClickListener listener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String purchaseType;
			if (listArticle.get(position) instanceof Theme) {
				purchaseType=""+Constants.PURCHASE_TYPE_THEME_ID;
			}
			else 
				purchaseType=""+Constants.PURCHASE_TYPE_TEXT_ID;
			
			shopItemClickListener.buyItem(listArticle.get(position), purchaseType);
		}
	};


	
}

