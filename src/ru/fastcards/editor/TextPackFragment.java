package ru.fastcards.editor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.common.Article;
import ru.fastcards.common.Offer;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.Theme;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import ru.fastcards.utils.WrongResponseCodeException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class TextPackFragment extends Fragment{
		
		private Context context;
		private ListView greetingText;
		private boolean opened;
		private ArrayAdapter<TextPack> adapter;
		private static List<TextPack> listTextPack;
		private Theme theme;
		private ProgressBar progress;
		
		private OnItemClickListener onTextPackClickListener;
		private OnClickListener onOpenClickListener;
		private OnClickListener onCloseClickListener;
		private RelativeLayout header;
		
		public TextPackFragment(){}
		
		public static TextPackFragment newInstance(Context context,boolean opened,Theme theme){
			TextPackFragment gtf=new TextPackFragment();
			gtf.context=context;
			gtf.opened=opened;
			gtf.theme=theme;
			return gtf;
		}

		public static void clearTextPackList(){
			if (listTextPack!=null) listTextPack.clear();
		}
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v;
			if (!opened) v= inflater.inflate(R.layout.text_pack_fragment_close, null);
			else  v= inflater.inflate(R.layout.text_pack_fragment_open, null);
			
			if (opened){
				greetingText=(ListView) v.findViewById(R.id.greeting_text);
				greetingText.setOnItemClickListener(onTextPackClickListener);
				progress =(ProgressBar) v.findViewById(R.id.progressBar);
				progress.setVisibility(View.INVISIBLE);
					if (listTextPack==null || listTextPack.isEmpty())loadTextPacks();
				else {
					adapter = new TextPacksAdapter(context, listTextPack);
					greetingText.setAdapter(adapter);}
			}
			
			header=(RelativeLayout)v.findViewById(R.id.header);
			
			if (opened) header.setOnClickListener(onCloseClickListener);
			else header.setOnClickListener(onOpenClickListener);

//			header.setLayoutParams(header_params);
			return v;
		}
		
		public void setOnTextPackClickListener(OnItemClickListener listener){
			onTextPackClickListener=listener;
		}
		
		public void setOnOpenClickListener(OnClickListener listener){
			onOpenClickListener=listener;
		}
		
		public void setOnCloseClickListener(OnClickListener listener){
			onCloseClickListener=listener;
		}
		
		public List<TextPack> getListTextPack(){
			return listTextPack;
		}
		
		public boolean getOpened(){
			return opened;
		}

		private TextPack createRecentlyUsedPack() {
			TextPack pack = new TextPack(Constants.RESENTLY_USED_ID, getResources()
					.getString(R.string.recently_used));
			pack.setBought(true);
			return pack;
		}
		
		public void refresh(){
			loadTextPacks();
		}

		private void loadTextPacks() {
			final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);

				listTextPack=dbHelper.getTextPacksListByThemeId(theme.getId());
				if (listTextPack==null||listTextPack.isEmpty()) loadfromNetwork();
				else 
				{
				listTextPack.add(0, createRecentlyUsedPack());
				adapter = new TextPacksAdapter(context, listTextPack);
				greetingText.setAdapter(adapter);
				}
						
			if (listTextPack.size()==1) 
				progress.setVisibility(View.VISIBLE);
}
		
	private void loadfromNetwork(){
	
	new AsyncTask<Params, List<TextPack>, List<TextPack>>() {
		final Api api = Api.getInstanse(context);
		@Override
		protected List<TextPack> doInBackground(Params... params) {
			try {
				listTextPack =api.getTextPacksByTheme(theme.getId());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return listTextPack;
		}
		protected void onPostExecute(List<TextPack> list) {
			if (list!=null){
			if (greetingText!=null){
				saveTextPacktoDB(listTextPack);
				listTextPack.add(0, createRecentlyUsedPack());
				progress.setVisibility(View.INVISIBLE);
				adapter = new TextPacksAdapter(context, listTextPack);
				greetingText.setAdapter(adapter);
				}
			}
			else 
			{
				Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_LONG).show();
			}
		}
	}.execute();
	}
		
		private void saveTextPacktoDB(List<TextPack> listPack){
			DataBaseHelper dbHelper=DataBaseHelper.getInstance(context);
			for (TextPack pack:listPack){
				dbHelper.saveTextPack(pack);	
			}
		}
		}



