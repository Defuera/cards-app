package ru.fastcards.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;

import ru.fastcards.HubActivity;
import ru.fastcards.PrintPdfActivity;
import ru.fastcards.PurseDialogFragment;
import ru.fastcards.R;
import ru.fastcards.TrackedActivity;
import ru.fastcards.common.Postcard;
import ru.fastcards.common.Project;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.Theme;
import ru.fastcards.editor.PostCardFragment.onPostCardListener;
import ru.fastcards.editor.PostcardFormButtonsFragment.onFormChangeListener;
import ru.fastcards.shop.Purchaser;
import ru.fastcards.shop.TextDialogFragment;
import ru.fastcards.social.api.Api;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.ImageManager;
import ru.fastcards.utils.ScreenParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class EditorActivity extends TrackedActivity implements
		onPostCardListener,
		onFormChangeListener {
	private LinearLayout fragment_postcard;

	private Activity activity;

	private Theme theme;
	
	// Fragments
	private TextPackFragment textPackFragment;
	private TextFragment textFragment;
//	private AppealFragment appeal_fragment;
//	private PostcardEditButtonsFragment editButtonsFragment;
	private PostcardFormButtonsFragment formButtonsFragment;
	
	private PostCardFragment postcard;
	private PostCardFragmentVertical vertical;
	private PostCardFragmentSquare square;
	private PostCardFragmentPrint print;
	private ArrayList<String> urlArray=new ArrayList<String>();
	private Bitmap[] bitmapArray=new Bitmap[3];
	


	private DataBaseHelper dbHelper;


	final int REQUEST_CODE_SIGNATURE = 1;

	// Create Postcard
	private Postcard card;

	private FragmentTransaction fm;
	private FrameLayout bottom_fragment_container;

	Button vertical_form_button, square_form_button, print_form_button;

	private RelativeLayout.LayoutParams bottom_close_params;
	private RelativeLayout.LayoutParams bottom_open_params;
	private RelativeLayout.LayoutParams postcard_params;

	private String themePurchaseId;

	private SharedPreferences prefs;

	private ArrayList<String> recently_used = new ArrayList<String>();

	private final int MAXIMUM_TEXTS = 20;

	private Project project;

//	private List<Appeal> appeal_list;

	private ProgressDialog dialog;
	private static final String TAG = "EditorActivity";
	
	private int delta=0;
	private int keyBoardHeight;

	private int selected_postcard;

	private TextDialogFragment textDialog;
	
	private ProgressDialog loadDialog;
	
//OVERRIDED ACTIVITY METHODS******************************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);

		activity=this;
		
		createLayoutParams();
		
		//Create New Postcard
		card = new Postcard();
		
		selected_postcard = getIntent().getIntExtra(Constants.EXTRA_POSTCARD_SELECTED, Constants.POSTCARD_VERTICAL);

		dbHelper = DataBaseHelper.getInstance(activity);
		getProjectExtra();
		getThemeExtra();
		

	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	private Handler textCallback=new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what==Constants.TRANSACTION_SUCCED)
				textPackFragment.refresh();
			else if (msg.what==Constants.NOT_ENOUGH_STARS)
				new PurseDialogFragment().show(getSupportFragmentManager(), TAG);
		};
	};
	
	private Handler callback=new  Handler(){
		
	    public void handleMessage(Message msg) {
	    	
	        Object[] pictute = (Object[]) msg.obj;  
	    	
	    	String url=(String)pictute[0];
	    	urlArray.add(url);
	    	       
	    	Bitmap bitmap  = (Bitmap) pictute[1];
	    	
	    	if (url.equals(theme.getECardImage())) bitmapArray[0]=bitmap;
	    	else if (url.equals(theme.getSquareImage())) bitmapArray[1]=bitmap;
	    	else if (url.equals(theme.getPostCardBackImage())) bitmapArray[2]=bitmap;
	    	
	    	if (loadDialog!=null) {
	    		if (isAlreadyLoaded()) {
	    				startShareActivity();
	    				loadDialog.dismiss();
	    				loadDialog=null;
	    		}
	    		
	    	}				
		};
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.done, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:{
			createDialog();
			return true;
		}
		case R.id.action_done: {
			card.setText(postcard.getText());
			if (isAlreadyLoaded()) startShareActivity();
			else {
				loadDialog = new ProgressDialog(activity);
				loadDialog.setMessage(getString(R.string.load_previevs));
				loadDialog.setCancelable(true);
				loadDialog.show();
				return true;}
		}
		default: {
		}
			return false;
		}
	}
	
	
