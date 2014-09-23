package ru.fastcards.shop;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.utils.Constants;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListShopFragment extends Fragment{
	
	private ListView listView;
	private List<Offer> listOffer;
	private onShopItemClickListener shopListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_scroll_shop, null);
		listView=(ListView) v.findViewById(R.id.list);
		listView.setOnItemClickListener(onItemClick);
		return v;
	}
	
	public void setOfferList(Context context, List<Offer> listOffer){
		this.listOffer=listOffer;
		ArticleAdapter adapter=new ArticleAdapter(context, (List<Article>)(List<?>)listOffer);
		listView.setAdapter(adapter);
		shopListener=(onShopItemClickListener)context;
		
	}
	
	private OnItemClickListener onItemClick=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			if (listOffer==null) return;
			shopListener.buyItem(listOffer.get(position), ""+Constants.PURCHASE_TYPE_OFFER_ID);
		}
	};

}
