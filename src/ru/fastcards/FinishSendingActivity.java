package ru.fastcards;

import java.util.UUID;

import ru.fastcards.common.Theme;
import ru.fastcards.utils.BitmapLoaderAsyncTask;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class FinishSendingActivity extends TrackedActivity {

	private FinishSendingActivity context;
	private String[] recIdsArray;
	private Theme theme;
	private DataBaseHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_finish_sending);
		
		context = this;	
		dbHelper =DataBaseHelper.getInstance(context);

		getExtras();

		initializeUi();
	}
	
	private void initializeUi() {		
		Button saveListBtn = (Button) findViewById(R.id.btn_save_list);
		saveListBtn.setVisibility(recIdsArray.length > 1 ?
										View.VISIBLE :
										View.GONE);

		TextView saveInfotv = (TextView) findViewById(R.id.tv_save_list_info);
		saveInfotv.setVisibility(recIdsArray.length > 1 ?
				View.VISIBLE :
				View.GONE);
		
		ImageView imageView = (ImageView) findViewById(R.id.iv_theme_icon);
		BitmapLoaderAsyncTask loader = new  BitmapLoaderAsyncTask(context, null, false, true);
		loader.loadImageAsync(theme.getCoverImage(), imageView, null);
		
		TextView themeNameTv = (TextView) findViewById(R.id.tv_theme_name);
		themeNameTv.setText(theme.getName());
		
		TextView recipientsTv = (TextView) findViewById(R.id.tv_recipients_quantity);
		recipientsTv.setText(recIdsArray.length+" "+getString(R.string.str_recipients));	

		TextView additionalTv = (TextView) findViewById(R.id.tv_additional_info);
		additionalTv.setText(getAdditionalText());	
	}

	private CharSequence getAdditionalText() {
		int count = recIdsArray.length;		
		if(count == 1){
			return Integer.toString(count)+" "+getString(R.string.str_send_done_additional);
		}else{
			return Integer.toString(count)+" "+getString(R.string.str_send_done_additionals);			
		}
		
	}

	private void getExtras() {
		Intent intent = getIntent();
		recIdsArray = intent.getStringArrayExtra(Constants.EXTRA_ID);
		String themeId = intent.getStringExtra(Constants.EXTRA_THEME_ID);
		
		theme = dbHelper.getTheme(themeId);
	}
	
	public void onSaveGroupButtonClick(View v){
		final EditText editText = new EditText(context);
		AlertDialog.Builder builder = 	new AlertDialog.Builder(context);
		builder.setTitle(getString(R.string.str_save_group_title));
		builder.setMessage(getString(R.string.str_save_group_message));
		builder.setView(editText);
		builder.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (editText.getText().toString() != "")
					saveGroup(editText.getText().toString());
			}
		});
		builder.create().show();		
	}
	
	private void saveGroup(String groupName) {
		String groupId = UUID.randomUUID().toString();		
		dbHelper.createNewGroupRecipients(groupId, groupName, recIdsArray.length);
		
		for (String recId : recIdsArray){
			dbHelper.addRecipientToGroup(UUID.randomUUID().toString(), groupId, recId);
		}		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}		

	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
    	    case R.id.action_home:
    	    {
    	    	Intent intent = new Intent(this, MainActivity.class);
    	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	    	startActivity(intent);
    	    }
    			return true;
            default:
                  return super.onOptionsItemSelected(item);
        }
    }
	
}
