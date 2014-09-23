package ru.fastcards.shop;

import java.util.List;

import ru.fastcards.R;
import ru.fastcards.onShopItemClickListener;
import ru.fastcards.common.Article;
import ru.fastcards.common.Theme;
import ru.fastcards.common.ThemesCategory;
import ru.fastcards.utils.Constants;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CategoriesScrollAdapter extends ArrayAdapter<ThemesCategory>{
	
	
	private Context context;
	private ArticleAdapter adapter;
	
	public CategoriesScrollAdapter(Context context, int resource,List<ThemesCategory> list) {
		super(context, resource, list);
		// TODO Auto-generated constructor stub
		this.context=context;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		convertView=inflater.inflate(R.layout.fragment_actions, null);
		
		TextView textView=(TextView) convertView.findViewById(R.id.tv_name);
		
		LinearLayout actionsScrollView = (LinearLayout) convertView.findViewById(R.id.ll_actions);

		ThemesCategory item=getItem(position);
		
		textView.setText(item.getCategory().getName());
		
		List<Theme> list=item.getThemesList();
		
		adapter = new ArticleAdapter(context, (List<Article>)(List<?>)list);

		if (adapter != null){
			for (int i = 0; i < adapter.getCount(); i++) {
				View v =adapter.getView(i, null, null);
				v.setTag(list.get(i));
				actionsScrollView.addView(v);
				v.setOnClickListener(clickListener);
			}
		}
		return convertView;
	}
	
	private OnClickListener clickListener=new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onShopItemClickListener shopItemClickListener=(onShopItemClickListener)context;
			Article article=(Article) v.getTag();
			shopItemClickListener.buyItem(article,""+Constants.PURCHASE_TYPE_THEME_ID);	
		}
	};
}
