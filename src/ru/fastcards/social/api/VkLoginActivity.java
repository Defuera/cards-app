
package ru.fastcards.social.api;


import ru.fastcards.R;
import ru.fastcards.utils.Account;
import ru.fastcards.utils.Constants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class VkLoginActivity extends Activity{

	    private static final String TAG = "Kate.LoginActivity";

	    /**
		 * @uml.property  name="webview"
		 * @uml.associationEnd  
		 */
	    WebView webview;
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.login);
	        
	        webview = (WebView) findViewById(R.id.webview);
	        webview.getSettings().setJavaScriptEnabled(true);
	        webview.clearCache(true);
	        
	        //Чтобы получать уведомлени�? об окончании загрузки �?траницы
	        webview.setWebViewClient(new VkontakteWebViewClient());
	                
	        //otherwise CookieManager will fall with java.lang.IllegalStateException: CookieSyncManager::createInstance() needs to be called before CookieSyncManager::getInstance()
	        CookieSyncManager.createInstance(this);
	        
	        CookieManager cookieManager = CookieManager.getInstance();
	        cookieManager.removeAllCookie();
	        
	        String url = Auth.getVkUrl(Constants.API_ID, Auth.getSettings());
	        webview.loadUrl(url);
	    }
	    
	    class VkontakteWebViewClient extends WebViewClient {
	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	            super.onPageStarted(view, url, favicon);
	            parseUrl(url);
	        }
	    }
	    
	    private void parseUrl(String url) {
	        try {
	            if(url==null)
	                return;
	            Log.i(TAG, "url="+url);
	            if(url.startsWith(Auth.redirect_url))
	            {
	                if(!url.contains("error=")){
	                    String[] auth=Auth.parseRedirectUrl(url);
	                    Intent intent=new Intent();
	                    Account account = Account.getInstance();
	    				account.setVkontakteToken(auth[0]);
	    				account.setVkontakteUserId(auth[1]);
	    				account.save(this);
	                    
	                    setResult(Activity.RESULT_OK, intent);
	                }
	                finish();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	

	
}

