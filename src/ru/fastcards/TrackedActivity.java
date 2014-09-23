package ru.fastcards;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * An example Activity using Google Analytics and EasyTracker.
 */
public class TrackedActivity extends ActionBarActivity {
  @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onStart() {
    super.onStart();
    // The rest of your onStart() code.
    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
  }

  @Override
  public void onStop() {
    super.onStop();
    // The rest of your onStop() code.
    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
  }
}