//*******************************************************************************
	
	//GET THEMES FROM DB OR NETWORK
	private void getThemeExtra(){
		// Theme ID from Extras
			if (project == null)
				themePurchaseId = getIntent().getExtras().getString(Constants.EXTRA_PURCHASE_ID);
			else {
				themePurchaseId = project.getThemeId();
				restorePostcard();
					}
			theme = dbHelper.getThemeByPurchaseId(themePurchaseId);
			if (theme==null){
				getThemeFromNetwork();
			}
			else themeIsLoaded();
	}
	
	private OnCancelListener cancelDialogListener=new OnCancelListener() {
		
		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			dialog.dismiss();
			activity.finish();
		}
	};
	
	private void initializeLoadDialog(){
		themeLoadProgress=new ProgressDialog(activity);
		themeLoadProgress.setMessage(getString(R.string.loading_postcards));			
		themeLoadProgress.setOnCancelListener(cancelDialogListener);
		themeLoadProgress.show();
	}
	
	ProgressDialog themeLoadProgress;

	private void getThemeFromNetwork(){
		final Api api = Api.getInstanse(activity);
		String purchaseId;
			if (project==null) 
				purchaseId=getIntent().getExtras().getString(Constants.EXTRA_PURCHASE_ID);
			else 
				purchaseId=project.getThemeId();
		
		final String themeId=purchaseId;
		new AsyncTask<Void, Theme, Theme>(){
			
			protected void onPreExecute() {
				initializeLoadDialog();
			};

			@Override
			
			protected Theme doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					theme=(Theme) api.getPurchase(themeId, ""+Constants.PURCHASE_TYPE_THEME_ID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return theme;
			}
			
			protected void onPostExecute(Theme result) {
				if (theme!=null) {
					dbHelper.saveTheme(theme);
					themeLoadProgress.dismiss();
					themeIsLoaded();
				}
				else {
					EditorActivity.this.finish();
				}
				
			};
		}.execute();

	}
	
	private void themeIsLoaded(){
//		createAppealFragment();
//		createAppealList();

		createPostcardsFragment();

		restoreRecentlyUsedTexts();
		createTextPackFragment();

		createTopPanelFragments();
		initializeUI();
	}
	
	//*****************************
	
	//SAVED PROJECTS***********************************************************
	private void getProjectExtra() {
		String projectId = getIntent().getStringExtra(Constants.EXTRA_PROJECT_ID);
		project = dbHelper.getProjectById(projectId);
	}
	
	private void restorePostcard(){
		String text=project.getText();
		card.setText(text);
//		if (project.getAppealsId()!=null){
//			card.setStateAppeal(Constants.OPEN);
//			card.setAppeal(dbHelper.getAppealById(project.getAppealsId()));
//		}
		if (//project.getSignatureText()!=null||
				project.getSignatureBitmapUri()!=null){
//			card.setStateSignature(Constants.OPEN);
//			card.setSignature(project.getSignatureText());
			restoreSignature();
		}		
	}
	

	
	private void restoreSignature(){
//		String filename=project.getSignatureBitmapUri();
//		ImageManager manager=new ImageManager(activity);
//		
//		Bitmap signature=manager.decodeBitmapFromFile(manager.createFileNameFromUrl(filename));
//		card.setVerticalSignature(signature);
//		
//		Bitmap printSignature=manager.decodeBitmapFromFile(manager.createFileNameFromUrl(filename));
//		int width=signature.getWidth();
//		int height=signature.getHeight();
//		
////		int origPixels[]=new int[width*height];
////		signature.getPixels(origPixels, 0,width, 0, 0,width,height);
////		
////		int changePixels[]=new int[width*height];
////		
////		for (int i=0;i<origPixels.length;i++){
//////			changePixels[i]=Color.rgb(origPixels[i],0,0,0);
////		}
////		
////		printSignature.setPixels(origPixels, 0,width, 0, 0,width,height);
//		
//		card.setPrintSignature(printSignature);
	}

	//*************************************************************************
	
	//APPEAL*******************************************************************
