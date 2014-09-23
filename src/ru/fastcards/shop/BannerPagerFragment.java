package ru.fastcards.shop;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.common.Banner;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BannerPagerFragment extends Fragment{

	private ViewPager pager;
	private Context context;
	private List<Banner> listBanner;
	private long currentTime;
	private int number;
	private MyFragmentPagerAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		context=getActivity();
		if (pager==null) {
			pager=(ViewPager) inflater.inflate(R.layout.banner_pager_fragment, null);
			getListBanner();
			pager.setOnPageChangeListener(pageListener);
			return pager;
		}
		
		else if(listBanner==null) getListBanner();
		
		return pager;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	private Handler handler = new Handler();
		
		// Описание Runnable-объекта
		private Runnable BannerReplacer = new Runnable() {
			
			public void run() {
				if (System.currentTimeMillis()-currentTime>3000){
					currentTime=System.currentTimeMillis();
					if (pager.getCurrentItem()<number-1)
						pager.setCurrentItem(pager.getCurrentItem()+1);
					else pager.setCurrentItem(0);}
				
				handler.post(this);
			}
		};
		
	private void getListBanner(){
		new AsyncTask<Params, List<Banner>, List<Banner>>() {
			final Api api=Api.getInstanse(context);
			@Override
			protected List<Banner> doInBackground(Params... params) {
				try {
					listBanner = api.getBanners();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return listBanner;
			}
			protected void onPostExecute(List<Banner> listBanner) {
				if (listBanner!=null) 
				{
				adapter=new MyFragmentPagerAdapter(((FragmentActivity)context).getSupportFragmentManager());
				pager.setAdapter(adapter);
				number=listBanner.size();
				handler.post(BannerReplacer);
				}
			}
		}.execute();
	}

	 
	private OnPageChangeListener pageListener=new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			currentTime=System.currentTimeMillis();
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {}
	};
	
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{
		    public MyFragmentPagerAdapter(FragmentManager fm) {
		      super(fm);
		    }
		    @Override
		    public Fragment getItem(int position) {
		    	BannerFragment banner=new BannerFragment();
		    	banner.setBanner(listBanner.get(position));
		      return banner;
		    }
		    @Override
		    public int getCount() {
		      return listBanner.size();
		    }
		    
		    
}
	
	@Override
	public void onPause() {
		if (listBanner!=null) handler.removeCallbacks(BannerReplacer);
		super.onPause();
	}
}
