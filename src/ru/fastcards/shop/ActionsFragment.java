package ru.fastcards.shop;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Article;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.Constants;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Denis V. On 20.11.2013
 * 
 * 
 */
public class ActionsFragment extends Fragment implements OnClickListener {

	private int whatList;
	private Context context;
	private View contentView;
	private List<Article> listArticles;
	
	private View item;
	private ArticleAdapter adapter;
	
	public static ActionsFragment newInstance(Context context,int whatList){
		ActionsFragment fragment=new ActionsFragment();
		fragment.context=context;
		fragment.whatList=whatList;
		return fragment;
	}
	
	private onShopItemClickListener shopItemClickListener;
	
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	        	shopItemClickListener = (onShopItemClickListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement shopItemClickListener");
	        }
	  }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_actions, container, false);
		if (listArticles==null) loadListArticles();
		else setAdapter();
		return contentView;
	}

	private void setAdapter() {
		LinearLayout actionsScrollView = (LinearLayout) contentView.findViewById(R.id.ll_actions);
		TextView tvName=(TextView) contentView.findViewById(R.id.tv_name);
		tvName.setLayoutParams(new LinearLayout.LayoutParams(0,0));
		
		if (listArticles!=null){
			adapter = new ArticleAdapter(context, listArticles);
			actionsScrollView.removeAllViews();}
		if (adapter != null){
			for (int i = 0; i < adapter.getCount(); i++) {
				item =adapter.getView(i, null, null);
				actionsScrollView.addView(item);
				item.setId(i);
				item.setOnClickListener(this);
			}
		}

	}
	
	private void loadListArticles(){
			//load new Article
			new AsyncTask<Params, List<Article>, List<Article>>() {
				final Api api = Api.getInstanse(context);
				@Override
				protected List<Article> doInBackground(Params... params) {
					try {
						switch(whatList){
						case Constants.GET_NEW: 
							listArticles = api.getNewPurchases();
							break;
						case (Constants.GET_RECOMMENDED):
							listArticles = api.getRecommendedPurchases();
							break;
						case (Constants.GET_BEST_SELLERS):
							listArticles = api.getBestSellers();
							break;
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return listArticles;
				}
				protected void onPostExecute(List<Article> listArticles) {
					if (listArticles!=null) setAdapter();
				}
			}.execute();
	}
	
	public void refresh(){
		listArticles=null;
		if (this.isVisible())
			loadListArticles();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		for (int i=0;i<listArticles.size();i++){
			if (v.getId()==i) {
				Article article=listArticles.get(i);
				shopItemClickListener.buyItem(article,article.getPurchaseType());	
				}
			}
		}
	}