//	private void createAppealList(){
//		final Appeal appeal=new Appeal(Constants.WITHOUT_APPEAL," "," ");
//		
//		final DataBaseHelper dbHelper = DataBaseHelper.getInstance(activity);
//		appeal_list = dbHelper.getAppealsList();
//		appeal_list.add(0, appeal);
//		
//		if (appeal_list.isEmpty()){
//			dialog = new ProgressDialog(activity);
//			dialog.setMessage("Loading items");
//			dialog.setCancelable(false);
//			dialog.show();
//		}else{
//			appeal_fragment.setAppealList(appeal_list);
//		}
//		Utils.checkForUpdate(activity, Constants.VERSIONS_APPEALS, new Handler.Callback() {
//			@Override
//			public boolean handleMessage(Message msg) {
//				if (msg.what == 1){
//					appeal_list = dbHelper.getAppealsList();
//					appeal_list.add(0, appeal);
//					if (dialog != null){
//						dialog.dismiss();
//						appeal_fragment.setAppealList(appeal_list);		
//					}else 
//						appeal_fragment.setAppealList(appeal_list);
//				}
//				return false;
//			}
//		});
//	}
//
//	private void createAppealFragment(){
//		// Обращения
//		appeal_fragment = new AppealFragment();
//		setAppealClickListener();
//		
//	}
//	
//	private void setAppealClickListener(){
//		appeal_fragment.setOnItemAppealClick(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position,
//					long ID) {
//				// TODO Auto-generated method stub
//				Appeal appeal=appeal_list.get(position);
//				postcard.setAppeal(appeal);
//			}
//		});
//	}
	
	//***************************************************************************
	
	//RECENTLY_USED TEXTS********************************************************
	public void restoreRecentlyUsedTexts() {
		// restore preferences with recently_used text in onCreate Activity
		prefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		recently_used.clear();
		int size = prefs.getInt(Constants.RECENTLY_USED_ARRAY_SIZE, 0);
		for (int i = 0; i < size; i++) {
			recently_used.add(prefs.getString(
					Constants.RECENTLY_USED_TEXT_NUMBER + i, " "));
		}
	}
	
	private void addToRecentlyUsed() {
		// Save to recently used texts
		String text=card.getText();
		
		if (text!=null) {
			text=text.trim();
			if (text.length()>0)
				recently_used.add(0, card.getText());
			}

		if (recently_used.size() > MAXIMUM_TEXTS)
			recently_used.remove(MAXIMUM_TEXTS);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(Constants.RECENTLY_USED_ARRAY_SIZE, recently_used.size());

		for (int i = 0; i < recently_used.size(); i++) {
			editor.putString(Constants.RECENTLY_USED_TEXT_NUMBER + i,
					recently_used.get(i));
		}
		editor.commit();
	}
	//*****************************************************************************
	
	//METHODS SOFTKEYBOARD
	private void initializeUI() {

		bottom_fragment_container=(FrameLayout) findViewById(R.id.bottom_fragment_container);		 
		bottom_fragment_container.setLayoutParams(bottom_close_params);
		// Определение видимости экранной клавиатуры
		final View activityRootView = findViewById(android.R.id.content);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					private int heightDiff;

					public void onGlobalLayout() {
						Rect r = new Rect();
						// создаём прямоугольник r с координатами видимого
						// пространства
						activityRootView.getWindowVisibleDisplayFrame(r);

						// Вычисляем разницу между высотой нашего View и высотой
						// видимого пространства
						heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);

						System.out.println("*****************************************");
						System.out.println(postcard.getEditableMode());
						
						if (postcard.getEditableMode()) 
							if (formButtonsFragment.isVisible()) 
								showEditButtons();
	
						// значит открыта экранная клавиатура
						if (heightDiff > 128) {
							keyBoardHeight=(heightDiff/2)+50;
							onKeyBoardShow();
						}
						// значит экранная клавиатура скрыта
						else {
							onKeyBoardHide();
						}
						}
				}
					);
	}
	
		private void onKeyBoardShow(){
			fragment_postcard.scrollTo(0, keyBoardHeight);
			bottom_fragment_container.setVisibility(View.INVISIBLE);
//			if (appeal_fragment.isVisible()) openTextPackFragment();
		}
	
		private void onKeyBoardHide(){
			fragment_postcard.scrollTo(0, delta);
			bottom_fragment_container.setVisibility(View.VISIBLE);
		}
		
		
	//POSTCARD FRAGMENTS***********************************************************
	private void createPostcardsFragment(){
		postcard = PostCardFragment.newInstance(activity, card, theme);

		vertical = new PostCardFragmentVertical();
		square =  new PostCardFragmentSquare();
		print =  new PostCardFragmentPrint();

		int selected_postcard = getIntent().getIntExtra(Constants.EXTRA_POSTCARD_SELECTED, Constants.POSTCARD_VERTICAL);
		card.setForm(selected_postcard);
		
		switch (selected_postcard) {
		case Constants.POSTCARD_VERTICAL:
			postcard = vertical;
			loadLargeBitmap(theme.getECardImage());
			break;
		case Constants.POSTCARD_SQUARE:
			postcard = square;
			loadLargeBitmap(theme.getSquareImage());
			break;
		case Constants.POSTCARD_HORIZONTAL:
			postcard = print;
			loadLargeBitmap(theme.getPostCardFrontImage());
			loadLargeBitmap(theme.getPostCardBackImage());
			break;
		}
		
		// Container for PostCardFragments
		fragment_postcard = (LinearLayout) findViewById(R.id.fragment_postcard);
		
		fragment_postcard.setLayoutParams(postcard_params);

		fm = getSupportFragmentManager().beginTransaction();
		fm.setTransition(FragmentTransaction.TRANSIT_NONE);
		fm.add(R.id.fragment_postcard, postcard);
		fm.commit();
		

	}

	//PostCardFragmentListenerEvents
	@Override
	public void onPostCardTouchEvent() {
		//postcard.setFocus();
		showEditButtons();
	}
	
