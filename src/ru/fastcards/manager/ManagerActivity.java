package ru.fastcards.manager;

import ru.fastcards.R;
import ru.fastcards.TrackedActivity;
import ru.fastcards.profile.MyEventsActivity;
import ru.fastcards.profile.SocialNetworkActivity;
import ru.fastcards.shop.StarsShopActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ManagerActivity extends TrackedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);
	}

	public void onProjectsClick(View v) {
		Intent intent = new Intent(this, ProjectsManagerActivity.class);
		startActivity(intent);
	}

	public void onAdressiesClick(View v) {
		Intent intent = new Intent(this, ContactsManagerActivity.class);
		startActivity(intent);
	}

	public void onMyEventsClick(View view) {
		startActivity(new Intent(this, MyEventsActivity.class));
	}

	public void onSocialNetworkClick(View view) {
		startActivity(new Intent(this, SocialNetworkActivity.class));
	}

	public void onMyPurseClick(View view) {
		startActivity(new Intent(this, StarsShopActivity.class));
	}

}
