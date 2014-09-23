package ru.fastcards.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import ru.fastcards.R;
import ru.fastcards.common.Text;
import ru.fastcards.common.TextPack;
import ru.fastcards.social.api.Api;
import ru.fastcards.social.api.Params;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class TextFragment extends Fragment{
	
	private Context context;
	private ListView set_text;
	private List<Text> listTexts;
	private String header_text;
	private LinearLayout header;
	private int position;
	private String textPack_id;
	
	//Listeners 
	private OnClickListener onReturnToTextPackClickListener;
	private OnItemClickListener onSetTextClickListener;
	private ProgressDialog dialog;
	private TextAdapter adapter;
	private SharedPreferences prefs;
	
	
	public TextFragment(){
	}
	
	public static TextFragment newInstance(Context context,String header_text,int position,String textPack_id){
		TextFragment stf=new TextFragment();
		stf.context=context;
		stf.header_text=header_text;
		stf.position=position;
		stf.textPack_id=textPack_id;
		return stf;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.text_fragment, null);
		set_text=(ListView) v.findViewById(android.R.id.list);

		header=(LinearLayout) v.findViewById(R.id.header);
		
		TextView header_text_view=(TextView) v.findViewById(R.id.header_text);
		header_text_view.setText(header_text);
				
		header.setOnClickListener(onReturnToTextPackClickListener);
	    
	    set_text.setOnItemClickListener(onSetTextClickListener); 
	    if (position==0)loadRecentlyUsedText();
	    else loadTexts();

		return v;
	}
	
	
	private void loadRecentlyUsedText(){
		prefs = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		listTexts=new ArrayList<Text>();
		int size = prefs.getInt(Constants.RECENTLY_USED_ARRAY_SIZE, 0);
		for (int i = 0; i < size; i++) {
			
			String textString=prefs.getString(
					Constants.RECENTLY_USED_TEXT_NUMBER + i, " ");
			String textName=ClippingTextName(textString);
			
			listTexts.add(new Text(Constants.RESENTLY_USED_ID, textName, textString));
		}
		adapter = new TextAdapter(context,listTexts);
		set_text.setAdapter(adapter);
}
	
	private String ClippingTextName(String string){
		String name;
		int endIndex=(string.indexOf('\n'));
		if (endIndex==-1) {
			name=string;
		}
		else {
			name=string.substring(0, endIndex);
		}
		return name;
	}
	
	public void setOnReturnToTextPackClickListener(OnClickListener listener){
		onReturnToTextPackClickListener=listener;
	}
	
	public void setOnSetTextClickListener(OnItemClickListener listener){
		onSetTextClickListener=listener;
	}

	public List<Text> getListText(){
		return listTexts;
	}
	
	private void loadTexts() {
		final DataBaseHelper dbHelper = DataBaseHelper.getInstance(context);
		listTexts = dbHelper.getTextsListByPackId(textPack_id);
//		if (listTexts.isEmpty()){
////			dialog = new ProgressDialog(getActivity());
////			dialog.setMessage("Loading data");
////			dialog.setCancelable(false);
////			dialog.show();
//		}else{
			adapter = new TextAdapter(context,listTexts);
			set_text.setAdapter(adapter);
//		}
//		
//		Utils.checkForUpdate(context, Constants.VERSIONS_TEXTS, new Handler.Callback() {
//			@Override
//			public boolean handleMessage(Message msg) {
//				if (msg.what == 1){
//					listTexts = dbHelper.getTextsListByPackId(textPack_id);
//					
//					if (dialog != null){
//						dialog.dismiss();
//						adapter = new TextAdapter(context,listTexts);
//						set_text.setAdapter(adapter);
//					} else
//						adapter.notifyDataSetChanged();
//					
//				}
//				return false;
//			}
//		});
//			new AsyncTask<Params, List<TextPack>, List<TextPack>>() {
//				Api api=new Api(context);
//				@Override
//				protected List<TextPack> doInBackground(Params... params) {
//					try {
//						listTexts =api.get;
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					
//					return listTextPack;
//				}
//				protected void onPostExecute(List<TextPack> list) {
//					if (list!=null){
//					if (greetingText!=null){
//						listTextPack.add(0, createRecentlyUsedPack());
//						progress.setVisibility(View.INVISIBLE);
//						adapter = new TextPacksAdapter(context, listTextPack);
//						greetingText.setAdapter(adapter);
//						}
//					}
//					else 
//					{
//						Toast.makeText(context, getString(R.string.str_no_network_connection), Toast.LENGTH_LONG).show();
//					}
//				}
//			}.execute();

	}


}