//	@Override
//	public void onAppealClickEvent() {
//		// TODO Auto-generated method stub
//		openAppealFragment();
//	}

	@Override
	public void onStartSignatureActivityEvent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(activity, SignatureActivity.class);
		intent.putExtra(Constants.RED, theme.getTextColorRed());
		intent.putExtra(Constants.GREEN, theme.getTextColorGreen());
		intent.putExtra(Constants.BLUE, theme.getTextColorBlue());
		intent.putExtra(Constants.EXTRA_POSTCARD_SELECTED,card.getForm());
		startActivityForResult(intent, REQUEST_CODE_SIGNATURE);
	}
	
	@Override
	public void onCloseEditorModeEvent() {
		showFormButtons();
//		if (appeal_fragment.isVisible())
//			openTextPackFragment();
	}
	
	//*****************************************************************************
	
	
	//TextPacksFragment***********************************************************
	
	private void createTextPackFragment(){
		// фрагменты с поздравительными текстами
		final String TAG="TEXT_PACK";
		textPackFragment = TextPackFragment.newInstance(activity, false, theme);
		TextPackFragment.clearTextPackList();
		
		fm = getSupportFragmentManager().beginTransaction();
		fm.add(R.id.bottom_fragment_container, textPackFragment);
		fm.addToBackStack(TAG);
		fm.commit();
	
		textPackFragment.setOnOpenClickListener(onOpenClickListener());
	}

	
	private OnClickListener onOpenClickListener(){
		OnClickListener onOpenClickListener=new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openTextPackFragment();
				fragment_postcard.scrollTo(0, delta);
			}
		};
		return onOpenClickListener;
	}
	
	private OnClickListener onCloseClickListener(){
		OnClickListener onCloseClickListener=new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeTextPackFragment();
			}
		};
		return onCloseClickListener;
	}
	
	private OnItemClickListener onTextPackListener(){

		OnItemClickListener onTextPackClickListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long ID) {
				// TODO Auto-generated method stub
				final String TAG="TEXT";
				
				TextPack pack=textPackFragment.getListTextPack().get(position);
				textFragment = TextFragment.newInstance(activity,pack.getName(),position,pack.getUuid());
				
				
				textFragment.setOnReturnToTextPackClickListener(onReturnToTextPackClickListener());
				textFragment.setOnSetTextClickListener(onSetTextClickListener());

				bottom_fragment_container.setLayoutParams(bottom_open_params);
				
				if(pack.isBought()||pack.getPrice()==0) openTexts();
				else openTextDialog(pack);
			}
		};
		return onTextPackClickListener;
	}
	
	private void openTexts(){
		fm = getSupportFragmentManager().beginTransaction();
		fm.setCustomAnimations(R.anim.translate_from_left,R.anim.translate_to_left,R.anim.translate_from_right,
				R.anim.translate_to_right);
		fm.replace(R.id.bottom_fragment_container, textFragment);
		fm.addToBackStack(TAG);
		fm.commit();
	}
	private void openTextDialog(final TextPack textPack){
		textDialog=TextDialogFragment.newInstance(activity, textPack);
		textDialog.show(getSupportFragmentManager(), TAG);
		textDialog.setOnBuyClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Purchaser(activity, textCallback, textPack, ""+Constants.PURCHASE_TYPE_TEXT_ID, textDialog).buyArticle();
			}
		});
	}


	private void openTextPackFragment(){
		final String TAG="OPEN";
		
		delta=bottom_open_params.height-bottom_close_params.height;
		fragment_postcard.scrollTo(0, delta);
		
		bottom_fragment_container.setLayoutParams(bottom_open_params);
		textPackFragment = TextPackFragment.newInstance(activity, true, theme);
		textPackFragment.setOnTextPackClickListener(onTextPackListener());
		textPackFragment.setOnCloseClickListener(onCloseClickListener());
		fm = getSupportFragmentManager().beginTransaction();
		fm.replace(R.id.bottom_fragment_container, textPackFragment);
		fm.addToBackStack(TAG);
		fm.commit();
	}
	
	private void closeTextPackFragment(){
		final String TAG="CLOSE";		
		
		delta=0;
		fragment_postcard.scrollTo(0, delta);
		
		bottom_fragment_container.setLayoutParams(bottom_close_params);
		textPackFragment = TextPackFragment.newInstance(activity, false, theme);
		textPackFragment.setOnOpenClickListener(onOpenClickListener());
		fm = getSupportFragmentManager().beginTransaction();
		fm.replace(R.id.bottom_fragment_container, textPackFragment);
		fm.addToBackStack(TAG);
		fm.commit();
	}
	//*****************************************************************************
	
	//TextFragment*****************************************************************
	private OnClickListener onReturnToTextPackClickListener(){
		OnClickListener onReturnToTextPackClickListener=new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fm = getSupportFragmentManager().beginTransaction();
				fm.setCustomAnimations(R.anim.translate_from_right,
						R.anim.translate_to_right);
				fm.replace(R.id.bottom_fragment_container, textPackFragment);
				fm.commit();
			}
		};
		return onReturnToTextPackClickListener;
	}
	private OnItemClickListener onSetTextClickListener(){
		OnItemClickListener onSetTextClickListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long ID) {
				// TODO Auto-generated method stub
				postcard.setTextPostCard(textFragment.getListText().get(position).getTextString());
			}
		};
		return onSetTextClickListener;
	}

	
	//*****************************************************************************	
	
	//TopPanelFragments************************************************************
	private void createTopPanelFragments(){
//		editButtonsFragment = new PostcardEditButtonsFragment();
		formButtonsFragment = new PostcardFormButtonsFragment();
		
		
		fm = getSupportFragmentManager().beginTransaction();
		fm.setTransition(FragmentTransaction.TRANSIT_NONE);
		fm.add(R.id.top_fragment_container, formButtonsFragment);
		fm.commit();
		
		formButtonsFragment.setSelectedPostcard(selected_postcard);

//		editButtonsFragment.setEditableFragment(postcard);
//		editButtonsFragment.setAppealClickListener(onButtonAppealClickListener());
//		editButtonsFragment.setSignatureClickListener(onButtonSignatureClickListener());
	}
	
	private void showEditButtons() {
//		if (postcard==vertical)	{
//			postcard.onEditableMode();
//			fm = getSupportFragmentManager().beginTransaction();
//			fm.setCustomAnimations(R.anim.alpha_plus, R.anim.alpha_minus);
//			fm.replace(R.id.top_fragment_container, editButtonsFragment);
//			fm.commit();
//			}
//		else{
		fm = getSupportFragmentManager().beginTransaction();
		fm.setCustomAnimations(R.anim.alpha_plus, R.anim.alpha_minus);
		fm.hide(formButtonsFragment);
		fm.commit();
//			} 
		if (postcard==print) postcard.ZoomText();
	}

	private void showFormButtons() {
//		if(postcard==vertical)
//		{			
//			fm = getSupportFragmentManager().beginTransaction();
//			fm.replace(R.id.top_fragment_container, formButtonsFragment);
//			fm.commit();	
//			}
//		else
//		{
			fm = getSupportFragmentManager().beginTransaction();
			fm.show(formButtonsFragment);
			fm.commit();
//			}
	}
	
	@Override
	public void onFormChangeEvent(int form) {
		// TODO Auto-generated method stub
		switch (form) {
		case Constants.POSTCARD_VERTICAL:
			replacePostcards(vertical);
			card.setForm(Constants.POSTCARD_VERTICAL);
			loadLargeBitmap(theme.getECardImage());
			break;
		case Constants.POSTCARD_SQUARE:
			replacePostcards(square);
			card.setForm(Constants.POSTCARD_SQUARE);
			loadLargeBitmap(theme.getSquareImage());
			break;
		case Constants.POSTCARD_HORIZONTAL:
			replacePostcards(print);
			card.setForm(Constants.POSTCARD_HORIZONTAL);
			loadLargeBitmap(theme.getPostCardFrontImage());				
			loadLargeBitmap(theme.getPostCardBackImage());
			break;
		}
	}
	
