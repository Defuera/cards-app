//package ru.fastcards;
//
//import java.util.List;
//
//import ru.fastcards.utils.Constants;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore.Images;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.CompoundButton;
//import android.widget.Toast;
//
//public class PostSocialActivity extends TrackedActivity{
//
//	private Context context;
//	private String TAG = "PostSocialActivity";
//
//	private CompoundButton insCheck;
//	private CompoundButton twCheck;
//	private Bitmap postcardBitmap;
//	private boolean sended;
//	
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		context = this;
//		setContentView(R.layout.activity_post);
//		
//		getExtras();
//		
//		insCheck = (CompoundButton) findViewById(R.id.insta_switch);
//		twCheck = (CompoundButton) findViewById(R.id.twitter_switch);
//		
////		AccountManager accountManager = 
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.home, menu);
//		return true;
//	}
//
//
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		switch (item.getItemId()){
//			case R.id.action_home:
//				startMainActivity();
//				break;
//		}
//		return true;
//	}
//	
//	private void startMainActivity(){
//		Intent intent=new Intent(context, MainActivity.class);
//    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
//	}
//
//	private void getExtras() {
//		Intent intent = getIntent();
//		String text = intent.getStringExtra(Constants.POSTCARD_TEXT);
//		byte[] postcardUrl = intent.getByteArrayExtra(Constants.POSTCARD_FRONT_IMAGE);
//
//		postcardBitmap = BitmapFactory.decodeByteArray(postcardUrl, 0 , postcardUrl.length);
//		
//		Log.d(TAG, "text "+text);
//		Log.d(TAG , "postcardUrl "+postcardUrl);
//		
//	}
//	
//	public void onPostButtonClick(View v){
//		sended=true;
//		if (insCheck.isChecked())
//			postToInst();
//		if (twCheck.isChecked())
//			postToTw();
//		else if (!twCheck.isChecked()&&!insCheck.isChecked()) 
//		{
//			Toast.makeText(context, getString(R.string.str_soc_net_not_choosed), Toast.LENGTH_LONG).show();
//			sended=false;
//		}
//	}
//
//	private void postToTw() {
//
//		String pathofBmp = Images.Media.insertImage(getContentResolver(), postcardBitmap,"test", null);
//	    Uri bmpUri = Uri.parse(pathofBmp);
//	    
//	    Intent tweetIntent = new Intent(Intent.ACTION_SEND);
//	    tweetIntent.putExtra(Intent.EXTRA_TEXT, "This is a Test.");
//	    tweetIntent.setType("text/plain");
//        tweetIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//
//	    PackageManager packManager = getPackageManager();
//	    List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);
//
//	    boolean resolved = false;
//	    for(ResolveInfo resolveInfo: resolvedInfoList){
//	        if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
//	            tweetIntent.setClassName(
//	                resolveInfo.activityInfo.packageName, 
//	                resolveInfo.activityInfo.name );
//	            resolved = true;
//	            break;
//	        }
//	    }
//	    if(resolved){
//	        startActivity(tweetIntent);
//	    }else{
//	        Toast.makeText(this, "Twitter app isn't found", Toast.LENGTH_LONG).show();
//	    }
//	}
//
//	private void postToInst() {
//		String pathofBmp = Images.Media.insertImage(getContentResolver(), postcardBitmap,"test", null);
//	    Uri bmpUri = Uri.parse(pathofBmp);
//	    
//	    Intent intent =new Intent(Intent.ACTION_SEND);
//	    intent.setType("image/jpeg");
//        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
//
//	    PackageManager packManager = getPackageManager();
//	    List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(intent,  PackageManager.MATCH_DEFAULT_ONLY);
//
//	    boolean resolved = false;
//	    for(ResolveInfo resolveInfo: resolvedInfoList){
//	        if(resolveInfo.activityInfo.packageName.startsWith("com.instagram.android")){
//	            intent.setClassName(
//	                resolveInfo.activityInfo.packageName, 
//	                resolveInfo.activityInfo.name );
//	            resolved = true;
//	            break;
//	        }
//	    }
//	    if(resolved){
//	        startActivity(intent);
//	    }else{
//	        Toast.makeText(this, "Instagram app isn't found", Toast.LENGTH_LONG).show();
//	    }
//		
//	}
//	
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		if (sended) startMainActivity();
//		else super.onBackPressed();
//	}
//}
