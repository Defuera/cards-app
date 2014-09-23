package ru.fastcards.shop;

import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Banner;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BannerFragment extends Fragment{

	private Context context;
	private Banner banner;
	
	private onShopItemClickListener shopItemClickListener;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		 try {
	        	shopItemClickListener = (onShopItemClickListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement shopItemClickListener");
	        }
			super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.fragment_banner, null);
		ImageView image_banner=(ImageView) v.findViewById(R.id.banner);
		
		if (banner==null) return v;
		
		BitmapLoaderAsyncTask loader=new BitmapLoaderAsyncTask(context, null, false, false);
		loader.loadImageAsync(banner.getCover(), image_banner, null);
		
		image_banner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				shopItemClickListener.buyItem(banner,banner.getPurchaseType());	
			}
		});
		return v;
	}
	
	public void setBanner(Banner banner){
		this.banner=banner;
	}
	

	
}