//	private OnClickListener onButtonAppealClickListener(){
//		OnClickListener onAppealClickListener = new OnClickListener() {		
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				switch (postcard.getStateAppeal()) {
//				case Constants.OPEN:
//					postcard.setStateAppeal(Constants.CLOSE);
////					editButtonsFragment
////							.setButtonTitleBackground(Constants.ADD);
//					break;
//				case Constants.CLOSE:
//					postcard.hideSoftInput();
////					openAppealFragment();
//					postcard.setStateAppeal(Constants.OPEN);
////					editButtonsFragment
////							.setButtonTitleBackground(Constants.DELETE);
//				}
//				
//			}
//		};
//		return onAppealClickListener;
//	}
//	
//	private OnClickListener onButtonSignatureClickListener(){
//		OnClickListener onSignatureClickListener=new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				switch (postcard.getStateSignature()) {
//				case Constants.OPEN:
//					postcard.setStateSignature(Constants.CLOSE);
//					editButtonsFragment
//							.setButtonSignatureBackground(Constants.ADD);
//					break;
//				case Constants.CLOSE:
//					onStartSignatureActivityEvent();
//					postcard.setStateSignature(Constants.OPEN);
//					editButtonsFragment
//							.setButtonSignatureBackground(Constants.DELETE);
//					break;
//				}
//			}
//		};
//		return onSignatureClickListener;
//	}

	//*****************************************************************************



	private void startShareActivity() {
		
		addToRecentlyUsed();
		postcard.savePostcard();
		switch (card.getForm()) {
		case Constants.POSTCARD_VERTICAL: {
			Intent intent = new Intent(activity, HubActivity.class);
			
			ImageManager manager=new ImageManager(activity);
			
			String filename=manager.createFileNameFromUrl(theme.getECardImage());

			Bitmap vBitmap=((PostCardFragmentVertical)postcard).getVerticalPostcard(bitmapArray[0]);
			manager.saveImageToInternalStorage(filename, vBitmap, Bitmap.CompressFormat.JPEG);
			

			intent.putExtra(Constants.POSTCARD_FRONT_IMAGE, filename);

			startActivity(intent);
			break;
		}
		case Constants.POSTCARD_SQUARE: {
			Intent intent = new Intent(activity, HubActivity.class);
			
//<<<<<<< HEAD
//
//			ByteArrayOutputStream pictureStream = new ByteArrayOutputStream();
//			bitmapArray[1].compress(Bitmap.CompressFormat.JPEG, 100, pictureStream);
//<<<<<<< HEAD
//			byte[] picture = pictureStream.toByteArray();
//			Log.i(TAG, "startShareActivity picture "+Arrays.toString(picture));
//			intent.putExtra(Constants.POSTCARD_FRONT_IMAGE, picture);
//=======
//=======
			intent.putExtra(Constants.POSTCARD_TEXT, card.getText());
//>>>>>>> d6ac9b8e1b96be1850d938c5a05ea3b1b366bfae
			
			ImageManager manager=new ImageManager(activity);
			String filename=manager.createFileNameFromUrl(theme.getSquareImage());
			
			intent.putExtra(Constants.POSTCARD_FRONT_IMAGE, filename);
//>>>>>>> 14903368d70beaa9e640863537df0639b7b98f59
			
			startActivity(intent);
			break;
		}
		case Constants.POSTCARD_HORIZONTAL: {
			Intent intent = new Intent(activity, PrintPdfActivity.class);
			intent.putExtra(Constants.EXTRA_PURCHASE_ID, theme.getPurchaseId());
			intent.putExtra(Constants.POSTCARD_TEXT, card.getText());
//<<<<<<< HEAD
//			if (card.getAppeal()==null){
//				String appeal_id=null;
//				intent.putExtra(Constants.POSTCARD_TITLE_TEXT, appeal_id);
//			}
//			else intent.putExtra(Constants.POSTCARD_TITLE_TEXT, card.getAppeal().getUuid());
//			intent.putExtra(Constants.POSTCARD_SIGNATURE_TEXT, card.getSignature());
//			ByteArrayOutputStream stream = new ByteArrayOutputStream();
//			if (card.getSignatureImage() != null)
//				card.getSignatureImage().compress(Bitmap.CompressFormat.PNG, 0,	stream);
//
//			intent.putExtra(Constants.POSTCARD_SIGNATURE_IMAGE,stream.toByteArray());
//
//			bitmapArray[0] = null;
//			bitmapArray[1] = null;

//=======
			bitmapArray[0]=null;
			bitmapArray[1]=null;
//>>>>>>> 14903368d70beaa9e640863537df0639b7b98f59
			urlArray.remove(theme.getECardImage());
			urlArray.remove(theme.getSquareImage());
			
			ImageManager manager=new ImageManager(activity);
			
			Bitmap hBitmap=((PostCardFragmentPrint)postcard).getHorisontalBitmap(bitmapArray[2]);
			String outputFilePath=Long.toString(System.currentTimeMillis());
			
			manager.saveImageToInternalStorage(outputFilePath, hBitmap, Bitmap.CompressFormat.JPEG);
			
			intent.putExtra(Constants.POSTCARD_BACK_IMAGE, outputFilePath);
			
			intent.putExtra(Constants.EXTRA_EVENT_ID, getEventId());
			startActivity(intent);
			break;
		}

		default:
			break;
		}
	}
	
	class CustomDialogClass extends Dialog {

		public Context context;

		public CustomDialogClass(Context context) {
			super(context);
			this.context = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_test_card);
			
			ImageManager manager=new ImageManager(activity);
			String filename=manager.getFilepathFromUrl(theme.getECardImage());

			final ImageView image = (ImageView) findViewById(R.id.iv_icon);

			image.setImageBitmap(
					((PostCardFragmentVertical)postcard).getVerticalPostcard(bitmapArray[0]));

//			image.setOnClickListener(new android.view.View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (signatureBitmap != null)
//						if (image.getDrawable().equals(signatureBitmap))
//							image.setImageBitmap(formPostcardBitmap("test", 1));
//						else
//							image.setImageBitmap(signatureBitmap);
//				}
//			});
		}
	}

	private String getEventId() {
		Intent intent = getIntent();
		return intent.getStringExtra(Constants.EXTRA_EVENT_ID);
	}


	private boolean isAlreadyLoaded(){
			
		switch (card.getForm()){
			case Constants.POSTCARD_VERTICAL:
				return (urlArray.contains(theme.getECardImage()));
			
			case Constants.POSTCARD_SQUARE:
				return (urlArray.contains(theme.getSquareImage()));
				
			case Constants.POSTCARD_HORIZONTAL:
				return (urlArray.contains(theme.getPostCardFrontImage())
						&&urlArray.contains(theme.getPostCardBackImage()));
		}
		return false;
		
	}


	//*****************************************************************************

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_CODE_SIGNATURE) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				byte[] byteArrayV = extras.getByteArray(Constants.SIGNATURE_VERTICAL);
				Bitmap signVertical = BitmapFactory.decodeByteArray(byteArrayV, 0,
						byteArrayV.length);
				signVertical = Bitmap.createBitmap(signVertical);
				
				card.setVerticalSignature(signVertical);
				
				byte[] byteArrayP = extras.getByteArray(Constants.SIGNATURE_PRINT);
				Bitmap signPrint = BitmapFactory.decodeByteArray(byteArrayP, 0,
						byteArrayP.length);
				signPrint = Bitmap.createBitmap(signPrint);
				
				card.setPrintSignature(signPrint);
			}
		}
	}

	@Override
	public void onBackPressed() {
		createDialog();
	}
	
