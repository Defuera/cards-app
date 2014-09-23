package ru.fastcards.shop;

import ru.fastcards.R;
import ru.fastcards.common.Theme;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.ScreenParams;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ThemeCreator implements OnClickListener{
	
	private Context context;
	private LayoutInflater inflater;
	private ImageView coverImageView;
	private ImageView vertical,square,print;
	private TextView title_description;
	private int selection_postcard=Constants.POSTCARD_VERTICAL;
	private int previeHeight;
	private int largeHeight;
	
	public ThemeCreator(Context context,int previewHeight,int largeHeight) {
		// TODO Auto-generated constructor stub
		this.context=context;
		inflater=(LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.previeHeight=previewHeight;
		this.largeHeight=largeHeight;


	}

	public View createTheme(Theme theme,Handler callback){

		View v=inflater.inflate(R.layout.shop_dialog_theme_content, null);
		
		LinearLayout preview_screen=(LinearLayout) v.findViewById(R.id.preview_screen);
		preview_screen.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,previeHeight));
		 
		LinearLayout large_screen=(LinearLayout) v.findViewById(R.id.large_screen);
		large_screen.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,largeHeight));
		
		coverImageView=(ImageView)v.findViewById(R.id.cover_image_view);
		
		vertical=(ImageView) v.findViewById(R.id.vertical);
		vertical.setOnClickListener(this);
		square=(ImageView) v.findViewById(R.id.square);
		square.setOnClickListener(this);
		print=(ImageView) v.findViewById(R.id.print);
		print.setOnClickListener(this);
	
		ProgressBar progress_vertical=(ProgressBar) v.findViewById(R.id.progress_vertical);
		BitmapLoaderAsyncTask loader_v =new BitmapLoaderAsyncTask(context, callback, false, true);
		loader_v.loadImageAsync(theme.getECardThumb(), vertical, progress_vertical);
		loader_v.loadImageAsync(theme.getECardThumb(), coverImageView, progress_vertical);
		
		ProgressBar progress_square=(ProgressBar) v.findViewById(R.id.progress_square);
		BitmapLoaderAsyncTask loader_s =new BitmapLoaderAsyncTask(context, callback, false, true);
		loader_s.loadImageAsync(theme.getSquareThumb(), square, progress_square);
		
		ProgressBar progress_print=(ProgressBar) v.findViewById(R.id.progress_print);
		BitmapLoaderAsyncTask loader_f =new BitmapLoaderAsyncTask(context, callback, false, true);
		loader_f.loadImageAsync(theme.getPostCardFrontThumb(), print, progress_print);
		
		BitmapLoaderAsyncTask loader_b =new BitmapLoaderAsyncTask(context, callback, false, true);
		loader_b.loadImageAsync(theme.getPostCardBackThumb(), null, null);
		
		
		title_description=(TextView)v.findViewById(R.id.description);
		title_description.setText(context.getResources().getString(R.string.vertical_description));
		
		vertical.setBackgroundResource(R.drawable.selector_previews);
		square.setBackgroundResource(R.drawable.selector_previews);
		print.setBackgroundResource(R.drawable.selector_previews);
		
		
		
		vertical.setEnabled(false);
		
		createYellowStroke();
		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		vertical.setEnabled(true);
		square.setEnabled(true);
		print.setEnabled(true);
		
		switch (v.getId()){
		case R.id.vertical: 
			coverImageView.setImageDrawable(vertical.getDrawable());
			title_description.setText(context.getResources().getString(R.string.vertical_description));
			vertical.setEnabled(false);
			selection_postcard=Constants.POSTCARD_VERTICAL;
			break;
		case R.id.square: 
			coverImageView.setImageDrawable(square.getDrawable());
			title_description.setText(context.getResources().getString(R.string.square_description));
			square.setEnabled(false);
			selection_postcard=Constants.POSTCARD_SQUARE;
			break;
		case R.id.print: 
			coverImageView.setImageDrawable(print.getDrawable());
			title_description.setText(context.getResources().getString(R.string.print_description));
			selection_postcard=Constants.POSTCARD_HORIZONTAL;
			print.setEnabled(false);
			break;
		}
		}
	
	private void createYellowStroke(){
		int imegeWidth=ScreenParams.screenWidth/4;
		int imageVHeight=(int)(imegeWidth*1.5);
		int imageHHeight=(int)(imegeWidth/1.5);
		
		RelativeLayout.LayoutParams verticalParams=new RelativeLayout.LayoutParams(imegeWidth,imageVHeight);
		verticalParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		vertical.setLayoutParams(verticalParams);
		vertical.setScaleType(ScaleType.FIT_CENTER);
		
		RelativeLayout.LayoutParams squareParams=new RelativeLayout.LayoutParams(imegeWidth,imegeWidth);
		squareParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		square.setLayoutParams(squareParams);
		square.setScaleType(ScaleType.FIT_CENTER);
		
		RelativeLayout.LayoutParams printParams=new RelativeLayout.LayoutParams(imegeWidth,imageHHeight);
		printParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		print.setLayoutParams(printParams);
		print.setScaleType(ScaleType.FIT_CENTER);
		
		
		
		
	}
	
	public int getSelectedPostcard(){
		return selection_postcard;
	}
}
