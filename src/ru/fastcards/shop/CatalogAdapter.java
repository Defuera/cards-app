package ru.fastcards.shop;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.common.CategoryGroup;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CatalogAdapter extends ArrayAdapter<CategoryGroup> implements SpinnerAdapter{

	private Context context;
	
	public CatalogAdapter(Context context, int resource,
			List<CategoryGroup> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context=context;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		convertView=inflater.inflate(R.layout.row_categories, null);

		CategoryGroup item=getItem(position);
		ImageView ivCover=(ImageView)convertView.findViewById(R.id.iv_cover);
		
		TextView tvName = (TextView)convertView.findViewById(R.id.tv_name);
		tvName.setText(item.getName());

		BitmapLoaderAsyncTask loader=new BitmapLoaderAsyncTask(context, null, false, true);
		loader.loadImageAsync(item.getCoverImage(), ivCover, null);
		
		return convertView;
	}
}