//Fragment Transaction******************************************************************
//	private void openAppealFragment(){
//
//		bottom_fragment_container.setLayoutParams(bottom_open_params);
//		delta=bottom_open_params.height-bottom_close_params.height;
//		
//		fm = getSupportFragmentManager().beginTransaction();
//		fm.replace(R.id.bottom_fragment_container,appeal_fragment);
//		fm.commit();
//	}

	private final void replacePostcards(Fragment fragment) {
		if (fragment.isVisible())
			return;
		fm = getSupportFragmentManager().beginTransaction();
		fm.setCustomAnimations(R.anim.alpha_plus, R.anim.alpha_minus);
		fm.replace(R.id.fragment_postcard, fragment);
		fm.commit();
		postcard = (PostCardFragment) fragment;
//		editButtonsFragment.setEditableFragment(postcard);
	}

//*****************************************************************************

	//LoadImages
	private void loadLargeBitmap(String url) {
		if (!isAlreadyLoaded()){
			BitmapLoaderAsyncTask loader;
			if (url==theme.getPostCardFrontImage()||url==theme.getSquareImage()){
			loader = new BitmapLoaderAsyncTask(activity,	callback, false, true);
			}
			else 
			loader = new BitmapLoaderAsyncTask(activity,	callback, false, false);
			loader.loadImageAsync(url, null, null);
			}
	}

	//LAYOUT PARAMS
	private void createLayoutParams(){
		 bottom_close_params=new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, ScreenParams.screenHeight/12);
		 bottom_close_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 
		 bottom_open_params=new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, ScreenParams.screenHeight/3); 
		 
		 bottom_open_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 
		 postcard_params=new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		 
		 postcard_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		 postcard_params.topMargin=5;
		 

	 
	}


	private void createDialog(){
		AlertDialog.Builder builder=new Builder(activity);
		builder.setTitle(null);
		builder.setMessage(getResources().getString(R.string.finish_dialog_message));
		builder.setPositiveButton(R.string.str_yes, new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				activity.finish();
			}
		});
		
		builder.setNegativeButton(R.string.str_no, new android.content.DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		AlertDialog finishDialog=builder.create();
		finishDialog.show();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (callback!=null) callback.removeCallbacksAndMessages(null);
		if (textCallback!=null) textCallback.removeCallbacksAndMessages(null);
	}

}
