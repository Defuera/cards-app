//package ru.fastcards.shop;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.json.JSONException;
//
//import ru.fastcards.R;
//import ru.fastcards.onShopItemClickListener;
//import ru.fastcards.common.Article;
//import ru.fastcards.social.api.Api;
//import ru.fastcards.social.api.Params;
//import android.app.Activity;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.GridView;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.TextView.OnEditorActionListener;
//import android.widget.Toast;
//
//public class SearchFragment extends Fragment{
//		private View v;
//		private Context context;
//		private String search_string;
//		private List<Article> listArticle;
//		
//		private EditText search_text;
//		private ImageButton search_button;
//		private GridView search_grid_view;
//		private ProgressBar search_progress;
//		private MainShopActivity activity;
//		
//		private ArrayAdapter<?> adapter;
//		private onShopItemClickListener shopItemClickListener;
//		
//		private static final String TAG="SearchFragment";
//		
//		public static SearchFragment newInstance(Context context){
//			SearchFragment fragment=new SearchFragment();
//			fragment.context=context;
//			return fragment;
//		}
//		
//		@Override
//		public void onAttach(Activity activity) {
//		    super.onAttach(activity);
//		        try {
//		        	shopItemClickListener = (onShopItemClickListener) activity;
//		        } catch (ClassCastException e) {
//		            throw new ClassCastException(activity.toString() + " must implement shopItemClickListener");
//		        }
//		  }
//		
//		
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//		Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		activity=(MainShopActivity) getActivity();
//		v=inflater.inflate(R.layout.view_main_shop_search, null);
//		search_text=(EditText) v.findViewById(R.id.search_text);
//		search_text.setOnEditorActionListener(editorActionListener);
//		search_button=(ImageButton) v.findViewById(R.id.search_button);
//		search_grid_view=(GridView) v.findViewById(R.id.search_grid_view);
//		search_grid_view.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
//					long id) {
//				// TODO Auto-generated method stub
//				shopItemClickListener.buyItem(listArticle.get(position), listArticle.get(position).getPurchaseType());
//			}
//			
//		});
//		search_progress=(ProgressBar) v.findViewById(R.id.search_progress);
//		search_progress.setVisibility(View.INVISIBLE);
//		search_button.setOnClickListener(new android.view.View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				search_string=search_text.getText().toString();
//				loadSearchArticles();
//			}
//		});
//		return v;
//}
//	
//		
//		OnEditorActionListener editorActionListener=new OnEditorActionListener() {
//			
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				// TODO Auto-generated method stub
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//					search_string=search_text.getText().toString();
//					loadSearchArticles();
//		            return true;
//		        }
//		        return false;
//			}
//		};
////		private ShopDialogFragment dialog;
//		private void loadSearchArticles(){
//			//Load MyTextPacks
//			new AsyncTask<Params, List<Article>, List<Article>>() {
//				Api api=new Api(context);
//				
//				protected void onPreExecute() {
//					search_progress.setVisibility(View.VISIBLE);
//					InputMethodManager imm = (InputMethodManager)context.getSystemService(
//									Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
//				};
//				@Override
//				protected List<Article> doInBackground(Params... params) {
//					List<Article> list=null;
//					try {
//						list =api.search(search_string);
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//					return list;
//				}
//				protected void onPostExecute(List<Article> list) {
//					if (list!=null &&!list.isEmpty() ){
//						listArticle=list;
//						adapter = new ArticleAdapter(context,listArticle);
//						search_grid_view.setAdapter(adapter);
//					}
//
//					else Toast.makeText(context, context.getResources().getString(R.string.search_cancelled), 
//							Toast.LENGTH_SHORT).show();
//
//					search_progress.setVisibility(View.INVISIBLE);
//				}
//			}.execute();
//		}
//		
//		public void refresh(){
//			if (this.isVisible()){
//				loadSearchArticles();
//			}
//		}
//		
////		private void createShopDialog(final Article article){
////			dialog=ShopDialogFragment.newInstance(getActivity(), article,article.getPurchaseType());
////			dialog.show(getActivity().getSupportFragmentManager(), TAG);
////			dialog.setOnChooseClickListener(listener);
////			dialog.setOnBuyClickListener(new OnClickListener() {
////				
////				@Override
////				public void onClick(View v) {
////					// TODO Auto-generated method stub
////					new AsyncTask<String, String, String>(){
////
////						@Override
////						protected String doInBackground(String... params) {
////							String result = null;
////							try {
////								Api api = new Api(context);
////								result = api.makePurchase(article.getPurchaseId());
////							} catch (Exception e) {
////								e.printStackTrace();
////							} 
////							return result;
////						}
////						@Override
////						protected void onPostExecute(String result) {
////							super.onPostExecute(result);
////							if ("".equals(result)){
////								Toast.makeText(context, "Transaction succeed", Toast.LENGTH_SHORT).show();
////								loadSearchArticles();/// refresh
////								dialog.refreshDialogButtons();
////								activity.refreshDB(article);
////							} else {
////								Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
////							}
////						}
////						
////					}.execute();
////				}
////			});
////		}
////
////		
////		private OnClickListener listener;
////		
////		public void setChooseListener(OnClickListener listener){
////			this.listener=listener;
////		}
////		
////		public ShopDialogFragment getDialog(){
////			return dialog;
////		}
//	}
//
