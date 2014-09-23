package ru.fastcards.shop;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.social.api.Api;
import ru.fastcards.utils.Constants;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class BundlesShopFragment extends Fragment{
	
	private ListView listViewBundles;
	private List<Offer> listBundles;
	private ProgressBar progress;
	private ArticleAdapter adapter;
	private Context context;
	
	private onShopItemClickListener shopItemClickListener;
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		context=activity;
		        try {
		        	shopItemClickListener = (onShopItemClickListener) activity;
		        } catch (ClassCastException e) {
		            throw new ClassCastException(activity.toString() + " must implement shopItemClickListener");
		        }
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_bundle, null);
		listViewBundles=(ListView) v.findViewById(R.id.list_bundles);
		progress=(ProgressBar) v.findViewById(R.id.progressBar);
		if (listBundles==null)
			loadListBundles();
		else {
			adapter.notifyDataSetChanged();
			listViewBundles.setAdapter(adapter);
			}
		
		listViewBundles.setOnItemClickListener(listener);
		return v;
	}
	
	private OnItemClickListener listener=new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			shopItemClickListener.buyItem(listBundles.get(position), ""+Constants.PURCHASE_TYPE_OFFER_ID);
		}
	};
	
	private void loadListBundles() {
		final Api api=Api.getInstanse(context);
		new AsyncTask<Void, List<Offer>, List<Offer>>() {
			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			protected List<Offer> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					listBundles=api.getAllOffers();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return listBundles;
			}
			
			@Override
			protected void onPostExecute(List<Offer> result) {
				// TODO Auto-generated method stub
				if (result!=null)
				{
					adapter=new ArticleAdapter(context, ((List<Article>)(List<?>)result));
					listViewBundles.setAdapter(adapter);
				}
				progress.setVisibility(View.INVISIBLE);
			}
			
		}.execute();
	}
	
	public void refresh(){
		listBundles=null;
		if (this.isVisible()) loadListBundles();
	}

}
