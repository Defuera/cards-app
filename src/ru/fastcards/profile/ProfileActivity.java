package ru.fastcards.profile;

import ru.fastcards.PurseDialogFragment;
import ru.fastcards.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ProfileActivity extends ActionBarActivity {

	private static final String TAG="ProfileActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);		
	}

	public void onMyEventsClick(View view){
		startActivity(new Intent(this,MyEventsActivity.class));
	}

	public void onSocialNetworkClick(View view){
		startActivity(new Intent(this,SocialNetworkActivity.class));
	}
	
	public void onMyPurseClick(View view){
		new PurseDialogFragment().show(getSupportFragmentManager(), TAG);
	}
}
