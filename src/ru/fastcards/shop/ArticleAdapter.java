
package ru.fastcards.shop;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.ScreenParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author Denis V. on 20.11.2013
 *
 */
public class ArticleAdapter extends ArrayAdapter<Article> {

	private Context context;
	private BitmapLoaderAsyncTask loader;
	private static final int imageSize=(ScreenParams.screenHeight)/7;
	
	public ArticleAdapter(Context ctx, List<Article> postcardsList) {
		super(ctx, R.layout.item_article, postcardsList);
		this.context = ctx;
		loader = new BitmapLoaderAsyncTask(context, null ,false, false);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		final Article item = getItem(position);
		
		LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (view==null)
			view = inflater.inflate(R.layout.item_article, parent,	false);

		TextView itemTitleTextView = (TextView) view.findViewById(R.id.tv_item_title);
		itemTitleTextView.setText(item.getName());
		
		TextView priceTextView = (TextView) view.findViewById(R.id.tv_item_additional);
		
		if (item.isBought()){
			ImageView starImageView = (ImageView) view.findViewById(R.id.iv_star);
			starImageView.setVisibility(View.GONE);
			priceTextView.setText(context.getResources().getString(R.string.str_bought));
		}else{
			priceTextView.setText(Integer.toString((int)item.getPrice()));
		}

		ProgressBar  progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_icon);
		
		if (item instanceof Offer){
			float width=ScreenParams.screenWidth-50;
			float height=width*180/609;
			imageView.setLayoutParams(new RelativeLayout.LayoutParams((int)width,(int)height));
			imageView.setScaleType(ScaleType.FIT_XY);
			}
		else	
			imageView.setLayoutParams(new RelativeLayout.LayoutParams(imageSize,imageSize));
		
		loader.loadImageAsync(item.getCoverImage(), imageView, progressBar);
		
		return view;
	}
}
