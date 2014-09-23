package ru.fastcards.shop;

import ru.fastcards.R;
import ru.fastcards.common.TextPack;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextCreator {

	private Context context;
	private LayoutInflater inflater;
	private int largeHeight;
	
	public TextCreator(Context context,int preview_height,int largeHeight) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.largeHeight=largeHeight;
		inflater=(LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public View createTextPack(TextPack textPack, Handler callback){
		View v=inflater.inflate(R.layout.shop_dialog_text_content,null);
		ImageView coverImageView=(ImageView)v.findViewById(R.id.cover_image_view);
		
		BitmapLoaderAsyncTask loader =new BitmapLoaderAsyncTask(context, callback, false, false);
		loader.loadImageAsync(textPack.getCoverImage(), coverImageView, null);
		coverImageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,largeHeight));
		
		TextView title_description=(TextView)v.findViewById(R.id.description);
		title_description.setText(textPack.getDescription());
		
		return v;
	}
}
