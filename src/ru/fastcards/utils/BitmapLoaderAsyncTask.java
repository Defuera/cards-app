package ru.fastcards.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class BitmapLoaderAsyncTask {

	private Handler handler;
	private boolean isCrop;
	private boolean isStorable;

//	private Context context;
	ImageManager manager;
	private static final ExecutorService service = Executors.newFixedThreadPool(4);

	private static final String TAG = "BitmapLoaderAsyncTask";

	public static final Map<String, Bitmap> cache = new LinkedHashMap<String, Bitmap>() {
		private static final int MAX_ENTRIES = 50;

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	/**
	 * Class is dedicated to work with images, load it from network, dave to device and cache it.
	 * 
	 * @param context - app context
	 * @param callback pass callback if you need any callback after bitmal is loaded
	 * @param isCrop - pass true if you need image to be cropped to square
	 * @param isStorable - pass true if you want to save image to device
	 */
	public BitmapLoaderAsyncTask(Context context, Handler callback, boolean isCrop, boolean isStorable) {
		this.handler = callback;
		this.isCrop = isCrop;
		this.isStorable = isStorable;
//		this.context = context;
		this.manager = new ImageManager(context);
	}

	/**
	 * Loads bitmap from given url, binds to a given image view. While bitmap is loading displays given progressBar. If imageView is null bitmap will be loaded, but not binded. If progressBar is null
	 * it's ok. Url cannot be null of course.
	 * 
	 * @param url - url to load image from
	 * @param imageView - to bind loaded omage to
	 * @param progressBar - to display while image loading.
	 */
	public void loadImageAsync(String url, ImageView imageView, ProgressBar progressBar) {
		if (cache.containsKey(url)) {
			Log.d(TAG, "image retrieved from cache");
			if (progressBar != null)
				progressBar.setVisibility(View.GONE);
			synchronized (cache) {
				if (imageView != null)
					imageView.setImageBitmap(cache.get(url));
				imageView.setVisibility(View.VISIBLE);
				if (handler != null)
					sendCallback(url, cache.get(url));
			}

		} else {
			LoadTask loadTask = new LoadTask();
			loadTask.url = url;
			loadTask.imageView = imageView;
			loadTask.progressBar = progressBar;

			if (Utils.hasHoneycomb())
				startMultipleThreadLoader(loadTask);
			else
				loadTask.execute();
			// service.submit(loadTask);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void startMultipleThreadLoader(LoadTask loadTask) {
		loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
	}

	private class LoadTask extends AsyncTask<String, Void, Bitmap> {

		ImageView imageView;
		Context context;
		String url;
		ProgressBar progressBar;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (imageView != null)
				imageView.setVisibility(View.INVISIBLE);
			if (progressBar != null)
				progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			Bitmap bitmap = null;
			if (isStorable && url != null) {
				bitmap = loadBitmapFromStorage(url, imageView);
				// Log.d(TAG, "loaded from storage "+bitmap);
			}
			if (bitmap == null) {
				InputStream is;
				try {
					is = (new URL(url)).openStream();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				bitmap = BitmapFactory.decodeStream(is);
				if (isStorable) {
					String filename = manager.createFileNameFromUrl(url);
					manager.saveImageToInternalStorage(filename, bitmap, Bitmap.CompressFormat.PNG);
					Log.d(TAG, "saving to device " + filename + " url " + url);
				}
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			if (progressBar != null) {
				progressBar.setVisibility(View.GONE);
			}
			if (bitmap != null) {
				if (imageView != null) {
					bindBitmapToImageView(bitmap);
					imageView.setVisibility(View.VISIBLE);
				}
				if (handler != null)
					sendCallback(url, bitmap);

				Log.e(TAG, "Loading success");

			} else {
			}
		}

		private Bitmap loadBitmapFromStorage(String url, ImageView imageView) {
			String filename = manager.createFileNameFromUrl(url);
			Bitmap bitmap = manager.decodeBitmapFromFile(filename);
			// Log.d(TAG, "isOnStorageCache "+bitmap);
			return bitmap;
		}

		private void bindBitmapToImageView(Bitmap bitmap) {
			// Log.i(TAG, "bindBitmapToImageView "+bitmap);
			if (isCrop)
				bitmap = manager.cropBitmap(bitmap);
			synchronized (cache) {
				cache.put(url, bitmap);
			}
			imageView.setImageBitmap(bitmap);
		}

	}

	private void sendCallback(String url, Bitmap bitmap) {
		Message message = handler.obtainMessage();
		Object[] picture = {url, bitmap};
		message.obj = picture;
		handler.sendMessage(message);
	}
}
