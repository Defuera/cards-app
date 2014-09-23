package ru.fastcards.social.api;
//package com.example.social.api;
//
//
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.webkit.CookieManager;
//import android.webkit.CookieSyncManager;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//public class InstaLoginDialog extends Dialog {
//
//	private static final FrameLayout.LayoutParams FILL_LAYOUT_PARAMS = new FrameLayout.LayoutParams(
//			ViewGroup.LayoutParams.MATCH_PARENT,
//			ViewGroup.LayoutParams.MATCH_PARENT);
//	static final float[] LANDSCAPE = { 460, 260 };
//	static final float[] PORTRAIT = { 380, 460 };
//	
//	private ProgressDialog mProgressDialog;
//	private LinearLayout linearLayout;
//	private TextView textView;
//	private WebView webView;
//	static final int margin = 4;
//	static final int padding = 2;
//	private String url;
//	private AuthDialogListener mAuthDialogListener;
//	private boolean isShowing = false;
//	
//	
//	@SuppressLint("SetJavaScriptEnabled")
//	@Override
//	protected void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		linearLayout = new LinearLayout(getContext());
//		linearLayout.setOrientation(LinearLayout.VERTICAL);
//
//		mProgressDialog = new ProgressDialog(getContext());
//		mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		textView = new TextView(getContext());
//		textView.setText("Instagram Login");
//		textView.setTextColor(Color.WHITE);
//		textView.setTypeface(Typeface.DEFAULT_BOLD);
//		textView.setBackgroundColor(Color.BLACK);
//		textView.setPadding(margin+padding, margin, margin, margin);
//		linearLayout.addView(textView);
//		
//		webView = new WebView(getContext());
//		webView.setVerticalScrollBarEnabled(false);
//		webView.setHorizontalScrollBarEnabled(false);
//		webView.setWebViewClient(new AuthWebViewClient());
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setSavePassword(false);
//		webView.loadUrl(url);
//		webView.setLayoutParams(FILL_LAYOUT_PARAMS);
//		
//		linearLayout.addView(webView);
//		
//		float scale = getContext().getResources().getDisplayMetrics().density;
//		DisplayMetrics displaymetrics = new DisplayMetrics();
//		getWindow().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//		int height = displaymetrics.heightPixels;
//		int width = displaymetrics.widthPixels;
//		
//		float[] dimensions = (width < height) ? PORTRAIT : LANDSCAPE;
//		
//		addContentView(linearLayout, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1] * scale + 0.5f)));
//		
//		CookieSyncManager.createInstance(getContext());
//		CookieManager cookieManager = CookieManager.getInstance();
//		cookieManager.removeAllCookie();
//		
//	}
//	
//	public class AuthWebViewClient extends WebViewClient
//	{
//
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//			if (url.startsWith(InstaImpl.CALLBACKURL)) {
////				System.out.println(url);
//				String urls[] = url.split("=");
//				mAuthDialogListener.onComplete(urls[1]);
//				InstaLoginDialog.this.hide();
//				InstaLoginDialog.this.dismiss();
//				return true;
//			}
//			return false;
//		}
//		   @Override
//		    public void onLoadResource(WebView  view, String  url){
////		        hide();
//		        dismiss();
//		        }
//
//		@Override
//		public void onPageFinished(WebView view, String url) {
//			super.onPageFinished(view, url);
//			
//			System.out.println("onPageFinished");
//			String title = webView.getTitle();
//			if (title != null && title.length() > 0) {
//				textView.setText(title);
//			}
//			Log.d("Insta Login Dialog", "On Page Finished URL: " + url);
//			Log.e("PAGE FINISHED", "Called");
//			InstaLoginDialog.this.dismiss();
//		}
//
//		@Override
//		public void onPageStarted(WebView view, String url, Bitmap favicon) {
//			super.onPageStarted(view, url, favicon);
//			if (!isShowing) {
//				showDialog("Loading..");
//			}
//			
//		}
//
//		@Override
//		public void onReceivedError(WebView view, int errorCode,
//				String description, String failingUrl) {
//			super.onReceivedError(view, errorCode, description, failingUrl);
//			InstaLoginDialog.this.dismiss();
//			mAuthDialogListener.onError(description);
//		}
//		
//	}
//	
//	public InstaLoginDialog(Context context, String url, AuthDialogListener listener) {
//		super(context);
//		this.url = url;
//		this.mAuthDialogListener = listener;
//	}
//	
//	public interface AuthDialogListener
//	{
//		public void onComplete(String token);
//		public void onError(String error);
//	}
//	
//	public void showDialog(String message)
//	{
//		mProgressDialog.setMessage(message);
//		mProgressDialog.show();
//	}
//	
//	public void dismissDialog() {
//		if (mProgressDialog.isShowing()) {
//			mProgressDialog.dismiss();
//		}
//	}
//
//}
