package ru.fastcards.shop;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.Theme;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OfferCreator implements OnClickListener{
	
	private Context context;
	private Offer offer;
	private LayoutInflater inflater;
	private View v;
	private BitmapLoaderAsyncTask loader;
	private LinearLayout contentContainer;
	private int previewHeight;
	private int largeHeight;

	public OfferCreator(Context context,Offer offer,int previewHeight,int largeHeight){
		// TODO Auto-generated constructor stub
		this.context=context;
		this.offer=offer;
		this.previewHeight=previewHeight;
		this.largeHeight=largeHeight;
		
		inflater=(LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	
	public View createOfferView(){
		v=inflater.inflate(R.layout.shop_dialog_offer_content, null);
		LinearLayout titleContainer=(LinearLayout) v.findViewById(R.id.title_container);
		titleContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,previewHeight));
		
		LinearLayout imagesContainer=(LinearLayout) v.findViewById(R.id.cover_images_container);
		
		Adapter adapter = new OfferAdapter(context, offer.getArticlesList());
		for (int i = 0; i < adapter.getCount(); i++) {
			View item =adapter.getView(i, null, null);
			imagesContainer.addView(item);
			item.setId(i);
			item.setOnClickListener(this);}
		
		TextView description=(TextView) v.findViewById(R.id.description);
		description.setText(offer.getDescription());
		description.setTextSize(TypedValue.COMPLEX_UNIT_PX,(int)(previewHeight/2)/5);
		
		contentContainer=(LinearLayout) v.findViewById(R.id.content_container);
		contentContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,largeHeight));
	
		return v;
	}

	private void createThemeContent(final Article a){
		float width=largeHeight/1.5f;
		float height=width/1.5f;
		
		LinearLayout.LayoutParams verticalParams=new LinearLayout.LayoutParams((int) width,largeHeight);
		verticalParams.rightMargin=5;
		
		LinearLayout.LayoutParams squareParams=new LinearLayout.LayoutParams((int) width,(int)width);
		squareParams.rightMargin=5;
		
		LinearLayout.LayoutParams printParams=new LinearLayout.LayoutParams((int) width,(int)height);
		printParams.rightMargin=5;
		
		if (contentContainer!=null) contentContainer.removeAllViews();
		
		final View scrollImages=inflater.inflate(R.layout.row_scroll_images, null);
		final ImageView vertical=(ImageView) scrollImages.findViewById(R.id.vertical);
		vertical.setLayoutParams(verticalParams);
		
		final ImageView square=(ImageView) scrollImages.findViewById(R.id.square);
		square.setLayoutParams(squareParams);
		
		final ImageView print=(ImageView) scrollImages.findViewById(R.id.print);
		print.setLayoutParams(printParams);
		
			new AsyncTask<Params, Theme, Theme>() {
				final Api api = Api.getInstanse(context);
				Theme  theme;
				@Override
				protected Theme doInBackground(Params... params) {
					try {
						theme=(Theme)api.getPurchase(a.getPurchaseId(), a.getPurchaseType());
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					return theme;
				}
				protected void onPostExecute(Theme theme) {
					loader.loadImageAsync(theme.getECardThumb(), vertical, null);
					loader.loadImageAsync(theme.getSquareThumb(), square, null);
					loader.loadImageAsync(theme.getPostCardFrontThumb(), print, null);
					contentContainer.addView(scrollImages);
				}
	}.execute();
		
	}
	
	private void createTextContent(final Article a){
		if (contentContainer!=null) contentContainer.removeAllViews();
		final TextView text_description=new TextView(context);
		text_description.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		text_description.setTextColor(Color.BLACK);
		text_description.setGravity(Gravity.CENTER);
		text_description.setSingleLine(false);
		
			new AsyncTask<Params, TextPack, TextPack>() {
				final Api api = Api.getInstanse(context);
				TextPack  textPack;
			@Override
			protected TextPack doInBackground(Params... params) {
				try {
					textPack=(TextPack)api.getPurchase(a.getPurchaseId(), a.getPurchaseType());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return textPack;
			}
			protected void onPostExecute(TextPack textPack) {
				text_description.setText(textPack.getDescription());
				contentContainer.addView(text_description);
			}
		}.execute();
	}
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		for (int i=0;i<offer.getArticlesList().size();i++){
			if (v.getId()==i) {
				Article article=offer.getArticlesList().get(i);
				int purchase_type=Integer.parseInt(article.getPurchaseType());
				switch (purchase_type){
				case 1:
					createThemeContent(article);
					break;
				case 2:
					createTextContent(article);
					break;
				}
				
				}
			}
	}
	
	private class OfferAdapter extends ArticleAdapter{

		private Context context;
		
		public OfferAdapter(Context ctx, List<Article> postcardsList) {
			super(ctx, postcardsList);
			// TODO Auto-generated constructor stub		
			this.context = ctx;
			loader = new BitmapLoaderAsyncTask(context, null ,false, false);
		}
		
		public View getView(int position, View view, ViewGroup parent) {
			final Article item = getItem(position);
			if (view == null) {
				
			view = inflater.inflate(R.layout.item_offer_dialog, parent,	false);}
				
			ImageView imageView = (ImageView) view.findViewById(R.id.iv_item_icon);
			imageView.setLayoutParams(new LinearLayout.LayoutParams((int)(previewHeight/2),(int)(previewHeight/2)));
			loader.loadImageAsync(item.getCoverImage(), imageView, null);
			
			if ("2".equals(offer.getArticlesList().get(0).getPurchaseType())) createTextContent(offer.getArticlesList().get(0));
			else createThemeContent(offer.getArticlesList().get(0));
			return view;
		}
	}
}
